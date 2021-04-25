package org.touchhome.bundle.camera.onvif;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.action.impl.StatefulContextMenuAction;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.ui.CameraActionBuilder;
import org.touchhome.bundle.camera.ui.CameraActionsContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public abstract class BaseOnvifCameraBrandHandler extends ChannelDuplexHandler implements CameraActionsContext {
    private static Map<String, List<StatefulContextMenuAction>> actions = new HashMap<>();

    protected final OnvifCameraHandler onvifCameraHandler;
    protected final int nvrChannel;

    protected final String username;
    protected final String password;
    protected final String ip;
    @Getter
    protected final OnvifCameraEntity cameraEntity;
    @Getter
    protected final EntityContext entityContext;

    public BaseOnvifCameraBrandHandler(OnvifCameraHandler onvifCameraHandler) {
        this.onvifCameraHandler = onvifCameraHandler;
        this.nvrChannel = 0;
        this.username = null;
        this.password = null;
        this.ip = null;
        this.cameraEntity = null;
        this.entityContext = null;
    }

    public BaseOnvifCameraBrandHandler(OnvifCameraEntity cameraEntity) {
        this.cameraEntity = cameraEntity;
        this.onvifCameraHandler = cameraEntity.getCameraHandler();
        this.nvrChannel = cameraEntity.getNvrChannel();
        this.username = cameraEntity.getUser();
        this.password = cameraEntity.getPassword();
        this.ip = cameraEntity.getIp();
        this.entityContext = onvifCameraHandler.getEntityContext();
    }

    @Override
    public boolean isSharable() {
        return true;
    }

    public State getAttribute(String name) {
        return onvifCameraHandler.getAttributes().getOrDefault(name, null);
    }

    public int boolToInt(boolean on) {
        return on ? 1 : 0;
    }

    public List<StatefulContextMenuAction> getCameraActions() {
        return actions.computeIfAbsent(getClass().getSimpleName(), key -> CameraActionBuilder.assemble(this));
    }

    protected void setAttribute(String key, State state) {
        onvifCameraHandler.setAttribute(key, state);
    }

    protected void setAttributeRequest(String key, State state) {
        onvifCameraHandler.setAttributeRequest(key, state);
    }

    protected State getAttributeRequest(String key) {
        return onvifCameraHandler.getRequestAttributes().get(key);
    }

    public void pollCameraRunnable(OnvifCameraHandler onvifCameraHandler) {

    }

    public void initialize(EntityContext entityContext) {

    }

    public void runOncePerMinute(EntityContext entityContext) {

    }

    public String getUrlToKeepOpenForIdleStateEvent() {
        return "";
    }

    public void handleSetURL(ChannelPipeline pipeline, String httpRequestURL) {

    }

    protected FullHttpRequest buildFullHttpRequest(String httpPutURL, String xml, HttpMethod httpMethod, MediaType mediaType) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, new HttpMethod(httpMethod.name()), httpPutURL);
        request.headers().set(HttpHeaderNames.HOST, onvifCameraHandler.getCameraEntity().getIp());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, mediaType.toString());
        ByteBuf bbuf = Unpooled.copiedBuffer(xml, StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
        request.content().clear().writeBytes(bbuf);
        return request;
    }

    public String updateURL(String url) {
        return url;
    }
}
