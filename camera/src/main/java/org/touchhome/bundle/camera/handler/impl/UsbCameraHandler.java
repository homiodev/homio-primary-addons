package org.touchhome.bundle.camera.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamHandler;
import org.touchhome.bundle.api.video.BaseVideoStreamServerHandler;
import org.touchhome.bundle.api.video.ffmpeg.FFMPEG;
import org.touchhome.bundle.api.video.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.entity.UsbCameraEntity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.touchhome.bundle.api.video.ffmpeg.FFMPEGFormat.GENERAL;

@Log4j2
public class UsbCameraHandler extends BaseFFMPEGVideoStreamHandler<UsbCameraEntity, UsbCameraHandler> {

    private List<String> outputs = new ArrayList<>();
    private FFMPEG ffmpegUsbStream;

    public UsbCameraHandler(UsbCameraEntity cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
    }

    @Override
    protected void initialize0() {
        String url = "video=\"" + videoStreamEntity.getIeeeAddress() + "\"";
        if (StringUtils.isNotEmpty(videoStreamEntity.getAudioSource())) {
            url += ":audio=\"" + videoStreamEntity.getAudioSource() + "\"";
        }
        Set<String> outputParams = new LinkedHashSet<>(videoStreamEntity.getStreamOptions());
        outputParams.add("-f tee");
        outputParams.add("-map 0:v");
        if (StringUtils.isNotEmpty(videoStreamEntity.getAudioSource())) {
            url += ":audio=\"" + videoStreamEntity.getAudioSource() + "\"";
            outputParams.add("-map 0:a");
        }

        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + videoStreamEntity.getStreamStartPort());
        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + (videoStreamEntity.getStreamStartPort() + 1));

        ffmpegUsbStream = new FFMPEG("FFmpegUSB_UDP", "FFmpeg usb udp re streamer", this, log,
                GENERAL, ffmpegLocation,
                "-loglevel warning " + (SystemUtils.IS_OS_LINUX ? "-f v4l2" : "-f dshow"), url,
                String.join(" ", outputParams),
                outputs.stream().map(o -> "[f=mpegts]udp://" + o + "?pkt_size=1316").collect(Collectors.joining("|")),
                "", "", null);
        ffmpegUsbStream.startConverting();

        super.initialize0();
    }

    @Override
    protected void dispose0() {
        super.dispose0();
        ffmpegUsbStream.stopConverting();

    }

    @Override
    public void testOnline() {
        String ffmpegPath = entityContext.setting().getFFMPEGInstallPath().toString();
        FfmpegInputDeviceHardwareRepository repository = entityContext.getBean(FfmpegInputDeviceHardwareRepository.class);
        Set<String> aliveVideoDevices = repository.getVideoDevices(ffmpegPath);
        if (!aliveVideoDevices.contains(getVideoStreamEntity().getIeeeAddress())) {
            throw new RuntimeException("Camera not available");
        }
    }

    @Override
    protected String createHlsRtspUri() {
        return "udp://@" + outputs.get(1);
    }

    @Override
    public String getRtspUri(String profile) {
        return "udp://@" + outputs.get(0);
    }

    @Override
    public String getFFMPEGInputOptions(String profile) {
        return "";
    }

    @Override
    protected BaseVideoStreamServerHandler createVideoStreamServerHandler() {
        return new UsbCameraStreamHandler(this);
    }

    @Override
    protected void streamServerStarted() {

    }

    @Override
    protected boolean hasAudioStream() {
        return super.hasAudioStream() || StringUtils.isNotEmpty(videoStreamEntity.getAudioSource());
    }

    private class UsbCameraStreamHandler extends BaseVideoStreamServerHandler<UsbCameraHandler> {

        public UsbCameraStreamHandler(UsbCameraHandler usbCameraHandler) {
            super(usbCameraHandler);
        }

        @Override
        protected void handleLastHttpContent(byte[] incomingJpeg) {
        }

        @Override
        protected boolean streamServerReceivedPostHandler(HttpRequest httpRequest) {
            return false;
        }

        @Override
        protected void handlerChildRemoved(ChannelHandlerContext ctx) {
        }
    }
}
