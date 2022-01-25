package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.BrandCameraHasAudioAlarm;
import org.touchhome.bundle.camera.onvif.BrandCameraHasMotionAlarm;
import org.touchhome.bundle.camera.onvif.util.Helper;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@CameraBrandHandler(name = "Foscam")
public class FoscamBrandHandler extends BaseOnvifCameraBrandHandler implements BrandCameraHasAudioAlarm, BrandCameraHasMotionAlarm {
    private static final String CG = "/cgi-bin/CGIProxy.fcgi?cmd=";
    private int audioThreshold;

    public FoscamBrandHandler(BaseVideoCameraEntity cameraEntity) {
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
            ////////////// Motion Alarm //////////////
            if (content.contains("<motionDetectAlarm>")) {
                if (content.contains("<motionDetectAlarm>0</motionDetectAlarm>")) {
                    setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.OFF);
                } else if (content.contains("<motionDetectAlarm>1</motionDetectAlarm>")) { // Enabled but no alarm
                    setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.ON);
                    onvifCameraHandler.motionDetected(false, CHANNEL_MOTION_ALARM);
                } else if (content.contains("<motionDetectAlarm>2</motionDetectAlarm>")) {// Enabled, alarm on
                    setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.ON);
                    onvifCameraHandler.motionDetected(true, CHANNEL_MOTION_ALARM);
                }
            }

            ////////////// Sound Alarm //////////////
            if (content.contains("<soundAlarm>0</soundAlarm>")) {
                setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.OFF);
                setAttribute(CHANNEL_AUDIO_ALARM, OnOffType.OFF);
            }
            if (content.contains("<soundAlarm>1</soundAlarm>")) {
                setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.ON);
                onvifCameraHandler.audioDetected(false);
            }
            if (content.contains("<soundAlarm>2</soundAlarm>")) {
                setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.ON);
                onvifCameraHandler.audioDetected(true);
            }

            ////////////// Sound Threshold //////////////
            if (content.contains("<sensitivity>0</sensitivity>")) {
                setAttribute(CHANNEL_AUDIO_THRESHOLD, DecimalType.ZERO);
            }
            if (content.contains("<sensitivity>1</sensitivity>")) {
                setAttribute(CHANNEL_AUDIO_THRESHOLD, new DecimalType(50));
            }
            if (content.contains("<sensitivity>2</sensitivity>")) {
                setAttribute(CHANNEL_AUDIO_THRESHOLD, DecimalType.HUNDRED);
            }

            //////////////// Infrared LED /////////////////////
            if (content.contains("<infraLedState>0</infraLedState>")) {
                setAttribute(CHANNEL_ENABLE_LED, OnOffType.OFF);
            }
            if (content.contains("<infraLedState>1</infraLedState>")) {
                setAttribute(CHANNEL_ENABLE_LED, OnOffType.ON);
            }

            if (content.contains("</CGI_Result>")) {
                ctx.close();
                onvifCameraHandler.getLog().debug("End of FOSCAM handler reached, so closing the channel to the camera now");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @UICameraAction(name = CHANNEL_ENABLE_LED, order = 50, icon = "far fa-lightbulb")
    public void enableLED(boolean on) {
        // Disable the auto mode first
        onvifCameraHandler.sendHttpGET(CG + "setInfraLedConfig&mode=1&usr=" + username + "&pwd=" + password);
        setAttribute(CHANNEL_AUTO_LED, OnOffType.OFF);
        if (on) {
            onvifCameraHandler.sendHttpGET(CG + "openInfraLed&usr=" + username + "&pwd=" + password);
        } else {
            onvifCameraHandler.sendHttpGET(CG + "closeInfraLed&usr=" + username + "&pwd=" + password);
        }
    }

    @UICameraAction(name = CHANNEL_AUTO_LED, order = 60, icon = "fas fa-lightbulb")
    public void autoLED(boolean on) {
        if (on) {
            setAttribute(CHANNEL_ENABLE_LED, null/*UnDefType.UNDEF*/);
            onvifCameraHandler.sendHttpGET(CG + "setInfraLedConfig&mode=0&usr=" + username + "&pwd=" + password);
        } else {
            onvifCameraHandler.sendHttpGET(CG + "setInfraLedConfig&mode=1&usr=" + username + "&pwd=" + password);
        }
    }

    @Override
    public void setAudioAlarmThreshold(int audioThreshold) {
        if (audioThreshold != this.audioThreshold) {
            this.audioThreshold = audioThreshold;
            if (audioThreshold == 0) {
                onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=0&usr="
                        + username + "&pwd=" + password);
            } else if (audioThreshold <= 33) {
                onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=1&sensitivity=0&usr="
                        + username + "&pwd=" + password);
            } else if (audioThreshold <= 66) {
                onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=1&sensitivity=1&usr="
                        + username + "&pwd=" + password);
            } else {
                onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=1&sensitivity=2&usr="
                        + username + "&pwd=" + password);
            }
        }
    }

    @Override
    public void setMotionAlarmThreshold(int threshold) {
        if (threshold > 0) {
            if (onvifCameraHandler.getCameraEntity().getCustomAudioAlarmUrl().isEmpty()) {
                onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=1&usr="
                        + username + "&pwd=" + password);
            } else {
                onvifCameraHandler.sendHttpGET(onvifCameraHandler.getCameraEntity().getCustomAudioAlarmUrl());
            }
        } else {
            onvifCameraHandler.sendHttpGET(CG + "setAudioAlarmConfig&isEnable=0&usr="
                    + username + "&pwd=" + password);
        }
    }

    @UICameraActionGetter(CHANNEL_ENABLE_MOTION_ALARM)
    public State getEnableMotionAlarm() {
        return getAttribute(CHANNEL_ENABLE_MOTION_ALARM);
    }

    @UICameraAction(name = CHANNEL_ENABLE_MOTION_ALARM, order = 14, icon = "fas fa-running")
    public void setEnableMotionAlarm(boolean on) {
        if (on) {
            if (onvifCameraHandler.getCameraEntity().getCustomMotionAlarmUrl().isEmpty()) {
                onvifCameraHandler.sendHttpGET(CG + "setMotionDetectConfig&isEnable=1&usr="
                        + username + "&pwd=" + password);
                onvifCameraHandler.sendHttpGET(CG + "setMotionDetectConfig1&isEnable=1&usr="
                        + username + "&pwd=" + password);
            } else {
                onvifCameraHandler.sendHttpGET(onvifCameraHandler.getCameraEntity().getCustomMotionAlarmUrl());
            }
        } else {
            onvifCameraHandler.sendHttpGET(CG + "setMotionDetectConfig&isEnable=0&usr="
                    + username + "&pwd=" + password);
            onvifCameraHandler.sendHttpGET(CG + "setMotionDetectConfig1&isEnable=0&usr="
                    + username + "&pwd=" + password);
        }
    }

    @Override
    public void runOncePerMinute(EntityContext entityContext) {
        onvifCameraHandler.sendHttpGET(CG + "getDevState&usr=" + username + "&pwd=" + password);
        onvifCameraHandler.sendHttpGET(CG + "getAudioAlarmConfig&usr=" + username + "&pwd=" + password);
    }

    @Override
    public void initialize(EntityContext entityContext) {
        OnvifCameraEntity cameraEntity = onvifCameraHandler.getCameraEntity();
        // Foscam needs any special char like spaces (%20) to be encoded for URLs.
        cameraEntity.setUser(Helper.encodeSpecialChars(cameraEntity.getUser()));
        cameraEntity.setPassword(Helper.encodeSpecialChars(cameraEntity.getPassword().asString()));
        if (StringUtils.isEmpty(onvifCameraHandler.getMjpegUri())) {
            onvifCameraHandler.setMjpegUri("/cgi-bin/CGIStream.cgi?cmd=GetMJStream&usr=" + cameraEntity.getUser() + "&pwd="
                    + cameraEntity.getPassword().asString());
        }
        if (StringUtils.isEmpty(onvifCameraHandler.getSnapshotUri())) {
            onvifCameraHandler.setSnapshotUri("/cgi-bin/CGIProxy.fcgi?usr=" + cameraEntity.getUser() + "&pwd="
                    + cameraEntity.getPassword().asString() + "&cmd=snapPicture2");
        }
    }
}
