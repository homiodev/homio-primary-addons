package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.UICameraAction;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@Log4j2
@CameraBrandHandler(name = "DoorBird")
public class DoorBirdBrandHandler extends BaseOnvifCameraBrandHandler {

    public DoorBirdBrandHandler(BaseVideoCameraEntity cameraEntity) {
        super(cameraEntity);
    }

    // This handles the incoming http replies back from the camera.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg == null || ctx == null) {
            return;
        }
        try {
            String content = msg.toString();
            onvifCameraHandler.getLog().trace("HTTP Result back from camera is \t:{}:", content);
            if (content.contains("doorbell:H")) {
                setAttribute(CHANNEL_DOORBELL, OnOffType.ON);
            }
            if (content.contains("doorbell:L")) {
                setAttribute(CHANNEL_DOORBELL, OnOffType.OFF);
            }
            if (content.contains("motionsensor:L")) {
                onvifCameraHandler.motionDetected(false, CHANNEL_MOTION_ALARM);
            }
            if (content.contains("motionsensor:H")) {
                onvifCameraHandler.motionDetected(true, CHANNEL_MOTION_ALARM);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void pollCameraRunnable(OnvifCameraHandler onvifCameraHandler) {
        if (onvifCameraHandler.streamIsStopped("/bha-api/monitor.cgi?ring=doorbell,motionsensor")) {
            log.info("The alarm stream was not running for camera {}, re-starting it now",
                    onvifCameraHandler.getCameraEntity().getIp());
            onvifCameraHandler.sendHttpGET("/bha-api/monitor.cgi?ring=doorbell,motionsensor");
        }
    }

    @Override
    public void initialize(EntityContext entityContext) {
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

    @UICameraAction(name = CHANNEL_EXTERNAL_LIGHT, order = 200, icon = "fas fa-sun")
    public void externalLight(boolean on) {
        if (on) {
            onvifCameraHandler.sendHttpGET("/bha-api/light-on.cgi");
        }
    }

    @UICameraAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT2, order = 47, icon = "fas fa-bell")
    public void activateAlarmOutput2(boolean on) {
        if (on) {
            onvifCameraHandler.sendHttpGET("/bha-api/open-door.cgi?r=2");
        }
    }

    @UICameraAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT, order = 45, icon = "fas fa-bell")
    public void activateAlarmOutput(boolean on) {
        if (on) {
            onvifCameraHandler.sendHttpGET("/bha-api/open-door.cgi");
        }
    }
}
