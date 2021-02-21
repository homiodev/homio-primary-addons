package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldPort;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.util.OnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Setter
@Getter
@Entity
@Accessors(chain = true)
public class OnvifCameraEntity extends BaseFFmpegStreamEntity<OnvifCameraEntity, OnvifCameraHandler>
        implements AbilityToStreamHLSOverFFmpeg<OnvifCameraEntity> {

    public static final String PREFIX = "onvifcam_";
    @Transient
    @JsonIgnore
    private int port;
    @Transient
    @JsonIgnore
    private String updateImageWhen = "";
    @Transient
    @JsonIgnore
    private OnvifCameraBrandHandler onvifCameraBrandHandler;

    @UIField(order = 11)
    @RestartHandlerOnChange
    public OnvifCameraType getCameraType() {
        return getJsonDataEnum("cameraType", OnvifCameraType.onvif);
    }

    public OnvifCameraEntity setCameraType(OnvifCameraType cameraType) {
        return setJsonDataEnum("cameraType", cameraType);
    }

    @Override
    @UIField(order = 12, type = UIFieldType.IpAddress, name = "ip", label = "cameraIpAddress")
    @RestartHandlerOnChange
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @JsonIgnore
    public String getIp() {
        return getIeeeAddress();
    }

    public OnvifCameraEntity setIp(String ip) {
        return setIeeeAddress(ip);
    }

    @UIFieldPort
    @UIField(order = 35)
    @RestartHandlerOnChange
    public int getOnvifPort() {
        return getJsonData("onvifPort", 0);
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
        return getJsonData("jpegPollTime", 0);
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
        return getJsonData("snapshotUrl", "");
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
        return getJsonData("mjpegUrl", "");
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
    protected void beforePersist() {
        setCameraType(OnvifCameraType.onvif);
        setUpdateImageWhen("0");
        setSnapshotUrl("ffmpeg");
        setMjpegUrl("ffmpeg");
        setJpegPollTime(1000);
        setOnvifPort(80);
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }
}
