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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.*;
import org.touchhome.bundle.camera.entity.AbilityToStreamHLSOverFFmpeg;
import org.touchhome.bundle.camera.entity.BaseFFmpegStreamEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;
import org.touchhome.bundle.camera.ui.UICameraDimmerButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.touchhome.bundle.api.util.TouchHomeUtils.MACHINE_IP_ADDRESS;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
public abstract class BaseFFmpegCameraHandler<T extends BaseFFmpegStreamEntity> extends BaseCameraHandler<T> implements Ffmpeg.FFmpegHandler {

    public boolean ffmpegSnapshotGeneration = false;
    public Ffmpeg ffmpegHLS;
    protected String rtspUri;
    @Getter
    protected boolean motionDetected = false;
    protected Ffmpeg ffmpegGIF;
    protected Ffmpeg ffmpegSnapshot;
    protected Ffmpeg ffmpegMjpeg;
    protected Ffmpeg ffmpegRecord = null;
    protected Ffmpeg ffmpegRtspHelper = null;
    protected int snapCount;
    private boolean motionAlarmEnabled = false;
    private int motionThreshold;
    private boolean audioAlarmEnabled = false;
    private int audioThreshold;
    private Pair<Integer, Integer> startedRtspAlarmOptions;
    private String gifFilename = "ipcamera";
    private int gifRecordTime = 5;
    private String mp4Filename = "ipcamera";
    private int mp4RecordTime;
    private LinkedList<byte[]> fifoSnapshotBuffer = new LinkedList<>();

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup serversLoopGroup = new NioEventLoopGroup();

    private String snapshotSource;
    private String snapshotInputOptions;
    private String mp4OutOptions;
    private String gifOutOptions;
    private String mgpegOutOptions;

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
        this.rtspUri = createRtspUri();
        setAttribute("RTSP_URI", new StringType(this.rtspUri));
        this.snapshotSource = initSnapshotInput();
        this.snapshotInputOptions = getFFMPEGInputOptions() + " -threads 1 -skip_frame nokey -hide_banner -loglevel warning -an";
        this.audioThreshold = cameraEntity.getAudioThreshold();
        this.motionThreshold = cameraEntity.getMotionThreshold();
        this.mp4OutOptions = String.join(" ", cameraEntity.getMp4OutOptions());
        this.gifOutOptions = String.join(" ", cameraEntity.getGifOutOptions());
        this.mgpegOutOptions = String.join(" ", cameraEntity.getMjpegOutOptions());

        ffmpegMjpeg = new Ffmpeg(this, log, FFmpegFormat.MJPEG, ffmpegLocation,
                getFFMPEGInputOptions() + " -hide_banner -loglevel warning", rtspUri,
                mgpegOutOptions, "http://127.0.0.1:" + serverPort + "/ipcamera.jpg",
                cameraEntity.getUser(), cameraEntity.getPassword(), null);
        setAttribute("FFMPEG_MJPEG", new StringType(String.join(" ", ffmpegMjpeg.getCommandArrayList())));

        ffmpegSnapshot = new Ffmpeg(this, log, FFmpegFormat.SNAPSHOT, ffmpegLocation, snapshotInputOptions, rtspUri,
                cameraEntity.getSnapshotOutOptionsAsString(),
                "http://127.0.0.1:" + serverPort + "/snapshot.jpg",
                cameraEntity.getUser(), cameraEntity.getPassword(), () -> ffmpegSnapshotGeneration = false);
        setAttribute("FFMPEG_SNAPSHOT", new StringType(String.join(" ", ffmpegSnapshot.getCommandArrayList())));

        if (cameraEntity instanceof AbilityToStreamHLSOverFFmpeg) {
            ffmpegHLS = new Ffmpeg(this, log, FFmpegFormat.HLS, ffmpegLocation,
                    "-hide_banner -loglevel warning " + getFFMPEGInputOptions(), createHlsRtspUri(),
                    buildHlsOptions(), getFfmpegHLSOutputPath().resolve("ipcamera.m3u8").toString(),
                    cameraEntity.getUser(), cameraEntity.getPassword(), () -> setAttribute(CHANNEL_START_STREAM, OnOffType.OFF));
            setAttribute("FFMPEG_HLS", new StringType(String.join(" ", ffmpegHLS.getCommandArrayList())));
        }

        if (cameraEntity.getGifPreroll() > 0) {
            ffmpegGIF = new Ffmpeg(this, log, FFmpegFormat.GIF, ffmpegLocation,
                    "-y -r 1 -hide_banner -loglevel warning", getFfmpegGifOutputPath().resolve("snapshot%d.jpg").toString(),
                    "-frames:v " + (cameraEntity.getGifPreroll() + gifRecordTime) + " "
                            + cameraEntity.getGifOutOptions(),
                    getFfmpegGifOutputPath().resolve(gifFilename + ".gif").toString(), cameraEntity.getUser(),
                    cameraEntity.getPassword(), null);
        } else {
            String inputOptions = "-y -t " + gifRecordTime + " -hide_banner -loglevel warning " + getFFMPEGInputOptions();
            ffmpegGIF = new Ffmpeg(this, log, FFmpegFormat.GIF, ffmpegLocation, inputOptions, rtspUri,
                    gifOutOptions, getFfmpegGifOutputPath().resolve(gifFilename + ".gif").toString(),
                    cameraEntity.getUser(), cameraEntity.getPassword(), null);
        }
        setAttribute("FFMPEG_GIF", new StringType(String.join(" ", ffmpegGIF.getCommandArrayList())));
        startStreamServer();
    }

    protected String createHlsRtspUri() {
        return createRtspUri();
    }

    protected abstract String createRtspUri();

    @Override
    protected void dispose0() {
        fireFfmpeg(ffmpegHLS, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegRecord, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegGIF, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegRtspHelper, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegMjpeg, Ffmpeg::stopConverting);
        fireFfmpeg(ffmpegSnapshot, Ffmpeg::stopConverting);
        stopStreamServer();
    }

   /* @UICameraActionGetter(CHANNEL_START_STREAM)
    public OnOffType getHKSStreamState() {
        return OnOffType.valueOf(this.ffmpegHLSStarted);
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
    public final void recordMp4(String fileName, int secondsToRecord) {
        mp4Filename = fileName;
        mp4RecordTime = secondsToRecord;
        startMP4Record();
    }

    @Override
    public final void recordGif(String fileName, int secondsToRecord) {
        gifFilename = fileName;
        gifRecordTime = secondsToRecord;
        if (cameraEntity.getGifPreroll() > 0) {
            snapCount = secondsToRecord;
        } else {
            startGifRecord();
        }
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
        setAttribute(key, OnOffType.valueOf(on));
        setAttribute(MOTION_ALARM, OnOffType.valueOf(on));
        motionDetected = on;
    }

    @Override
    public void audioDetected(boolean on) {
        setAttribute(CHANNEL_AUDIO_ALARM, OnOffType.valueOf(on));
    }

    public void processSnapshot(byte[] incomingSnapshot) {
        log.info("Got new snapshot for camera: <{}>", cameraEntity.getTitle());
        lockCurrentSnapshot.lock();
        try {
            latestSnapshot = incomingSnapshot;
            // fire ui that snapshot was updated
            entityContext.ui().updateItem(cameraEntity);

            if (cameraEntity.getGifPreroll() > 0) {
                fifoSnapshotBuffer.add(incomingSnapshot);
                if (fifoSnapshotBuffer.size() > (cameraEntity.getGifPreroll() + gifRecordTime)) {
                    fifoSnapshotBuffer.removeFirst();
                }
            }
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

    public void startMP4Record() {
        String inputOptions = getFFMPEGInputOptions();
        inputOptions = "-y -t " + mp4RecordTime + " -hide_banner -loglevel warning " + inputOptions;
        ffmpegRecord = new Ffmpeg(this, log, FFmpegFormat.RECORD, ffmpegLocation, inputOptions, rtspUri,
                mp4OutOptions, getFfmpegMP4OutputPath().resolve(mp4Filename + ".mp4").toString(),
                cameraEntity.getUser(), cameraEntity.getPassword(), null);
        fireFfmpeg(ffmpegRecord, Ffmpeg::startConverting);
    }

    public void startGifRecord() {
        if (cameraEntity.getGifPreroll() > 0) {
            storeSnapshots();
        }
        fireFfmpeg(ffmpegGIF, Ffmpeg::startConverting);
    }

    public boolean startRtspAlarms() {
        String inputOptions = getFFMPEGInputOptions();

        if (ffmpegRtspHelper != null) { // mean rtsp alarm was fired
            if (!audioAlarmEnabled && !motionAlarmEnabled) {
                ffmpegRtspHelper.stopConverting();
                return false;
            }
            // if values that involved in precious run same as new - just skip restarting
            if (ffmpegRtspHelper.getIsAlive() && motionThreshold == this.startedRtspAlarmOptions.getKey() &&
                    audioThreshold == this.startedRtspAlarmOptions.getValue()) {
                return true;
            }
            ffmpegRtspHelper.stopConverting();
        }
        this.startedRtspAlarmOptions = Pair.of(this.motionThreshold, this.audioThreshold);
        String input = StringUtils.defaultIfEmpty(cameraEntity.getAlarmInputUrl(), rtspUri);

        List<String> filterOptionsList = new ArrayList<>();
        filterOptionsList.add(audioAlarmEnabled ? "-af silencedetect=n=-" + audioThreshold + "dB:d=2" : "-an");
        if (motionAlarmEnabled) {
            filterOptionsList.addAll(cameraEntity.getMotionOptions());
            filterOptionsList.add("-vf select='gte(scene," + (motionThreshold / 1000F) + ")',metadata=print");
        } else {
            filterOptionsList.add("-vn");
        }
        ffmpegRtspHelper = new Ffmpeg(this, log, FFmpegFormat.RTSP_ALARMS, ffmpegLocation, inputOptions, input,
                String.join(" ", filterOptionsList), "-f null -", cameraEntity.getUser(), cameraEntity.getPassword(), null);
        fireFfmpeg(ffmpegRtspHelper, Ffmpeg::startConverting);
        setAttribute("FFMPEG_RTSP_ALARM", new StringType(String.join(" ", ffmpegRtspHelper.getCommandArrayList())));
        return true;
    }

    private void storeSnapshots() {
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
    }

    public abstract String getFFMPEGInputOptions();

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
                setAttribute(CHANNEL_MJPEG_URL, new StringType("http://" + MACHINE_IP_ADDRESS + ":" + serverPort + "/ipcamera.mjpeg"));
                setAttribute(CHANNEL_HLS_URL, new StringType("http://" + MACHINE_IP_ADDRESS + ":" + serverPort + "/ipcamera.m3u8"));
                setAttribute(CHANNEL_IMAGE_URL, new StringType("http://" + MACHINE_IP_ADDRESS + ":" + serverPort + "/ipcamera.jpg"));
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

    @UICameraAction(name = CHANNEL_AUDIO_THRESHOLD, order = 120, icon = "fas fa-volume-up",
            type = UICameraAction.ActionType.Dimmer)
    @UICameraDimmerButton(name = "ON", icon = "fas fa-volume-down")
    @UICameraDimmerButton(name = "OFF", icon = "fas fa-volume-off")
    public boolean setAudioAlarmThreshold(String command) {
        command = command.toLowerCase();
        if (this.isAudioAlarmHandlesByCamera()) {
            if ("off".equals(command) || "0".equals(command) || "false".equals(command)) {
                setAudioAlarmThreshold(0);
            } else if ("on".equals(command) || "true".equals(command)) {
                setAudioAlarmThreshold(cameraEntity.getAudioThreshold());
            } else {
                setAudioAlarmThreshold(Integer.parseInt(command));
            }
            return true;
        } else {
            audioAlarmEnabled = !"OFF".equals(command) && !"0".equals(command);
            if (audioAlarmEnabled) {
                if (!"ON".equals(command)) {
                    audioThreshold = Integer.parseInt(command);
                    setAttribute(CHANNEL_AUDIO_THRESHOLD, new StringType(audioThreshold + ""));
                }
            } else {
                audioDetected(false);
            }
            return startRtspAlarms();
        }
    }

    protected void setAudioAlarmThreshold(int audioThreshold) {
        throw new RuntimeException("Must be implemented in subclass");
    }

    protected void setMotionAlarmThreshold(int motionThreshold) {
        throw new RuntimeException("Must be implemented in subclass");
    }

    protected boolean isAudioAlarmHandlesByCamera() {
        return false;
    }

    protected boolean isMotionAlarmHandlesByCamera() {
        return false;
    }

    @UICameraActionGetter(CHANNEL_MOTION_THRESHOLD)
    public DecimalType getMotionThreshold() {
        return new DecimalType(cameraEntity.getMotionThreshold());
    }

    @UICameraAction(name = CHANNEL_MOTION_THRESHOLD, order = 110, icon = "fas fa-expand-arrows-alt",
            type = UICameraAction.ActionType.Dimmer, max = 1000)
    @UICameraDimmerButton(name = "ON", icon = "fas fa-power-off")
    @UICameraDimmerButton(name = "OFF", icon = "fas fa-power-on")
    public boolean setMotionThreshold(String command) {
        command = command.toLowerCase();
        if (this.isMotionAlarmHandlesByCamera()) {
            if ("off".equals(command) || "0".equals(command) || "false".equals(command)) {
                setMotionAlarmThreshold(0);
            } else if ("on".equals(command) || "true".equals(command)) {
                setMotionAlarmThreshold(cameraEntity.getAudioThreshold());
            } else {
                setMotionAlarmThreshold(Integer.parseInt(command));
            }
            return true;
        } else {
            motionAlarmEnabled = !"OFF".equals(command) && !"0".equals(command);
            if (motionAlarmEnabled) {
                if (!"ON".equals(command)) {
                    motionThreshold = Integer.parseInt(command);
                    setAttribute(CHANNEL_MOTION_THRESHOLD, new StringType(command));
                } else if (motionThreshold == 0) {
                    motionAlarmEnabled = false;
                }
            } else {
                motionDetected(false, CHANNEL_FFMPEG_MOTION_ALARM);
            }
            return startRtspAlarms();
        }
    }

    public byte[] recordGifSync(int time) {
        String output = getFfmpegGifOutputPath().resolve("tmp_" + System.currentTimeMillis() + ".gif").toString();
        return fireFfmpegSync(output, "-y -t " + time + " -hide_banner -loglevel warning",
                gifOutOptions, time + 20);
    }

    public byte[] recordMp4Sync(int time) {
        String output = getFfmpegMP4OutputPath().resolve("tmp_" + System.currentTimeMillis() + ".mp4").toString();
        return fireFfmpegSync(output, "-y -t " + time + " -hide_banner -loglevel warning",
                mp4OutOptions, time + 20);
    }

    public RawType recordImageSync(String profile) {
        String output = getFfmpegImageOutputPath().resolve("tmp_" + System.currentTimeMillis() + ".jpg").toString();
        byte[] imageBytes = fireFfmpegSync(output, snapshotInputOptions, cameraEntity.getSnapshotOutOptionsAsString(), 20);
        latestSnapshot = imageBytes;
        return new RawType(imageBytes, "image/jpeg");
    }

    @SneakyThrows
    private byte[] fireFfmpegSync(String output, String inputArguments, String outOptions, int maxTimeout) {
        try {
            entityContext.getBean(FfmpegInputDeviceHardwareRepository.class).fireFfmpeg(
                    ffmpegLocation,
                    inputArguments + " " + getFFMPEGInputOptions(),
                    snapshotSource,
                    outOptions + " " + outOptions + " " + output,
                    maxTimeout);
            Path path = Paths.get(output);
            return Files.exists(path) ? IOUtils.toByteArray(Files.newInputStream(path)) : null;
        } finally {
            try {
                Files.delete(Paths.get(output));
            } catch (IOException ex) {
                log.error("Unable to remove file: <{}>", output, ex);
            }
        }
    }

    private String initSnapshotInput() {
        if (!cameraEntity.getPassword().isEmpty() && !rtspUri.contains("@") && rtspUri.contains("rtsp")) {
            String credentials = cameraEntity.getUser() + ":" + cameraEntity.getPassword() + "@";
            return rtspUri.substring(0, 7) + credentials + rtspUri.substring(7);
        }
        return rtspUri;
    }

    private String buildHlsOptions() {
        AbilityToStreamHLSOverFFmpeg hlsOptions = (AbilityToStreamHLSOverFFmpeg) cameraEntity;
        List<String> options = new ArrayList<>();
        options.add("-strict -2");
        options.add("-c:v " + hlsOptions.getHlsVideoCodec()); // video codec
        options.add("-hls_flags delete_segments"); // remove old segments
        options.add("-hls_init_time 1"); // build first ts ASAP
        options.add("-hls_time 2"); // ~ 2sec per file ?
        options.add("-hls_list_size " + hlsOptions.getHlsListSize()); // how many files
        if (StringUtils.isNotEmpty(hlsOptions.getHlsScale())) {
            options.add("-vf scale=" + hlsOptions.getHlsScale()); // scale result video
        }
        if (hasAudioStream()) {
            options.add("-c:a " + hlsOptions.getHlsAudioCodec());
            options.add("-ac 2"); // audio channels (stereo)
            options.add("-ab 32k"); // audio bitrate in Kb/s
            options.add("-ar 44100"); // audio sampling rate
        }
        options.addAll(hlsOptions.getHlsExtraOptions());
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
}
