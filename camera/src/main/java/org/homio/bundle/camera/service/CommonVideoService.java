package org.homio.bundle.camera.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.exception.ServerException;
import org.homio.bundle.api.model.ActionResponseModel;
import org.homio.bundle.api.state.State;
import org.homio.bundle.api.state.StringType;
import org.homio.bundle.api.ui.UI.Color;
import org.homio.bundle.api.video.BaseVideoService;
import org.homio.bundle.api.video.BaseVideoStreamServerHandler;
import org.homio.bundle.camera.entity.CommonVideoStreamEntity;
import org.homio.bundle.camera.rtsp.message.sdp.SdpMessage;
import org.homio.bundle.camera.scanner.RtspStreamScanner;

public class CommonVideoService extends BaseVideoService<CommonVideoStreamEntity> {

    private VideoSourceType videoSourceType;

    public CommonVideoService(EntityContext entityContext, CommonVideoStreamEntity entity) {
        super(entity, entityContext);
    }

    @Override
    public boolean testService() {
        testVideoOnline();
        return true;
    }

    @Override
    public void testVideoOnline() {
        if (getEntity().getIeeeAddress() == null) {
            throw new ServerException("Url must be not null");
        }

        videoSourceType = VideoSourceType.UNKNOWN;
        String ieeeAddress = getEntity().getIeeeAddress();
        if (ieeeAddress != null) {
            if (ieeeAddress.endsWith("m3u8")) {
                videoSourceType = VideoSourceType.HLS;
            } else if (ieeeAddress.startsWith("rtsp://")) {
                videoSourceType = VideoSourceType.RTSP;
            }
        }
    }

    @Override
    public String getRtspUri(String profile) {
        return getEntity().getIeeeAddress();
    }

    @Override
    public String getFFMPEGInputOptions(String profile) {
        return videoSourceType.ffmpegInputOptions;
    }

    @Override
    public Map<String, State> getAttributes() {
        Map<String, State> map = new HashMap<>(super.getAttributes());
        SdpMessage sdpMessage = RtspStreamScanner.rtspUrlToSdpMessage.get(getEntity().getIeeeAddress());
        if (sdpMessage != null) {
            map.put("RTSP Description Message", new StringType(sdpMessage.toString()));
        }
        return map;
    }

    @Override
    public void afterInitialize() {
        updateNotificationBlock();
    }

    @Override
    public void afterDispose() {
        updateNotificationBlock();
    }

    public void updateNotificationBlock() {
        entityContext.ui().addNotificationBlock(entityID, getEntity().getTitle(), "fas fa-film", "#02B05C", builder -> {
            builder.setStatus(getEntity().getSourceStatus());
            if (!getEntity().isStart()) {
                builder.addButtonInfo(StringUtils.defaultIfEmpty(getEntity().getSourceStatusMessage(), "video.not_started"), Color.RED, "fas fa-stop", null,
                    "fas fa-play", "Start", null, (entityContext, params) -> {
                        entityContext.save(getEntity().setStart(true));
                        return ActionResponseModel.success();
                    });
            } else {
                builder.setStatusMessage(getEntity().getSourceStatusMessage());
            }
        });
    }

    @Override
    protected BaseVideoStreamServerHandler createVideoStreamServerHandler() {
        return new RtspCameraStreamHandler(this);
    }

    @Override
    protected void streamServerStarted() {

    }

    @RequiredArgsConstructor
    public enum VideoSourceType {
        HLS(""),
        RTSP("-rtsp_transport tcp -timeout " + TimeUnit.SECONDS.toMicros(10)),
        UNKNOWN("");

        private final String ffmpegInputOptions;
    }

    private static class RtspCameraStreamHandler extends BaseVideoStreamServerHandler<CommonVideoService> {

        public RtspCameraStreamHandler(CommonVideoService service) {
            super(service);
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
