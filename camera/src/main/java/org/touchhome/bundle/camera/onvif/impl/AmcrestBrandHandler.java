package org.touchhome.bundle.camera.onvif.impl;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ACTIVATE_ALARM_OUTPUT;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ACTIVATE_ALARM_OUTPUT2;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_AUDIO_THRESHOLD;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_AUTO_LED;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ENABLE_AUDIO_ALARM;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ENABLE_LED;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ENABLE_LINE_CROSSING_ALARM;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ENABLE_MOTION_ALARM;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_ENABLE_PRIVACY_MODE;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_MOTION_ALARM;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_TEXT_OVERLAY;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CM;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.video.ui.UIVideoAction;
import org.touchhome.bundle.api.video.ui.UIVideoActionGetter;
import org.touchhome.bundle.camera.onvif.brand.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.brand.BrandCameraHasAudioAlarm;
import org.touchhome.bundle.camera.onvif.brand.BrandCameraHasMotionAlarm;
import org.touchhome.bundle.camera.onvif.util.Helper;
import org.touchhome.bundle.camera.service.OnvifCameraService;

/**
 * responsible for handling commands, which are sent to one of the channels.
 */
@Log4j2
@CameraBrandHandler(name = "Amcrest", handlerName = "amcrestHandler")
public class AmcrestBrandHandler extends BaseOnvifCameraBrandHandler implements BrandCameraHasAudioAlarm,
    BrandCameraHasMotionAlarm {

  private String requestUrl = "Empty";
  private int audioThreshold;

  public AmcrestBrandHandler(OnvifCameraService service) {
    super(service);
  }

  @UIVideoAction(name = CHANNEL_TEXT_OVERLAY, order = 100, icon = "fas fa-paragraph")
  public void textOverlay(String value) {
    String text = Helper.encodeSpecialChars(value);
    if (text.isEmpty()) {
      getService().sendHttpGET(CM + "setConfig&VideoWidget[0].CustomTitle[1].EncodeBlend=false");
    } else {
      getService().sendHttpGET(CM + "setConfig&VideoWidget[0].CustomTitle[1].EncodeBlend=true&VideoWidget[0].CustomTitle[1].Text="
          + text);
    }
  }

  @UIVideoAction(name = CHANNEL_ENABLE_LED, order = 50, icon = "far fa-lightbulb")
  public void enableLed(boolean on) {
    setAttribute(CHANNEL_AUTO_LED, OnOffType.OFF);
    if (on) {
      getService().sendHttpGET(CM + "setConfig&Lighting[0][0].Mode=Manual");
    } else {
      getService().sendHttpGET(CM + "setConfig&Lighting[0][0].Mode=Off");
    }
  }

  @UIVideoAction(name = CHANNEL_AUTO_LED, order = 60, icon = "fas fa-lightbulb")
  public void autoLed(boolean on) {
    if (on) {
      setAttribute(CHANNEL_ENABLE_LED, null);
      getService().sendHttpGET(CM + "setConfig&Lighting[0][0].Mode=Auto");
    }
  }

  @Override
  public Consumer<Boolean> getIRLedHandler() {
    return on -> {
      setAttribute(CHANNEL_AUTO_LED, OnOffType.OFF);
      if (on) {
        getService().sendHttpGET(CM + "setConfig&Lighting[0][0].Mode=Manual");
      } else {
        getService().sendHttpGET(CM + "setConfig&Lighting[0][0].Mode=Off");
      }
    };
  }

  @Override
  public Supplier<Boolean> getIrLedValueHandler() {
    return () -> Optional.ofNullable(getAttribute(CHANNEL_ENABLE_LED)).map(State::boolValue).orElse(false);
  }

  @Override
  public void setAudioAlarmThreshold(int audioThreshold) {
    if (audioThreshold != this.audioThreshold) {
      this.audioThreshold = audioThreshold;
      if (this.audioThreshold > 0) {
        getService().sendHttpGET(CM + "setConfig&AudioDetect[0].MutationThreold=" + audioThreshold);
      } else {
        getService().sendHttpGET(CM + "setConfig&AudioDetect[0].MutationThreold=1");
      }
    }
  }

  @Override
  public void setMotionAlarmThreshold(int threshold) {
    if (threshold > 0) {
      getService().sendHttpGET(CM + "setConfig&AudioDetect[0].MutationDetect=true&AudioDetect[0].EventHandler.Dejitter=1");
    } else {
      getService().sendHttpGET(CM + "setConfig&AudioDetect[0].MutationDetect=false");
    }
  }

  @UIVideoActionGetter(CHANNEL_ENABLE_LINE_CROSSING_ALARM)
  public State getEnableLineCrossingAlarm() {
    return getAttribute(CHANNEL_ENABLE_LINE_CROSSING_ALARM);
  }

  @UIVideoAction(name = CHANNEL_ENABLE_LINE_CROSSING_ALARM, order = 150, icon = "fas fa-grip-lines-vertical")
  public void setEnableLineCrossingAlarm(boolean on) {
    if (on) {
      getService().sendHttpGET(CM + "setConfig&VideoAnalyseRule[0][1].Enable=true");
    } else {
      getService().sendHttpGET(CM + "setConfig&VideoAnalyseRule[0][1].Enable=false");
    }
  }

  @UIVideoActionGetter(CHANNEL_ENABLE_MOTION_ALARM)
  public State getEnableMotionAlarm() {
    return getAttribute(CHANNEL_ENABLE_MOTION_ALARM);
  }

  @UIVideoAction(name = CHANNEL_ENABLE_MOTION_ALARM, order = 14, icon = "fas fa-running")
  public void setEnableMotionAlarm(boolean on) {
    if (on) {
      getService().sendHttpGET(CM + "setConfig&MotionDetect[0].Enable=true&MotionDetect[0].EventHandler.Dejitter=1");
    } else {
      getService().sendHttpGET(CM + "setConfig&MotionDetect[0].Enable=false");
    }
  }

  @UIVideoAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT, order = 45, icon = "fas fa-bell")
  public void activateAlarmOutput(boolean on) {
    if (on) {
      getService().sendHttpGET(CM + "setConfig&AlarmOut[0].Mode=1");
    } else {
      getService().sendHttpGET(CM + "setConfig&AlarmOut[0].Mode=0");
    }
  }

  @UIVideoAction(name = CHANNEL_ACTIVATE_ALARM_OUTPUT2, order = 47, icon = "fas fa-bell")
  public void activateAlarmOutput2(boolean on) {
    if (on) {
      getService().sendHttpGET(CM + "setConfig&AlarmOut[1].Mode=1");
    } else {
      getService().sendHttpGET(CM + "setConfig&AlarmOut[1].Mode=0");
    }
  }

  @UIVideoActionGetter(CHANNEL_ENABLE_PRIVACY_MODE)
  public State getEnablePrivacyMode() {
    return getAttribute(CHANNEL_ENABLE_PRIVACY_MODE);
  }

  @UIVideoAction(name = CHANNEL_ENABLE_PRIVACY_MODE, order = 70, icon = "fas fa-user-secret")
  public void setEnablePrivacyMode(boolean on) {
    getService().sendHttpGET(CM + "setConfig&LeLensMask[0].Enable=" + on);
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
    OnvifCameraService service = getService();
    try {
      String content = msg.toString();
      log.debug("[{}]: HTTP Result back from camera is \t:{}:", entityID, content);
      if (content.contains("Error: No Events")) {
        if ("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=VideoMotion".equals(requestUrl)) {
          service.motionDetected(false, CHANNEL_MOTION_ALARM);
        } else if ("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=AudioMutation".equals(requestUrl)) {
          service.audioDetected(false);
        }
      } else if (content.contains("channels[0]=0")) {
        if ("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=VideoMotion".equals(requestUrl)) {
          service.motionDetected(true, CHANNEL_MOTION_ALARM);
        } else if ("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=AudioMutation".equals(requestUrl)) {
          service.audioDetected(true);
        }
      }

      if (content.contains("table.MotionDetect[0].Enable=false")) {
        setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.OFF);
      } else if (content.contains("table.MotionDetect[0].Enable=true")) {
        setAttribute(CHANNEL_ENABLE_MOTION_ALARM, OnOffType.ON);
      }
      // determine if the audio alarm is turned on or off.
      if (content.contains("table.AudioDetect[0].MutationDetect=true")) {
        setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.ON);
      } else if (content.contains("table.AudioDetect[0].MutationDetect=false")) {
        setAttribute(CHANNEL_ENABLE_AUDIO_ALARM, OnOffType.OFF);
      }
      // Handle AudioMutationThreshold alarm
      if (content.contains("table.AudioDetect[0].MutationThreold=")) {
        String value = service.returnValueFromString(content, "table.AudioDetect[0].MutationThreold=");
        setAttribute(CHANNEL_AUDIO_THRESHOLD, new DecimalType(value));
      }
      // Privacy Mode on/off
      if (content.contains("Code=LensMaskOpen;") || content.contains("table.LeLensMask[0].Enable=true")) {
        setAttribute(CHANNEL_ENABLE_PRIVACY_MODE, OnOffType.ON);
      } else if (content.contains("Code=LensMaskClose;")
          || content.contains("table.LeLensMask[0].Enable=false")) {
        setAttribute(CHANNEL_ENABLE_PRIVACY_MODE, OnOffType.OFF);
      }
    } finally {
      ReferenceCountUtil.release(msg);
      ctx.close();
    }
  }

  @Override
  public void runOncePerMinute(EntityContext entityContext) {
    OnvifCameraService service = getService();
    service.sendHttpGET("/cgi-bin/configManager.cgi?action=getConfig&name=AudioDetect[0]");
    service.sendHttpGET("/cgi-bin/configManager.cgi?action=getConfig&name=LeLensMask[0]");
    service.sendHttpGET("/cgi-bin/configManager.cgi?action=getConfig&name=MotionDetect[0]");
    service.sendHttpGET("/cgi-bin/configManager.cgi?action=getConfig&name=CrossLineDetection[0]");
  }

  @Override
  public void pollCameraRunnable() {
    OnvifCameraService service = getService();
    service.sendHttpGET("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=VideoMotion");
    service.sendHttpGET("/cgi-bin/eventManager.cgi?action=getEventIndexes&code=AudioMutation");
  }

  @Override
  public void initialize(EntityContext entityContext) {
    OnvifCameraService service = getService();
    if (StringUtils.isEmpty(service.getMjpegUri())) {
      service.setMjpegUri("/cgi-bin/mjpg/video.cgi?channel=" + getEntity().getNvrChannel() + "&subtype=1");
    }
    if (StringUtils.isEmpty(service.getSnapshotUri())) {
      service.setSnapshotUri("/cgi-bin/snapshot.cgi?channel=" + getEntity().getNvrChannel());
    }
  }

  @Override
  public void handleSetURL(ChannelPipeline pipeline, String httpRequestURL) {
    AmcrestBrandHandler amcrestHandler = (AmcrestBrandHandler) pipeline.get("amcrestHandler");
    amcrestHandler.setURL(httpRequestURL);
  }
}
