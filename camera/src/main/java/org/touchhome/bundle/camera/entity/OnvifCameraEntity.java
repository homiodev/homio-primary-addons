package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.field.*;
import org.touchhome.bundle.api.ui.field.action.ActionInputParameter;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.action.impl.DynamicContextMenuAction;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraActions;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.OnvifConnection;
import org.touchhome.bundle.camera.onvif.util.OnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Collections;
import java.util.Set;

@Log4j2
@Setter
@Getter
@Entity
@Accessors(chain = true)
public class OnvifCameraEntity extends BaseFFmpegStreamEntity<OnvifCameraEntity, OnvifCameraHandler>
        implements AbilityToStreamHLSOverFFmpeg<OnvifCameraEntity>, HasDynamicContextMenuActions {

    public static final String PREFIX = "onvifcam_";

    @Transient
    @JsonIgnore
    private int port;

    @Transient
    @JsonIgnore
    private String updateImageWhen = "0";

    @Transient
    @JsonIgnore
    private OnvifCameraBrandHandler onvifCameraBrandHandler;

    @UIField(order = 1, readOnly = true, hideOnEmpty = true, fullWidth = true, bg = "#334842")
    @UIFieldRenderAsHTML
    public String getDescription() {
        if (getIeeeAddress() == null) {
            return Lang.getServerMessage("ONVIF.REQ_AUTH_DESCRIPTION");
        }
        return null;
    }

    @UIField(order = 11)
    @RestartHandlerOnChange
    public OnvifCameraType getCameraType() {
        return getJsonDataEnum("cameraType", OnvifCameraType.onvif);
    }

    public OnvifCameraEntity setCameraType(OnvifCameraType cameraType) {
        return setJsonDataEnum("cameraType", cameraType);
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
    public String getPassword() {
        return super.getPassword();
    }

    @UIField(order = 80, onlyEdit = true)
    @RestartHandlerOnChange
    public String getAlarmInputUrl() {
        return super.getAlarmInputUrl();
    }

    @UIField(order = 60, onlyEdit = true)
    @RestartHandlerOnChange
    public int getJpegPollTime() {
        return getJsonData("jpegPollTime", 1000);
    }

    public void setJpegPollTime(int value) {
        setJsonData("jpegPollTime", value);
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
    public String getSnapshotUrl() {
        return getJsonData("snapshotUrl", "ffmpeg");
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
    public OnvifCameraHandler createCameraHandler(EntityContext entityContext) {
        return new OnvifCameraHandler(this, entityContext);
    }

    @SneakyThrows
    public OnvifCameraBrandHandler getOnvifCameraBrandHandler() {
        if (onvifCameraBrandHandler == null) {
            onvifCameraBrandHandler = getCameraType().getCameraHandlerClass().getDeclaredConstructor(OnvifCameraEntity.class).newInstance(this);
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
        if (!getIp().equals(ip) || getOnvifPort() != port) {
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
        this.getCameraHandler().reboot();
        return null;
    }

    @Override
    public Set<DynamicContextMenuAction> getActions(EntityContext entityContext) {
        if (StringUtils.isNotEmpty(getIeeeAddress())) {
            return Collections.emptySet();
        }
        DynamicContextMenuAction authAction = new DynamicContextMenuAction("AUTHENTICATE",
                0, json -> {

            String user = json.getString("user");
            String password = json.getString("pwd");
            OnvifCameraEntity entity = this;
            OnvifConnection onvifConnection = new OnvifConnection(null, getIp(),  getOnvifPort(),
                    user, password);
            onvifConnection.setOnvifCameraActions(new OnvifCameraActions() {
                @Override
                public void onDeviceInformationReceived(OnvifConnection.GetDeviceInformationResponse deviceInformation) {
                    entityContext.updateDelayed(entity, e -> e.setUser(user).setPassword(password)
                            .setIeeeAddress(deviceInformation.getIeeeAddress()));
                    entityContext.ui().sendSuccessMessage("Onvif camera: " + getTitle() + " authenticated successfully");
                    onvifConnection.sendOnvifDeviceServiceRequest(OnvifConnection.RequestType.GetScopes);
                }

                @Override
                public void cameraUnreachable(String message) {
                    entityContext.ui().sendWarningMessage("Onvif camera: " + getTitle() + " unreachable");
                }

                @Override
                public void cameraFaultResponse(int code, String reason) {
                    entityContext.ui().sendWarningMessage("Onvif camera: " + getTitle() + " fault '" + code + "' response: " + reason);
                }

                @Override
                public void onCameraNameReceived(String name) {
                    entityContext.updateDelayed(entity, e -> e.setName(name));
                }
            });
            onvifConnection.sendOnvifDeviceServiceRequest(OnvifConnection.RequestType.GetDeviceInformation);
        }).setIcon("fas fa-sign-in-alt");
        authAction.addInput(ActionInputParameter.text("user", getUser()));
        authAction.addInput(ActionInputParameter.text("pwd", getPassword()));
        return Collections.singleton(authAction);
    }
}
