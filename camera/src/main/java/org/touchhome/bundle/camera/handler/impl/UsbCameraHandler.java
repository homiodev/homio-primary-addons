package org.touchhome.bundle.camera.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.entity.UsbCameraEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.handler.BaseCameraStreamServerHandler;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants;

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
    protected void initialize0() {
        String url = "video=\"" + cameraEntity.getIeeeAddress() + "\"";
        if (StringUtils.isNotEmpty(cameraEntity.getAudioSource())) {
            url += ":audio=\"" + cameraEntity.getAudioSource() + "\"";
        }
        Set<String> outputParams = new LinkedHashSet<>(cameraEntity.getStreamOptions());
        outputParams.add("-f tee");
        outputParams.add("-map 0:v");
        if (StringUtils.isNotEmpty(cameraEntity.getAudioSource())) {
            url += ":audio=\"" + cameraEntity.getAudioSource() + "\"";
            outputParams.add("-map 0:a");
        }

        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + cameraEntity.getStreamStartPort());
        outputs.add(TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + (cameraEntity.getStreamStartPort() + 1));

        ffmpegUsbStream = new Ffmpeg(this, log, IpCameraBindingConstants.FFmpegFormat.GENERAL, ffmpegLocation,
                "-loglevel warning " + (TouchHomeUtils.OS.isLinux() ? "-f v4l2" : "-f dshow"), url,
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
    protected String createHlsRtspUri() {
        return "udp://@" + outputs.get(1);
    }

    @Override
    protected String createRtspUri() {
        return "udp://@" + outputs.get(0);
    }

    @Override
    public String getFFMPEGInputOptions() {
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
        protected boolean handleHttpRequest(QueryStringDecoder queryStringDecoder, ChannelHandlerContext ctx) {
            return false;
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
