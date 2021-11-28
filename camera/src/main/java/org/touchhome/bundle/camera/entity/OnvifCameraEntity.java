package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.onvif.soap.OnvifDeviceState;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.*;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.action.v1.UIEntityItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.camera.CameraCoordinator;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.CameraBrandHandlerDescription;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Collection;

@Log4j2
@Setter
@Getter
@Entity
@Accessors(chain = true)
public class OnvifCameraEntity extends BaseFFmpegStreamEntity<OnvifCameraEntity, OnvifCameraHandler>
        implements AbilityToStreamHLSOverFFmpeg<OnvifCameraEntity>, HasDynamicContextMenuActions {

    public static final String PREFIX = "onvifcam_";

    public OnvifCameraEntity() {
    }

    @Transient
    @JsonIgnore
    private BaseOnvifCameraBrandHandler onvifCameraBrandHandler;

    @UIField(order = 1, readOnly = true, hideOnEmpty = true, fullWidth = true, bg = "#334842", type = UIFieldType.HTML)
    public String getDescription() {
        if (getIeeeAddress() == null) {
            return Lang.getServerMessage("ONVIF.REQ_AUTH_DESCRIPTION");
        }
        return null;
    }

    @UIField(order = 11)
    @RestartHandlerOnChange
    @UIFieldSelection(SelectCameraBrand.class)
    public String getCameraType() {
        return getJsonData("cameraType", CameraBrandHandlerDescription.DEFAULT_BRAND.getID());
    }

    public OnvifCameraEntity setCameraType(String cameraType) {
        return setJsonData("cameraType", cameraType);
    }

    public static class SelectCameraBrand implements DynamicOptionLoader {

        @Override
        public Collection<OptionModel> loadOptions(BaseEntity baseEntity, EntityContext entityContext, String[] staticParameters) {
            return OptionModel.list(CameraCoordinator.cameraBrands.keySet());
        }
    }

    @UIField(order = 12, type = UIFieldType.IpAddress)
    @RestartHandlerOnChange
    public String getIp() {
        return getJsonData("ip");
    }

    public OnvifCameraEntity setIp(String ip) {
        return setJsonData("ip", ip);
    }

    @UIFieldPort
    @UIField(order = 35)
    @RestartHandlerOnChange
    public int getOnvifPort() {
        return getJsonData("onvifPort", 8000);
    }

    public OnvifCameraEntity setOnvifPort(int value) {
        return setJsonData("onvifPort", value);
    }

    @UIFieldPort
    @UIField(order = 36, advanced = true, onlyEdit = true)
    @RestartHandlerOnChange
    public int getRestPort() {
        return getJsonData("restPort", 80);
    }

    public OnvifCameraEntity setRestPort(int value) {
        return setJsonData("restPort", value);
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
        if (getCameraHandler() != null && getCameraHandler().isHandlerInitialized() &&
                (StringUtils.isEmpty(snapshotUrl) || snapshotUrl.equals("ffmpeg"))) {
            snapshotUrl = getCameraHandler().getOnvifDeviceState().getMediaDevices().getSnapshotUri();
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

    @UIField(order = 250, readOnly = true)
    @UIFieldColorStatusMatch
    public String getEventSubscription() {
        OnvifCameraHandler cameraHandler = getCameraHandler();
        if (cameraHandler != null) {
            String subscriptionError = cameraHandler.getOnvifDeviceState().getSubscriptionError();
            if (subscriptionError != null) {
                return Status.ERROR.name() + " " + subscriptionError;
            }
            return Status.ONLINE.name();
        }
        return null;
    }

    @Override
    public OnvifCameraHandler createCameraHandler(EntityContext entityContext) {
        return new OnvifCameraHandler(this, entityContext);
    }

    @JsonIgnore
    public BaseOnvifCameraBrandHandler getOnvifCameraBrandHandler() {
        if (onvifCameraBrandHandler == null) {
            onvifCameraBrandHandler = CameraCoordinator.cameraBrands.get(getCameraType()).newInstance(this);
        }
        return onvifCameraBrandHandler;
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
                log.info("Onvif camera <{}> changed ip address from <{}> to <{}>", getTitle(), getIp(), ip);
            }
            if (!getIp().equals(ip)) {
                log.info("Onvif camera <{}> changed port from <{}> to <{}>", getTitle(), getOnvifPort(), port);
            }
            if (!getName().equals(name)) {
                log.info("Onvif camera <{}> changed name from <{}> to <{}>", getTitle(), getName(), name);
            }
            entityContext.updateDelayed(this, entity -> entity.setIp(ip).setOnvifPort(port).setName(name));
        }
    }

    @UIContextMenuAction(value = "RESTART", icon = "fas fa-power-off")
    public ActionResponseModel reboot() {
        if (this.getCameraHandler().isHandlerInitialized()) {
            String response = this.getCameraHandler().getOnvifDeviceState().getInitialDevices().reboot();
            return ActionResponseModel.showSuccess(response);
        }
        return ActionResponseModel.showWarn("VIDEO_STREAM.CAMERA_NOT_INIT");
    }

    @Override
    public UIInputBuilder assembleActions() {
        UIInputBuilder uiInputBuilder = super.assembleActions();
        if (uiInputBuilder != null) {
            for (UIEntityItemBuilder uiEntity : uiInputBuilder.getUiEntityItemBuilders(true)) {
                uiEntity.setDisabled(!this.isStart());
            }

            if (StringUtils.isEmpty(getIeeeAddress()) || getStatus() == Status.REQUIRE_AUTH) {
                uiInputBuilder.addOpenDialogSelectableButton("AUTHENTICATE", "fas fa-sign-in-alt", null, null,
                        (entityContext, params) -> {

                            String user = params.getString("user");
                            String password = params.getString("pwd");
                            OnvifCameraEntity entity = this;
                            OnvifDeviceState onvifDeviceState = new OnvifDeviceState(getIp(), getOnvifPort(), 0, user, password, log);
                            try {
                                onvifDeviceState.checkForErrors();
                                setUser(user);
                                setPassword(password);
                                setName(onvifDeviceState.getInitialDevices().getName());
                                setIeeeAddress(onvifDeviceState.getIEEEAddress());

                                entityContext.save(entity);
                                entityContext.ui().sendSuccessMessage("Onvif camera: " + getTitle() + " authenticated successfully");
                            } catch (Exception ex) {
                                entityContext.ui().sendWarningMessage("Onvif camera: " + getTitle() + " fault response: " + ex.getMessage());
                            }
                            return null;
                        }).editDialog(dialogBuilder -> {
                    dialogBuilder.addTextInput("user", getUser(), true);
                    dialogBuilder.addTextInput("pwd", getPassword().asString(), false);
                });
            }
        }

        return uiInputBuilder;
    }
}
