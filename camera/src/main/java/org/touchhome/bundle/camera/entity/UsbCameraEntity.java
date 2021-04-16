package org.touchhome.bundle.camera.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.camera.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.handler.impl.UsbCameraHandler;
import org.touchhome.bundle.camera.setting.CameraFFMPEGInstallPathOptions;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import javax.persistence.Entity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Accessors(chain = true)
public class UsbCameraEntity extends BaseFFmpegStreamEntity<UsbCameraEntity, UsbCameraHandler>
        implements AbilityToStreamHLSOverFFmpeg<UsbCameraEntity> {

    public static final String PREFIX = "usbcam_";

    @Override
    @UIField(order = 5, label = "usb")
    @RestartHandlerOnChange
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @Override
    @UIFieldIgnore
    public boolean isHasAudioStream() {
        return super.isHasAudioStream();
    }

    @UIField(order = 25, type = UIFieldType.TextSelectBoxDynamic)
    @UIFieldSelection(SelectAudioSource.class)
    @UIFieldSelectValueOnEmpty(label = "selection.audioSource", color = "#A7D21E")
    @RestartHandlerOnChange
    public String getAudioSource() {
        return getJsonData("asource");
    }

    public void setAudioSource(String value) {
        setJsonData("asource", value);
    }

    @UIField(order = 100, onlyEdit = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getStreamOptions() {
        return getJsonDataList("stream");
    }

    public void setStreamOptions(String value) {
        setJsonData("stream", value);
    }

    @UIField(order = 90, onlyEdit = true)
    @RestartHandlerOnChange
    public int getStreamStartPort() {
        return getJsonData("streamPort", 35001);
    }

    public void setStreamStartPort(int value) {
        setJsonData("streamPort", value);
    }

    @Override
    public UsbCameraHandler createCameraHandler(EntityContext entityContext) {
        return new UsbCameraHandler(this, entityContext);
    }

    @Override
    public String toString() {
        return "usb" + getTitle();
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    @Override
    protected void beforePersist() {
        super.beforePersist();
        setHlsVideoCodec("libx264");
        setSnapshotOutOptions("-vsync vfr~~~-q:v 2~~~-update 1~~~-frames:v 10");
        setStreamOptions("-vcodec libx264~~~-s 800x600~~~-bufsize:v 5M~~~-preset ultrafast~~~-vcodec libx264~~~-tune zerolatency~~~-b:v 2.5M");
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        super.afterFetch(entityContext);
        if (getStatus() == Status.UNKNOWN) {
            String ffmpegPath = entityContext.setting().getValue(CameraFFMPEGInstallPathOptions.class, Paths.get("ffmpeg")).toString();
            FfmpegInputDeviceHardwareRepository repository = entityContext.getBean(FfmpegInputDeviceHardwareRepository.class);
            Set<String> aliveVideoDevices = repository.getVideoDevices(ffmpegPath);
            if (aliveVideoDevices.contains(getIeeeAddress())) {
                entityContext.updateStatus(this, Status.ONLINE, null);
            } else {
                entityContext.updateStatus(this, Status.OFFLINE, "No usb camera available from ");
            }
        }
    }

    public static class SelectAudioSource implements DynamicOptionLoader {

        @Override
        public Collection<OptionModel> loadOptions(BaseEntity baseEntity, EntityContext entityContext) {
            Path path = entityContext.setting().getValue(CameraFFMPEGInstallPathOptions.class);
            return OptionModel.list(entityContext.getBean(FfmpegInputDeviceHardwareRepository.class)
                    .getAudioDevices(path.toString()));
        }
    }
}
