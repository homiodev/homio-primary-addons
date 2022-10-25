package org.touchhome.bundle.camera.onvif.brand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.video.VideoActionsContext;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.BaseBrandCameraHandler;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.ui.CameraActionBuilder;

@Log4j2
public abstract class BaseOnvifCameraBrandHandler extends ChannelDuplexHandler implements VideoActionsContext, BaseBrandCameraHandler {

  protected final OnvifCameraHandler onvifCameraHandler;
  protected final int nvrChannel;

  protected final String username;
  protected final String password;
  protected final String ip;
  @Getter
  protected final OnvifCameraEntity videoStreamEntity;
  @Getter
  protected final EntityContext entityContext;

  public BaseOnvifCameraBrandHandler(OnvifCameraHandler onvifCameraHandler) {
    this.onvifCameraHandler = onvifCameraHandler;
    this.nvrChannel = 0;
    this.username = null;
    this.password = null;
    this.ip = null;
    this.videoStreamEntity = null;
    this.entityContext = null;
  }

  public BaseOnvifCameraBrandHandler(OnvifCameraEntity entity) {
    this.videoStreamEntity = entity;
    this.onvifCameraHandler = videoStreamEntity.getVideoHandler();
    this.nvrChannel = videoStreamEntity.getNvrChannel();
    this.username = videoStreamEntity.getUser();
    this.password = videoStreamEntity.getPassword().asString();
    this.ip = videoStreamEntity.getIp();
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

  public void assembleActions(UIInputBuilder uiInputBuilder) {
    CameraActionBuilder.assembleActions(this, uiInputBuilder);
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
    request.headers().set(HttpHeaderNames.HOST, onvifCameraHandler.getVideoStreamEntity().getIp());
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

  public boolean isSupportOnvifEvents() {
    return false;
  }

  @Override
  public ChannelHandler asBootstrapHandler() {
    return this;
  }
}
