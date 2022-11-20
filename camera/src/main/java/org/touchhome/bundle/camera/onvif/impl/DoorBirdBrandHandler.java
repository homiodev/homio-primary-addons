package org.touchhome.bundle.camera.onvif.impl;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ACTIVATE_ALARM_OUTPUT;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ACTIVATE_ALARM_OUTPUT2;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_DOORBELL;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_EXTERNAL_LIGHT;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_MOTION_ALARM;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.video.ui.UIVideoAction;
import org.touchhome.bundle.camera.onvif.brand.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.service.OnvifCameraService;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@Log4j2
@CameraBrandHandler(name = "DoorBird")
public class DoorBirdBrandHandler extends BaseOnvifCameraBrandHandler {

  public DoorBirdBrandHandler(OnvifCameraService service) {
    super(service);
  }

  // This handles the incoming http replies back from the camera.
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg == null || ctx == null) {
      return;
    }
    OnvifCameraService service = getService();
    try {
      String content = msg.toString();
      log.debug("[{}]: HTTP Result back from camera is \t:{}:", entityID, content);
      if (content.contains("doorbell:H")) {
        setAttribute(CHANNEL_DOORBELL, OnOffType.ON);
      }
      if (content.contains("doorbell:L")) {
        setAttribute(CHANNEL_DOORBELL, OnOffType.OFF);
      }
      if (content.contains("motionsensor:L")) {
        service.motionDetected(false, CHANNEL_MOTION_ALARM);
      }
      if (content.contains("motionsensor:H")) {
        service.motionDetected(true, CHANNEL_MOTION_ALARM);
      }
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void pollCameraRunnable() {
    OnvifCameraService service = getService();
    if (service.streamIsStopped("/bha-api/monitor.cgi?ring=doorbell,motionsensor")) {
      log.info("[{}]: The alarm stream was not running for camera {}, re-starting it now",
          entityID, getEntity().getIp());
      service.sendHttpGET("/bha-api/monitor.cgi?ring=doorbell,motionsensor");
    }
  }

  @Override
  public void initialize(EntityContext entityContext) {
    OnvifCameraService onvifCameraHandler = getService();
    if (StringUtils.isEmpty(onvifCameraHandler.getMjpegUri())) {
      onvifCameraHandler.setMjpegUri("/bha-api/video.cgi");
    }
    if (StringUtils.isEmpty(onvifCameraHandler.getSnapshotUri())) {
      onvifCameraHandler.setSnapshotUri("/bha-api/image.cgi");
    }
  }

  @Override
  public String getUrlToKeepOpenForIdleStateEvent() {
    return "/bha-api/monitor.cgi?ring=doorbell,motionsensor";
  }

  @UIVideoAction(name = CHANNEL_EXTERNAL_LIGHT, order = 200, icon = "fas fa-sun")
  public void externalLight(boolean on) {
    if (on) {
      getService().sendHttpGET("/bha-api/light-on.cgi");
    }
  }

  @UIVideoAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT2, order = 47, icon = "fas fa-bell")
  public void activateAlarmOutput2(boolean on) {
    if (on) {
      getService().sendHttpGET("/bha-api/open-door.cgi?r=2");
    }
  }

  @UIVideoAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT, order = 45, icon = "fas fa-bell")
  public void activateAlarmOutput(boolean on) {
    if (on) {
      getService().sendHttpGET("/bha-api/open-door.cgi");
    }
  }
}
