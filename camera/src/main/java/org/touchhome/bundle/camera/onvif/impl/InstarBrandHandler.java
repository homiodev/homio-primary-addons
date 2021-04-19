package org.touchhome.bundle.camera.onvif.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.BrandCameraHasAudioAlarm;
import org.touchhome.bundle.camera.onvif.util.Helper;
import org.touchhome.bundle.camera.ui.UICameraAction;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@CameraBrandHandler(name = "Instar", handlerName = "instarHandler")
public class InstarBrandHandler extends BaseOnvifCameraBrandHandler implements BrandCameraHasAudioAlarm {
    private String requestUrl = "Empty";
    private int audioThreshold;

    public InstarBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
    }

    public InstarBrandHandler(OnvifCameraHandler onvifCameraHandler) {
        super(onvifCameraHandler);
    }

    public void setURL(String url) {
        requestUrl = url;
    }

    // This handles the incoming http replies back from the camera.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg == null || ctx == null) {
            return;
        }
        try {
            String value1;
            String content = msg.toString();
            onvifCameraHandler.getLog().trace("HTTP Result back from camera is \t:{}:", content);
            switch (requestUrl) {
                case "/param.cgi?cmd=getinfrared":
                    if (content.contains("var infraredstat=\"auto")) {
                        setAttribute(CHANNEL_AUTO_LED, OnOffType.ON);
                    } else {
                        setAttribute(CHANNEL_AUTO_LED, OnOffType.OFF);
                    }
                    break;
                case "/param.cgi?cmd=getoverlayattr&-region=1":// Text Overlays
                    if (content.contains("var show_1=\"0\"")) {
                        setAttribute(CHANNEL_TEXT_OVERLAY, StringType.EMPTY);
                    } else {
                        value1 = Helper.searchString(content, "var name_1=\"");
                        if (!value1.isEmpty()) {
                            setAttribute(CHANNEL_TEXT_OVERLAY, new StringType(value1));
                        }
                    }
                    break;
                case "/cgi-bin/hi3510/param.cgi?cmd=getmdattr":// Motion Alarm
                    // Motion Alarm
                    if (content.contains("var m1_enable=\"1\"")) {
                        setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.ON);
                    } else {
                        setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.OFF);
                    }
                    break;
                case "/cgi-bin/hi3510/param.cgi?cmd=getaudioalarmattr":// Audio Alarm
                    if (content.contains("var aa_enable=\"1\"")) {
                        setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.ON);
                        value1 = Helper.searchString(content, "var aa_value=\"");
                        if (!value1.isEmpty()) {
                            setAttribute(CHANNEL_AUDIO_THRESHOLD, new DecimalType(value1));
                        }
                    } else {
                        setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.OFF);
                    }
                    break;
                case "param.cgi?cmd=getpirattr":// PIR Alarm
                    if (content.contains("var pir_enable=\"1\"")) {
                        setAttribute(CHANNEL_ENABLE_PIR_ALARM, OnOffType.ON);
                    } else {
                        setAttribute(CHANNEL_ENABLE_PIR_ALARM, OnOffType.OFF);
                    }
                    // Reset the Alarm, need to find better place to put this.
                    onvifCameraHandler.motionDetected(false, CHANNEL_PIR_ALARM);
                    break;
                case "/param.cgi?cmd=getioattr":// External Alarm Input
                    if (content.contains("var io_enable=\"1\"")) {
                        setAttribute(CHANNEL_ENABLE_EXTERNAL_ALARM_INPUT, OnOffType.ON);
                    } else {
                        setAttribute(CHANNEL_ENABLE_EXTERNAL_ALARM_INPUT, OnOffType.OFF);
                    }
                    break;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void setAudioAlarmThreshold(int audioThreshold) {
        if (audioThreshold != this.audioThreshold) {
            this.audioThreshold = audioThreshold;
            if (this.audioThreshold > 0) {
                onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=setaudioalarmattr&-aa_enable=1");
                onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=setaudioalarmattr&-aa_enable=1&-aa_value=" + audioThreshold);
            } else {
                onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=setaudioalarmattr&-aa_enable=0");
            }
        }
    }

    @UICameraAction(name = CHANNEL_ENABLE_MOTION_ALARM, order = 14, icon = "fas fa-running")
    public void enableMotionAlarm(boolean on) {
        int val = boolToInt(on);
        onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=setmdattr&-enable=" + val +
                "&-name=1&cmd=setmdattr&-enable=" + val + "&-name=2&cmd=setmdattr&-enable=" + val + "&-name=3&cmd=setmdattr&-enable=" + val + "&-name=4");
    }

    @UICameraAction(name = CHANNEL_TEXT_OVERLAY, order = 100, icon = "fas fa-paragraph")
    public void textOverlay(String value) {
        String text = Helper.encodeSpecialChars(value);
        if (text.isEmpty()) {
            onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setoverlayattr&-region=1&-show=0");
        } else {
            onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setoverlayattr&-region=1&-show=1&-name=" + text);
        }
    }

    @UICameraAction(name = CHANNEL_AUTO_LED, order = 60, icon = "fas fa-lightbulb")
    public void autoLED(boolean on) {
        if (on) {
            onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setinfrared&-infraredstat=auto");
        } else {
            onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setinfrared&-infraredstat=close");
        }
    }

    @UICameraAction(name = CHANNEL_ENABLE_PIR_ALARM, order = 120, icon = "fas fa-compress-alt")
    public void enablePirAlarm(boolean on) {
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setpirattr&-pir_enable=" + boolToInt(on));
    }


    @UICameraAction(name = CHANNEL_ENABLE_EXTERNAL_ALARM_INPUT, order = 250, icon = "fas fa-external-link-square-alt")
    public void enableExternalAlarmInput(boolean on) {
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=setioattr&-io_enable=" + boolToInt(on));
    }

    public void alarmTriggered(String alarm) {
        onvifCameraHandler.getLog().debug("Alarm has been triggered:{}", alarm);
        switch (alarm) {
            case "/instar?&active=1":// The motion area boxes 1-4
            case "/instar?&active=2":
            case "/instar?&active=3":
            case "/instar?&active=4":
                onvifCameraHandler.motionDetected(true, CHANNEL_MOTION_ALARM);
                break;
            case "/instar?&active=5":// PIR
                onvifCameraHandler.motionDetected(true, CHANNEL_PIR_ALARM);
                break;
            case "/instar?&active=6":// Audio Alarm
                onvifCameraHandler.audioDetected(true);
                break;
            case "/instar?&active=7":// Motion Area 1
            case "/instar?&active=8":// Motion Area 2
            case "/instar?&active=9":// Motion Area 3
            case "/instar?&active=10":// Motion Area 4
                onvifCameraHandler.motionDetected(true, CHANNEL_MOTION_ALARM);
                break;
        }
    }

    @Override
    public void runOncePerMinute(EntityContext entityContext) {
        onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=getaudioalarmattr");
        onvifCameraHandler.sendHttpGET("/cgi-bin/hi3510/param.cgi?cmd=getmdattr");
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=getinfrared");
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=getoverlayattr&-region=1");
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=getpirattr");
        onvifCameraHandler.sendHttpGET("/param.cgi?cmd=getioattr"); // ext alarm input on/off
    }

    @Override
    public void pollCameraRunnable(OnvifCameraHandler onvifCameraHandler) {
        onvifCameraHandler.motionDetected(false, CHANNEL_MOTION_ALARM);
        onvifCameraHandler.motionDetected(false, CHANNEL_PIR_ALARM);
        onvifCameraHandler.audioDetected(false);
    }

    @Override
    public void initialize(EntityContext entityContext) {
        if (StringUtils.isEmpty(onvifCameraHandler.getMjpegUri())) {
            onvifCameraHandler.setMjpegUri("/tmpfs/snap.jpg");
        }
        if (StringUtils.isEmpty(onvifCameraHandler.getSnapshotUri())) {
            onvifCameraHandler.setSnapshotUri("/mjpegstream.cgi?-chn=12");
        }
    }

    @Override
    public void handleSetURL(ChannelPipeline pipeline, String httpRequestURL) {
        InstarBrandHandler instarHandler = (InstarBrandHandler) pipeline.get("instarHandler");
        instarHandler.setURL(httpRequestURL);
    }
}
