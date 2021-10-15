package org.touchhome.bundle.camera.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.util.Pair;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.entity.dependency.RequireExecutableDependency;
import org.touchhome.bundle.api.netty.HasBootstrapServer;
import org.touchhome.bundle.api.netty.NettyUtils;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.util.FFMPEGDependencyExecutableInstaller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RequireExecutableDependency(installer = FFMPEGDependencyExecutableInstaller.class)
public abstract class BaseFFmpegStreamEntity<T extends BaseFFmpegStreamEntity, H extends BaseFFmpegCameraHandler>
        extends BaseVideoCameraEntity<T, H> {

    // not all entity has user name
    public String getUser() {
        return getJsonData("user", "");
    }

    public T setUser(String value) {
        return setJsonData("user", value);
    }

    // not all entity has password
    public SecureString getPassword() {
        return new SecureString(getJsonData("password", ""));
    }

    public T setPassword(String value) {
        return setJsonData("password", value);
    }

    public String getAlarmInputUrl() {
        return getJsonData("alarmInputUrl", "");
    }

    public void setAlarmInputUrl(String value) {
        setJsonData("alarmInputUrl", value);
    }

    @Override
    @UIFieldIgnore
    public String getIeeeAddress() {
        return super.getIeeeAddress();
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

    @UIField(order = 110)
    @UIFieldSlider(min = 1, max = 100)
    public int getMotionThreshold() {
        return getJsonData("motionThreshold", 40);
    }

    public void setMotionThreshold(int value) {
        setJsonData("motionThreshold", value);
    }

    @UIField(order = 112)
    @UIFieldSlider(min = 1, max = 100)
    public int getAudioThreshold() {
        return getJsonData("audioThreshold", 40);
    }

    @UIField(order = 200)
    @UIFieldSlider(min = 1, max = 30)
    public int getSnapshotPollInterval() {
        return getJsonData("spi", 5);
    }

    public void setSnapshotPollInterval(int value) {
        setJsonData("spi", value);
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

    public String getHlsStreamUrl() {
        return "http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + getCameraHandler().getServerPort() + "/ipcamera.m3u8";
    }

    public String getSnapshotsMjpegUrl() {
        return "http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + getCameraHandler().getServerPort() + "/snapshots.mjpeg";
    }

    public String getAutofpsMjpegUrl() {
        return "http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + getCameraHandler().getServerPort() + "/autofps.mjpeg";
    }

    public String getImageUrl() {
        return "http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + getCameraHandler().getServerPort() + "/ipcamera.jpg";
    }

    public String getIpCameraMjpeg() {
        return "http://" + TouchHomeUtils.MACHINE_IP_ADDRESS + ":" + getCameraHandler().getServerPort() + "/ipcamera.mjpeg";
    }

    @Override
    public Collection<Pair<String, String>> getVideoSources() {
        return Arrays.asList(
                Pair.of("autofps.mjpeg", "autofps.mjpeg"),
                Pair.of("snapshots.mjpeg", "snapshots.mjpeg"),
                Pair.of("ipcamera.mjpeg", "ipcamera.mjpeg"),
                Pair.of("HLS", "HLS"));
    }

    @Override
    public String getStreamUrl(String source) {
        switch (source) {
            case "autofps.mjpeg":
                return getAutofpsMjpegUrl();
            case "snapshots.mjpeg":
                return getSnapshotsMjpegUrl();
            case "HLS":
                return getHlsStreamUrl();
            case "ipcamera.mjpeg":
                return getIpCameraMjpeg();
            case "image.jpg":
                return getImageUrl();
        }
        return null;
    }
}
