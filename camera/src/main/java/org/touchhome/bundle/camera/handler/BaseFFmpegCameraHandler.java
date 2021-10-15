package org.touchhome.bundle.camera.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.MimeTypeUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.*;
import org.touchhome.bundle.camera.entity.AbilityToStreamHLSOverFFmpeg;
import org.touchhome.bundle.camera.entity.BaseFFmpegStreamEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
public abstract class BaseFFmpegCameraHandler<T extends BaseFFmpegStreamEntity> extends BaseCameraHandler<T> implements Ffmpeg.FFmpegHandler {

    public Ffmpeg ffmpegHLS;
    @Getter
    protected boolean motionDetected = false;
    protected Ffmpeg ffmpegGIF;
    protected Ffmpeg ffmpegSnapshot;
    protected Ffmpeg ffmpegMjpeg;
    protected Ffmpeg ffmpegRecord = null;

    private String gifFilename = "ipcamera";
    private int gifRecordTime = 5;

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup serversLoopGroup = new NioEventLoopGroup();

    private String snapshotSource;
    private String snapshotInputOptions;
    private String mp4OutOptions;
    private String gifOutOptions;
    private String mgpegOutOptions;

    private FFMpegRtspAlarm ffMpegRtspAlarm = new FFMpegRtspAlarm();

    public BaseFFmpegCameraHandler(T cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
    }

    @Override
    protected void pollingCameraConnection() {
        startSnapshot();
    }

    @Override
    protected void pollCameraRunnable() {
        fireFfmpeg(ffmpegHLS, Ffmpeg::stopProcessIfNoKeepAlive);

        long timePassed = System.currentTimeMillis() - lastAnswerFromCamera;
        if (timePassed > 1200000) { // more than 2 min passed
            disposeAndSetStatus(Status.OFFLINE, "Passed more that 2 min without answer from camera");
        } else if (timePassed > 30000) {
            startSnapshot();
        }
    }

    @Override
    protected void initialize0() {
        this.snapshotSource = initSnapshotInput();
        this.snapshotInputOptions = getFFMPEGInputOptions() + " -threads 1 -skip_frame nokey -hide_banner -loglevel warning -an";
        this.mp4OutOptions = String.join(" ", cameraEntity.getMp4OutOptions());
        this.gifOutOptions = String.join(" ", cameraEntity.getGifOutOptions());
        this.mgpegOutOptions = String.join(" ", cameraEntity.getMjpegOutOptions());

        String rtspUri = getRtspUri(null);

        ffmpegMjpeg = new Ffmpeg("FFmpegMjpeg", "FFmpeg mjpeg", this, log,
                FFmpegFormat.MJPEG, ffmpegLocation,
                getFFMPEGInputOptions() + " -hide_banner -loglevel warning", rtspUri,
                mgpegOutOptions, "http://127.0.0.1:" + serverPort + "/ipcamera.jpg",
                cameraEntity.getUser(), cameraEntity.getPassword().asString(), null);
        setAttribute("FFMPEG_MJPEG", new StringType(String.join(" ", ffmpegMjpeg.getCommandArrayList())));

        ffmpegSnapshot = new Ffmpeg("FFmpegSnapshot", "FFmpeg snapshot", this, log,
                FFmpegFormat.SNAPSHOT, ffmpegLocation, snapshotInputOptions, rtspUri,
                cameraEntity.getSnapshotOutOptionsAsString(),
                "http://127.0.0.1:" + serverPort + "/snapshot.jpg",
                cameraEntity.getUser(), cameraEntity.getPassword().asString(), () -> {
        });
        setAttribute("FFMPEG_SNAPSHOT", new StringType(String.join(" ", ffmpegSnapshot.getCommandArrayList())));

        if (cameraEntity instanceof AbilityToStreamHLSOverFFmpeg) {
            ffmpegHLS = new Ffmpeg("FFmpegHLS", "FFmpeg HLS", this, log, FFmpegFormat.HLS, ffmpegLocation,
                    "-hide_banner -loglevel warning " + getFFMPEGInputOptions(), createHlsRtspUri(),
                    buildHlsOptions(), getFfmpegHLSOutputPath().resolve("ipcamera.m3u8").toString(),
                    cameraEntity.getUser(), cameraEntity.getPassword().asString(), () -> setAttribute(CHANNEL_START_STREAM, OnOffType.OFF));
            setAttribute("FFMPEG_HLS", new StringType(String.join(" ", ffmpegHLS.getCommandArrayList())));
        }

        String gifInputOptions = "-y -t " + gifRecordTime + " -hide_banner -loglevel warning " + getFFMPEGInputOptions();
        ffmpegGIF = new Ffmpeg("FFmpegGIF", "FFmpeg GIF", this, log, FFmpegFormat.GIF, ffmpegLocation, gifInputOptions, rtspUri,
                gifOutOptions, getFfmpegGifOutputPath().resolve(gifFilename + ".gif").toString(),
                cameraEntity.getUser(), cameraEntity.getPassword().asString(), null);
        setAttribute("FFMPEG_GIF", new StringType(String.join(" ", ffmpegGIF.getCommandArrayList())));

        startStreamServer();
    }

    protected String createHlsRtspUri() {
        return getRtspUri(null);
    }

    public abstract String getRtspUri(String profile);

    @Override
    protected void dispose0() {
        fireFfmpeg(ffmpegHLS, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegRecord, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegGIF, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegMjpeg, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegSnapshot, Ffmpeg::stopConverting);
        ffMpegRtspAlarm.stop();
        stopStreamServer();
    }

   /* @UICameraActionGetter(CHANNEL_START_STREAM)
    public OnOffType getHKSStreamState() {
        return OnOffType.of(this.ffmpegHLSStarted);
    }*/

    //@UICameraAction(name = CHANNEL_START_STREAM, icon = "fas fa-expand-arrows-alt")
    public void startStream(boolean on) {
        Ffmpeg localHLS;
        // this.ffmpegHLSStarted = on;
        if (on) {
            localHLS = ffmpegHLS;
            fireFfmpeg(localHLS, ffmpeg -> {
                ffmpeg.setKeepAlive(-1);// Now will run till manually stopped.
                if (ffmpeg.startConverting()) {
                    setAttribute(CHANNEL_START_STREAM, OnOffType.ON);
                }
            });
        } else {
            // Still runs but will be able to auto stop when the HLS stream is no longer used.
            fireFfmpeg(ffmpegHLS, ffmpeg -> ffmpeg.setKeepAlive(1));
        }
    }

    @Override
    public final void recordMp4(String fileName, @Nullable String profile, int secondsToRecord) {
        String inputOptions = getFFMPEGInputOptions(profile);
        inputOptions = "-y -t " + secondsToRecord + " -hide_banner -loglevel warning " + inputOptions;
        ffmpegRecord = new Ffmpeg("FFmpegRecordMP4", "FFmpeg record MP4", this, log, FFmpegFormat.RECORD, ffmpegLocation, inputOptions, getRtspUri(profile),
                mp4OutOptions, getFfmpegMP4OutputPath().resolve(fileName + ".mp4").toString(),
                cameraEntity.getUser(), cameraEntity.getPassword().asString(), null);
        fireFfmpeg(ffmpegRecord, Ffmpeg::startConverting);
    }

    @Override
    public final void recordGif(String fileName, @Nullable String profile, int secondsToRecord) {
        gifFilename = fileName;
        gifRecordTime = secondsToRecord;
        startGifRecord();
    }

    @Override
    public void setAttribute(String key, State state) {
        super.setAttribute(key, state);
        if (key.equals(CHANNEL_AUDIO_THRESHOLD)) {
            entityContext.updateDelayed(cameraEntity, e -> e.setAudioThreshold(state.intValue()));
        } else if (key.equals(CHANNEL_MOTION_THRESHOLD)) {
            entityContext.updateDelayed(cameraEntity, e -> e.setMotionThreshold(state.intValue()));
        }
    }

    @Override
    public void motionDetected(boolean on, String key) {
        if (on) {
            setAttribute(CHANNEL_LAST_MOTION_TYPE, new StringType(key));
        }
        setAttribute(key, OnOffType.of(on));
        setAttribute(MOTION_ALARM, OnOffType.of(on));
        motionDetected = on;
    }

    @Override
    public void audioDetected(boolean on) {
        setAttribute(CHANNEL_AUDIO_ALARM, OnOffType.of(on));
    }

    public void processSnapshot(byte[] incomingSnapshot) {
        log.debug("Gеt camera snapshot: <{}>", cameraEntity.getTitle());
        lockCurrentSnapshot.lock();
        try {
            latestSnapshot = incomingSnapshot;
            // fires ui that snapshot was updated
            entityContext.ui().updateItem(cameraEntity);
        } finally {
            lockCurrentSnapshot.unlock();
        }
    }

    @Override
    public void startSnapshot() {
        fireFfmpeg(ffmpegSnapshot, Ffmpeg::startConverting);
    }

    public void startMJPEGRecord() {
        fireFfmpeg(ffmpegMjpeg, Ffmpeg::startConverting);
    }

    public void startGifRecord() {
        fireFfmpeg(ffmpegGIF, Ffmpeg::startConverting);
    }

    public abstract String getFFMPEGInputOptions(@Nullable String profile);

    public String getFFMPEGInputOptions() {
        return getFFMPEGInputOptions(null);
    }

    public final Logger getLog() {
        return log;
    }

    @SneakyThrows
    private void stopStreamServer() {
        serversLoopGroup.shutdownGracefully().sync();
        serverBootstrap = null;
    }

    public final void startStreamServer() {
        if (serverBootstrap == null) {
            try {
                serversLoopGroup = new NioEventLoopGroup();
                serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(serversLoopGroup);
                serverBootstrap.channel(NioServerSocketChannel.class);
                // IP "0.0.0.0" will bind the server to all network connections//
                serverBootstrap.localAddress(new InetSocketAddress("0.0.0.0", serverPort));
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast("idleStateHandler",
                                new IdleStateHandler(0, 60, 0));
                        socketChannel.pipeline().addLast("HttpServerCodec", new HttpServerCodec());
                        socketChannel.pipeline().addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
                        socketChannel.pipeline().addLast("streamServerHandler", BaseFFmpegCameraHandler.this.createCameraStreamServerHandler());
                    }
                });
                ChannelFuture serverFuture = serverBootstrap.bind().sync();
                serverFuture.await(4000);
                log.info("File server for camera at {} has started on port {} for all NIC's.", cameraEntity, serverPort);
            } catch (Exception e) {
                disposeAndSetStatus(Status.ERROR, "Exception when starting server. Try changing the Server Port to another number.");
            }
            this.streamServerStarted();
        }
    }

    protected abstract BaseCameraStreamServerHandler createCameraStreamServerHandler();

    protected abstract void streamServerStarted();

    public void ffmpegError(String error) {
        this.updateStatus(Status.ERROR, error);
    }

    @UICameraActionGetter(CHANNEL_AUDIO_THRESHOLD)
    public DecimalType getAudioAlarmThreshold() {
        return new DecimalType(cameraEntity.getAudioThreshold());
    }

    @UICameraAction(name = CHANNEL_AUDIO_THRESHOLD, order = 120, icon = "fas fa-volume-up", type = UICameraAction.ActionType.Dimmer)
    public void setAudioThreshold(int threshold) {
        entityContext.updateDelayed(cameraEntity, e -> e.setAudioThreshold(threshold));
        setAudioAlarmThreshold(threshold);
    }

    @UICameraActionGetter(CHANNEL_MOTION_THRESHOLD)
    public DecimalType getMotionThreshold() {
        return new DecimalType(cameraEntity.getMotionThreshold());
    }

    @UICameraAction(name = CHANNEL_MOTION_THRESHOLD, order = 110, icon = "fas fa-expand-arrows-alt",
            type = UICameraAction.ActionType.Dimmer, max = 1000)
    public void setMotionThreshold(int threshold) {
        entityContext.updateDelayed(cameraEntity, e -> e.setMotionThreshold(threshold));
        setMotionAlarmThreshold(threshold);
    }

    protected void setAudioAlarmThreshold(int threshold) {
        setAttribute(CHANNEL_AUDIO_THRESHOLD, new StringType(threshold));
        if (threshold == 0) {
            audioDetected(false);
        }
    }

    protected void setMotionAlarmThreshold(int threshold) {
        setAttribute(CHANNEL_MOTION_THRESHOLD, new StringType(threshold));
        if (threshold == 0) {
            motionDetected(false, CHANNEL_FFMPEG_MOTION_ALARM);
        }
    }

    protected boolean isAudioAlarmHandlesByCamera() {
        return false;
    }

    protected boolean isMotionAlarmHandlesByCamera() {
        return false;
    }

    public void startOrAddMotionAlarmListener(String listener) {
        if (!isMotionAlarmHandlesByCamera()) {
            ffMpegRtspAlarm.addMotionAlarmListener(listener);
        }
    }

    public void removeMotionAlarmListener(String listener) {
        if (!isMotionAlarmHandlesByCamera()) {
            ffMpegRtspAlarm.removeMotionAlarmListener(listener);
        }
    }

    @SneakyThrows
    public byte[] recordGifSync(String profile, int secondsToRecord) {
        String output = getFfmpegGifOutputPath().resolve("tmp_" + System.currentTimeMillis() + ".gif").toString();
        return fireFfmpegSync(profile, output, "-y -t " + secondsToRecord + " -hide_banner -loglevel warning",
                gifOutOptions, secondsToRecord + 20);
    }

    public byte[] recordMp4Sync(String profile, int secondsToRecord) {
        String output = getFfmpegMP4OutputPath().resolve("tmp_" + System.currentTimeMillis() + ".mp4").toString();
        return fireFfmpegSync(profile, output, "-y -t " + secondsToRecord + " -hide_banner -loglevel warning",
                mp4OutOptions, secondsToRecord + 20);
    }

    public RawType recordImageSync(String profile) {
        String output = getFfmpegImageOutputPath().resolve("tmp_" + System.currentTimeMillis() + ".jpg").toString();
        byte[] imageBytes = fireFfmpegSync(profile, output, snapshotInputOptions, cameraEntity.getSnapshotOutOptionsAsString(), 20);
        latestSnapshot = imageBytes;
        return new RawType(imageBytes, MimeTypeUtils.IMAGE_JPEG_VALUE);
    }

    @SneakyThrows
    private byte[] fireFfmpegSync(String profile, String output, String inputArguments, String outOptions, int maxTimeout) {
        try {
            Files.createFile(Paths.get(output));
            entityContext.getBean(FfmpegInputDeviceHardwareRepository.class).fireFfmpeg(
                    ffmpegLocation,
                    inputArguments + " " + getFFMPEGInputOptions(profile),
                    snapshotSource,
                    outOptions + " " + output,
                    maxTimeout);
            Path path = Paths.get(output);
            return IOUtils.toByteArray(Files.newInputStream(path));
        } finally {
            try {
                Files.delete(Paths.get(output));
            } catch (IOException ex) {
                log.error("Unable to remove file: <{}>", output, ex);
            }
        }
    }

    private String initSnapshotInput() {
        String rtspUri = getRtspUri(null);
        if (!cameraEntity.getPassword().isEmpty() && !rtspUri.contains("@") && rtspUri.contains("rtsp")) {
            String credentials = cameraEntity.getUser() + ":" + cameraEntity.getPassword().asString() + "@";
            return rtspUri.substring(0, 7) + credentials + rtspUri.substring(7);
        }
        return rtspUri;
    }

    private String buildHlsOptions() {
        AbilityToStreamHLSOverFFmpeg hlsOptions = (AbilityToStreamHLSOverFFmpeg) cameraEntity;
        List<String> options = new ArrayList<>();
        options.add("-strict -2");
        options.add("-c:v " + hlsOptions.getVideoCodec()); // video codec
        options.add("-hls_flags delete_segments"); // remove old segments
        options.add("-hls_init_time 1"); // build first ts ASAP
        options.add("-hls_time 2"); // ~ 2sec per file ?
        options.add("-hls_list_size " + hlsOptions.getHlsListSize()); // how many files
        if (StringUtils.isNotEmpty(hlsOptions.getHlsScale())) {
            options.add("-vf scale=" + hlsOptions.getHlsScale()); // scale result video
        }
        if (hasAudioStream()) {
            options.add("-c:a " + hlsOptions.getAudioCodec());
            options.add("-ac 2"); // audio channels (stereo)
            options.add("-ab 32k"); // audio bitrate in Kb/s
            options.add("-ar 44100"); // audio sampling rate
        }
        options.addAll(hlsOptions.getExtraOptions());
        return String.join(" ", options);
    }

    protected boolean hasAudioStream() {
        return cameraEntity.isHasAudioStream();
    }

    protected boolean isRunning(Ffmpeg ffmpeg) {
        return ffmpeg != null && ffmpeg.getIsAlive();
    }

    @Override
    public String getEntityID() {
        return cameraEntityID;
    }

    private class FFMpegRtspAlarm {
        private Ffmpeg ffmpegRtspHelper = null;
        private Set<String> motionAlarmObservers = new HashSet<>();

        private int motionThreshold;
        private int audioThreshold;

        public void addMotionAlarmListener(String listener) {
            motionAlarmObservers.add(listener);
            runFFmpegRtspAlarmThread();
        }

        public void removeMotionAlarmListener(String listener) {
            motionAlarmObservers.remove(listener);
            if (motionAlarmObservers.isEmpty()) {
                stop();
            }
        }

        private boolean runFFmpegRtspAlarmThread() {
            T cameraEntity = BaseFFmpegCameraHandler.this.cameraEntity;
            String inputOptions = BaseFFmpegCameraHandler.this.getFFMPEGInputOptions();

            if (ffmpegRtspHelper != null) {
                // stop stream if threshold - 0
                if (cameraEntity.getAudioThreshold() == 0 && cameraEntity.getMotionThreshold() == 0) {
                    ffmpegRtspHelper.stopConverting();
                    return false;
                }
                // if values that involved in precious run same as new - just skip restarting
                if (ffmpegRtspHelper.getIsAlive() && motionThreshold == cameraEntity.getMotionThreshold() &&
                        audioThreshold == cameraEntity.getAudioThreshold()) {
                    return true;
                }
                ffmpegRtspHelper.stopConverting();
            }
            this.motionThreshold = cameraEntity.getMotionThreshold();
            this.audioThreshold = cameraEntity.getAudioThreshold();
            String input = StringUtils.defaultIfEmpty(BaseFFmpegCameraHandler.this.cameraEntity.getAlarmInputUrl(), getRtspUri(null));

            List<String> filterOptionsList = new ArrayList<>();
            filterOptionsList.add(this.audioThreshold > 0 ? "-af silencedetect=n=-" + audioThreshold + "dB:d=2" : "-an");
            if (this.motionThreshold > 0) {
                filterOptionsList.addAll(cameraEntity.getMotionOptions());
                filterOptionsList.add("-vf select='gte(scene," + (motionThreshold / 100F) + ")',metadata=print");
            } else {
                filterOptionsList.add("-vn");
            }
            ffmpegRtspHelper = new Ffmpeg("FFmpegRtspAlarm", "FFmpeg rtsp alarm",
                    BaseFFmpegCameraHandler.this, log, FFmpegFormat.RTSP_ALARMS, ffmpegLocation, inputOptions, input,
                    String.join(" ", filterOptionsList), "-f null -", BaseFFmpegCameraHandler.this.cameraEntity.getUser(),
                    BaseFFmpegCameraHandler.this.cameraEntity.getPassword().asString(), null);
            fireFfmpeg(ffmpegRtspHelper, Ffmpeg::startConverting);
            setAttribute("FFMPEG_RTSP_ALARM", new StringType(String.join(" ", ffmpegRtspHelper.getCommandArrayList())));
            return true;
        }

        public void stop() {
            fireFfmpeg(ffmpegRtspHelper, Ffmpeg::stopConverting);
        }
    }

    /*private void storeSnapshots() {
        int count = 0;
        // Need to lock as fifoSnapshotBuffer is not thread safe and new snapshots can be incoming.
        lockCurrentSnapshot.lock();
        try {
            for (byte[] foo : fifoSnapshotBuffer) {
                Path file = getFfmpegImageOutputPath().resolve("snapshot" + count + ".jpg");
                count++;
                try {
                    Files.write(file, foo);
                } catch (FileNotFoundException e) {
                    log.warn("FileNotFoundException {}", e.getMessage());
                } catch (IOException e) {
                    log.warn("IOException {}", e.getMessage());
                }
            }
        } finally {
            lockCurrentSnapshot.unlock();
        }
    }*/
}
