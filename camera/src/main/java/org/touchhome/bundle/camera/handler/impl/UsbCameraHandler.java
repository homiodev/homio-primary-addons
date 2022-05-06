package org.touchhome.bundle.camera.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.entity.UsbCameraEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.handler.BaseCameraStreamServerHandler;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants;
import org.touchhome.bundle.camera.setting.FFMPEGInstallPathSetting;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class UsbCameraHandler extends BaseFFmpegCameraHandler<UsbCameraEntity> {

    private List<String> outputs = new ArrayList<>();
    private Ffmpeg ffmpegUsbStream;

    public UsbCameraHandler(UsbCameraEntity cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
    }

    @Override
    protected void initialize0(UsbCameraEntity cameraEntity) {
        String url = "video=\"" + this.cameraEntity.getIeeeAddress() + "\"";
        if (StringUtils.isNotEmpty(this.cameraEntity.getAudioSource())) {
            url += ":audio=\"" + this.cameraEntity.getAudioSource() + "\"";
        }
        Set<String> outputParams = new LinkedHashSet<>(this.cameraEntity.getStreamOptions());
        outputParams.add("-f tee");
        outputParams.add("-map 0:v");
        if (StringUtils.isNotEmpty(this.cameraEntity.getAudioSource())) {
            url += ":audio=\"" + this.cameraEntity.getAudioSource() + "\"";
            outputParams.add("-map 0:a");
        }

        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + this.cameraEntity.getStreamStartPort());
        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + (this.cameraEntity.getStreamStartPort() + 1));

        ffmpegUsbStream = new Ffmpeg("FFmpegUSB_UDP", "FFmpeg usb udp re streamer", this, log,
                IpCameraBindingConstants.FFmpegFormat.GENERAL, ffmpegLocation,
                "-loglevel warning " + (SystemUtils.IS_OS_LINUX ? "-f v4l2" : "-f dshow"), url,
                String.join(" ", outputParams),
                outputs.stream().map(o -> "[f=mpegts]udp://" + o + "?pkt_size=1316").collect(Collectors.joining("|")),
                "", "", null);
        ffmpegUsbStream.startConverting();

        super.initialize0(cameraEntity);
    }

    @Override
    protected void dispose0() {
        super.dispose0();
        ffmpegUsbStream.stopConverting();

    }

    @Override
    public void testOnline() {
        String ffmpegPath = entityContext.setting().getValue(FFMPEGInstallPathSetting.class, Paths.get("ffmpeg")).toString();
        FfmpegInputDeviceHardwareRepository repository = entityContext.getBean(FfmpegInputDeviceHardwareRepository.class);
        Set<String> aliveVideoDevices = repository.getVideoDevices(ffmpegPath);
        if (!aliveVideoDevices.contains(getCameraEntity().getIeeeAddress())) {
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
    protected BaseCameraStreamServerHandler createCameraStreamServerHandler() {
        return new UsbCameraStreamHandler(this);
    }

    @Override
    protected void streamServerStarted() {

    }

    @Override
    protected boolean hasAudioStream() {
        return super.hasAudioStream() || StringUtils.isNotEmpty(cameraEntity.getAudioSource());
    }

    private class UsbCameraStreamHandler extends BaseCameraStreamServerHandler<UsbCameraHandler> {

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
