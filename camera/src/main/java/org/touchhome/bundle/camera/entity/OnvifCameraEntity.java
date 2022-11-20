package org.touchhome.bundle.camera.entity;

import de.onvif.soap.OnvifDeviceState;
import java.net.URI;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityLog;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldIgnoreGetDefault;
import org.touchhome.bundle.api.ui.field.UIFieldPort;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.action.v1.UIEntityItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.touchhome.bundle.api.video.BaseVideoService;
import org.touchhome.bundle.api.video.DownloadFile;
import org.touchhome.bundle.api.video.VideoPlaybackStorage;
import org.touchhome.bundle.camera.onvif.brand.CameraBrandHandlerDescription;
import org.touchhome.bundle.camera.service.OnvifCameraService;
import org.touchhome.common.util.Lang;

@Log4j2
@Setter
@Getter
@Entity
public class OnvifCameraEntity extends BaseFFMPEGVideoStreamEntity<OnvifCameraEntity, OnvifCameraService>
    implements HasDynamicContextMenuActions, VideoPlaybackStorage, HasEntityLog {

  public static final String PREFIX = "onvifcam_";

  @UIField(order = 16)
  @RestartHandlerOnChange
  @UIFieldSelection(SelectCameraBrand.class)
  public String getCameraType() {
    return getJsonData("cameraType", CameraBrandHandlerDescription.DEFAULT_BRAND.getID());
  }

  public OnvifCameraEntity setCameraType(String cameraType) {
    setJsonData("cameraType", cameraType);
    return this;
  }

  @Override
  public String getTitle() {
    return super.getTitle();
  }

  @UIField(order = 1, readOnly = true, hideOnEmpty = true, fullWidth = true, bg = "#334842", type = UIFieldType.HTML)
  public String getDescription() {
    if (getIeeeAddress() == null) {
      return Lang.getServerMessage("ONVIF.REQ_AUTH_DESCRIPTION");
    }
    return null;
  }

  @UIField(order = 12, readOnly = true, hideOnEmpty = true)
  @UIFieldColorStatusMatch(handlePrefixes = true)
  public String getEventSubscription() {
    return optService().filter(BaseVideoService::isHandlerInitialized).map(s -> {
      String subscriptionError = s.getOnvifDeviceState().getSubscriptionError();
      if (subscriptionError != null) {
        return Status.ERROR.name() + " " + subscriptionError;
      }
      return Status.ONLINE.name();
    }).orElse(Status.UNKNOWN.name());
  }

  @UIField(order = 15, type = UIFieldType.IpAddress)
  @RestartHandlerOnChange
  public String getIp() {
    return getJsonData("ip");
  }

  public OnvifCameraEntity setIp(String ip) {
    setJsonData("ip", ip);
    return this;
  }

  @UIFieldPort
  @UIField(order = 35)
  @RestartHandlerOnChange
  public int getOnvifPort() {
    return getJsonData("onvifPort", 8000);
  }

  public OnvifCameraEntity setOnvifPort(int value) {
    setJsonData("onvifPort", value);
    return this;
  }

  @UIFieldPort
  @UIField(order = 36, onlyEdit = true)
  @RestartHandlerOnChange
  public int getRestPort() {
    return getJsonData("restPort", 80);
  }

  public void setRestPort(int value) {
    setJsonData("restPort", value);
  }

  @UIField(order = 55, onlyEdit = true)
  @RestartHandlerOnChange
  public int getOnvifMediaProfile() {
    return getJsonData("onvifMediaProfile", 0);
  }

  public void setOnvifMediaProfile(int value) {
    setJsonData("onvifMediaProfile", value);
  }

  @UIField(order = 45, onlyEdit = true, label = "cameraUsername")
  @RestartHandlerOnChange
  public String getUser() {
    return super.getUser();
  }

  @UIField(order = 50, onlyEdit = true, label = "cameraPassword")
  @RestartHandlerOnChange
  public SecureString getPassword() {
    return super.getPassword();
  }

  @UIField(order = 80, onlyEdit = true)
  @RestartHandlerOnChange
  public String getAlarmInputUrl() {
    return super.getAlarmInputUrl();
  }

  @UIField(order = 70, onlyEdit = true)
  @RestartHandlerOnChange
  public int getNvrChannel() {
    return getJsonData("nvrChannel", 0);
  }

  public void setNvrChannel(int value) {
    setJsonData("nvrChannel", value);
  }

  @UIField(order = 75, onlyEdit = true)
  @RestartHandlerOnChange
  @UIFieldIgnoreGetDefault
  public String getSnapshotUrl() {
    String snapshotUrl = getJsonData("snapshotUrl");
    OnvifCameraService service = optService().orElse(null);
    if (service != null && service.isHandlerInitialized() &&
        (StringUtils.isEmpty(snapshotUrl) || snapshotUrl.equals("ffmpeg"))) {
      snapshotUrl = service.getOnvifDeviceState().getMediaDevices().getSnapshotUri();
    }
    return StringUtils.isEmpty(snapshotUrl) ? "ffmpeg" : snapshotUrl;
  }

  public void setSnapshotUrl(String value) {
    setJsonData("snapshotUrl", value);
  }

  @UIField(order = 85, onlyEdit = true)
  @RestartHandlerOnChange
  public String getCustomMotionAlarmUrl() {
    return getJsonData("customMotionAlarmUrl", "");
  }

  public void setCustomMotionAlarmUrl(String value) {
    setJsonData("customMotionAlarmUrl", value);
  }

  @UIField(order = 90, onlyEdit = true)
  @RestartHandlerOnChange
  public String getCustomAudioAlarmUrl() {
    return getJsonData("customAudioAlarmUrl", "");
  }

  public void setCustomAudioAlarmUrl(String value) {
    setJsonData("customAudioAlarmUrl", value);
  }

  @UIField(order = 95, onlyEdit = true)
  @RestartHandlerOnChange
  public String getMjpegUrl() {
    return getJsonData("mjpegUrl", "ffmpeg");
  }

  public void setMjpegUrl(String value) {
    setJsonData("mjpegUrl", value);
  }

  @UIField(order = 100, onlyEdit = true)
  @RestartHandlerOnChange
  public String getFfmpegInput() {
    return getJsonData("ffmpegInput", "");
  }

  public void setFfmpegInput(String value) {
    setJsonData("ffmpegInput", value);
  }

  @UIField(order = 105, onlyEdit = true)
  @RestartHandlerOnChange
  public String getFfmpegInputOptions() {
    return getJsonData("ffmpegInputOptions", "");
  }

  public void setFfmpegInputOptions(String value) {
    setJsonData("ffmpegInputOptions", value);
  }

  @UIField(order = 155, onlyEdit = true)
  public boolean isPtzContinuous() {
    return getJsonData("ptzContinuous", false);
  }

  public void setPtzContinuous(boolean value) {
    setJsonData("ptzContinuous", value);
  }

  @Override
  public String getFolderName() {
    return "camera";
  }

  @Override
  public String getDefaultName() {
    return "Onvif camera";
  }

  @Override
  public String toString() {
    return "onvif:" + getIp() + ":" + getOnvifPort();
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @Override
  @UIFieldIgnore
  public boolean isHasAudioStream() {
    return true;
  }

  public void tryUpdateData(EntityContext entityContext, String ip, Integer port, String name) {
    if (!getIp().equals(ip) || getOnvifPort() != port || !getName().equals(name)) {
      if (!getIp().equals(ip)) {
        log.info("[{}]: Onvif camera <{}> changed ip address from <{}> to <{}>", getEntityID(), this, getIp(), ip);
      }
      if (!getIp().equals(ip)) {
        log.info("[{}]: Onvif camera <{}> changed port from <{}> to <{}>", getEntityID(), this, getOnvifPort(), port);
      }
      if (!getName().equals(name)) {
        log.info("[{}]: Onvif camera <{}> changed name from <{}> to <{}>", getEntityID(), this, getName(), name);
      }
      entityContext.updateDelayed(this, entity -> entity.setIp(ip).setOnvifPort(port).setName(name));
    }
  }

  @UIContextMenuAction(value = "RESTART", icon = "fas fa-power-off")
  public ActionResponseModel reboot() {
    String response = getService().getOnvifDeviceState().getInitialDevices().reboot();
    return ActionResponseModel.showSuccess(response);
  }

  @Override
  public UIInputBuilder assembleActions() {
    UIInputBuilder uiInputBuilder = super.assembleActions();
    if (uiInputBuilder != null) {
      for (UIEntityItemBuilder uiEntity : uiInputBuilder.getUiEntityItemBuilders(true)) {
        uiEntity.setDisabled(!this.isStart());
      }

      if (StringUtils.isEmpty(getIeeeAddress()) || getStatus() == Status.REQUIRE_AUTH) {
        uiInputBuilder.addOpenDialogSelectableButton("AUTHENTICATE", "fas fa-sign-in-alt", null, 250,
            (entityContext, params) -> {

              String user = params.getString("user");
              String password = params.getString("pwd");
              OnvifCameraEntity entity = entityContext.getEntity(getEntityID());
              OnvifDeviceState onvifDeviceState = new OnvifDeviceState(getEntityID());
              onvifDeviceState.updateParameters(entity.getIp(), entity.getOnvifPort(), 0, user, password);
              try {
                onvifDeviceState.checkForErrors();
                entity.setUser(user);
                entity.setPassword(password);
                entity.setName(onvifDeviceState.getInitialDevices().getName());
                entity.setIeeeAddress(onvifDeviceState.getIEEEAddress());

                entityContext.save(entity);
                entityContext.ui()
                    .sendSuccessMessage("Onvif camera: " + this + " authenticated successfully");
              } catch (Exception ex) {
                entityContext.ui().sendWarningMessage(
                    "Onvif camera: " + this + " fault response: " + ex.getMessage());
              }
              return null;
            }).editDialog(dialogBuilder -> {
          dialogBuilder.setTitle(null, "fas fa-sign-in-alt");
          dialogBuilder.addTextInput("user", getUser(), true);
          dialogBuilder.addTextInput("pwd", getPassword().asString(), false);
        });
      }
    }

    return uiInputBuilder;
  }

  @Override
  public LinkedHashMap<Long, Boolean> getAvailableDaysPlaybacks(EntityContext entityContext, String profile, Date from, Date to)
      throws Exception {
    return getService().getVideoPlaybackStorage().getAvailableDaysPlaybacks(entityContext, profile, from, to);
  }

  @Override
  public List<PlaybackFile> getPlaybackFiles(EntityContext entityContext, String profile, Date from, Date to) throws Exception {
    return getService().getVideoPlaybackStorage().getPlaybackFiles(entityContext, profile, from, to);
  }

  @Override
  public DownloadFile downloadPlaybackFile(EntityContext entityContext, String profile, String fileId, Path path)
      throws Exception {
    return getService().getVideoPlaybackStorage().downloadPlaybackFile(entityContext, profile, fileId, path);
  }

  @Override
  public URI getPlaybackVideoURL(EntityContext entityContext, String fileId) throws Exception {
    return getService().getVideoPlaybackStorage().getPlaybackVideoURL(entityContext, fileId);
  }

  @Override
  public PlaybackFile getLastPlaybackFile(EntityContext entityContext, String profile) {
    return getService().getVideoPlaybackStorage().getLastPlaybackFile(entityContext, profile);
  }

  @Override
  public Class<OnvifCameraService> getEntityServiceItemClass() {
    return OnvifCameraService.class;
  }

  @Override
  public OnvifCameraService createService(EntityContext entityContext) {
    return new OnvifCameraService(entityContext, this);
  }

  @Override
  public void logBuilder(EntityLogBuilder entityLogBuilder) {
    entityLogBuilder.addTopic("org.touchhome.bundle.camera", "entityID");
    entityLogBuilder.addTopic("org.touchhome.bundle.api.video", "entityID");
  }

  public static class SelectCameraBrand implements DynamicOptionLoader {

    @Override
    public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
      return OptionModel.list(OnvifCameraService.getCameraBrands(parameters.getEntityContext()).keySet());
    }
  }
}
