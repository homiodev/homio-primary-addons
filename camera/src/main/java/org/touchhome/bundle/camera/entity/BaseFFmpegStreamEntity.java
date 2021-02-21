package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.touchhome.bundle.api.entity.dependency.RequireExecutableDependency;
import org.touchhome.bundle.api.netty.HasBootstrapServer;
import org.touchhome.bundle.api.netty.NettyUtils;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;
import org.touchhome.bundle.camera.util.FFMPEGDependencyExecutableInstaller;

import java.util.List;

@RequireExecutableDependency(name = "ffmpeg", installer = FFMPEGDependencyExecutableInstaller.class)
public abstract class BaseFFmpegStreamEntity<T extends BaseFFmpegStreamEntity, H extends BaseFFmpegCameraHandler>
        extends BaseVideoCameraEntity<T, H> {

    // not all entity has user name
    public String getUser() {
        return getJsonData("user", "");
    }

    public void setUser(String value) {
        setJsonData("user", value);
    }

    // not all entity has password
    public String getPassword() {
        return getJsonData("password", "");
    }

    public void setPassword(String value) {
        setJsonData("password", value);
    }

    public String getAlarmInputUrl() {
        return getJsonData("alarmInputUrl", "");
    }

    public void setAlarmInputUrl(String value) {
        setJsonData("alarmInputUrl", value);
    }

    @UIField(order = 125, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getGifOutOptions() {
        return getJsonDataList("gifOutOptions");
    }

    public void setGifOutOptions(String value) {
        setJsonData("gifOutOptions", value);
    }

    @UIField(order = 130, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getMjpegOutOptions() {
        return getJsonDataList("mjpegOutOptions");
    }

    public void setMjpegOutOptions(String value) {
        setJsonData("mjpegOutOptions", value);
    }

    @UIField(order = 135, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getSnapshotOutOptions() {
        return getJsonDataList("imgOutOptions");
    }

    public void setSnapshotOutOptions(String value) {
        setJsonData("imgOutOptions", value);
    }

    @JsonIgnore
    public String getSnapshotOutOptionsAsString() {
        return String.join(" ", getSnapshotOutOptions());
    }

    @UIField(order = 140, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getMotionOptions() {
        return getJsonDataList("motionOptions");
    }

    public void setMotionOptions(String value) {
        setJsonData("motionOptions", value);
    }

    @UIField(order = 145, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public int getGifPreroll() {
        return getJsonData("gifPreroll", 0);
    }

    public void setGifPreroll(int value) {
        setJsonData("gifPreroll", value);
    }

    @UIField(order = 110, inlineEdit = true)
    @UIFieldSlider(min = 0, max = 1000)
    @RestartHandlerOnChange
    public int getMotionThreshold() {
        return getJsonData("motionThreshold", 250);
    }

    public void setMotionThreshold(int value) {
        setJsonData("motionThreshold", value);
    }

    @UIField(order = 112)
    @UIFieldSlider(min = 0, max = 100)
    @RestartHandlerOnChange
    public int getAudioThreshold() {
        return getJsonData("audioThreshold", 35);
    }

    public void setAudioThreshold(int value) {
        setJsonData("audioThreshold", value);
    }

    @UIField(order = 160, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getMp4OutOptions() {
        return getJsonDataList("mp4OutOptions");
    }

    public void setMp4OutOptions(String value) {
        setJsonData("mp4OutOptions", value);
    }

    @Override
    protected void beforePersist() {
        setGifPreroll(0);
        setMp4OutOptions("-c:v copy~~~-c:a copy");
        setMjpegOutOptions("-q:v 5~~~-r 2~~~-vf scale=640:-2~~~-update 1");
        setSnapshotOutOptions("-vsync vfr~~~-q:v 2~~~-update 1~~~-frames:v 1");
        setGifOutOptions("-r 2~~~-filter_complex scale=-2:360:flags=lanczos,setpts=0.5*PTS,split[o1][o2];[o1]palettegen[p];[o2]fifo[o3];[o3][p]paletteuse");
        setServerPort(NettyUtils.findFreeBootstrapServerPort());
    }

    @Override
    protected void beforeUpdate() {
        super.beforeUpdate();
        HasBootstrapServer server = NettyUtils.getServerByPort(getEntityID(), getServerPort());
        if (server != null) {
            throw new RuntimeException("Unable to save camera entity: " + getTitle() + ". Server port: " + getServerPort() + " already in use by: " +
                    server.getName());
        }
    }

    @Override
    public H getCameraHandler() {
        return super.getCameraHandler();
    }
}