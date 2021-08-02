package org.touchhome.bundle.camera.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.camera.CameraCoordinator;
import org.touchhome.bundle.camera.entity.BaseFFmpegStreamEntity;
import org.touchhome.bundle.camera.handler.BaseCameraStreamServerHandler;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.rtsp.message.sdp.SdpMessage;
import org.touchhome.bundle.camera.scanner.RtspStreamScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
public class RtspStreamHandler extends BaseFFmpegCameraHandler<BaseFFmpegStreamEntity> {

    private final RtspStreamScanner rtspStreamScanner;

    public RtspStreamHandler(BaseFFmpegStreamEntity cameraEntity, EntityContext entityContext) {
        super(cameraEntity, entityContext);
        rtspStreamScanner = entityContext.getBean(RtspStreamScanner.class);
    }

    @Override
    public void testOnline() {

    }

    @Override
    protected String createRtspUri() {
        return cameraEntity.getIeeeAddress();
    }

    @Override
    public String getFFMPEGInputOptions() {
        return "-rtsp_transport tcp -stimeout " + TimeUnit.SECONDS.toMicros(10);
    }

    @Override
    protected BaseCameraStreamServerHandler createCameraStreamServerHandler() {
        return new RtspCameraStreamHandler(this);
    }

    @Override
    protected void streamServerStarted() {

    }

    @Override
    public Map<String, State> getAttributes() {
        Map<String, State> map = new HashMap<>(super.getAttributes());
        SdpMessage sdpMessage = CameraCoordinator.getSdpMessage(cameraEntity.getIeeeAddress());
        if (sdpMessage != null) {
            map.put("RTSP Description Message", new StringType(sdpMessage.toString()));
        }
        return map;
    }

    private class RtspCameraStreamHandler extends BaseCameraStreamServerHandler<RtspStreamHandler> {

        public RtspCameraStreamHandler(RtspStreamHandler rtspStreamHandler) {
            super(rtspStreamHandler);
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
