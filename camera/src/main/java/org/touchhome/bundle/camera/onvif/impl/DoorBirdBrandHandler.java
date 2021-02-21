package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.util.OnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.UICameraAction;

import java.util.ArrayList;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
public class DoorBirdBrandHandler extends OnvifCameraBrandHandler {

    public DoorBirdBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
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

    public ArrayList<String> getLowPriorityRequests() {
        return null;
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
