package org.touchhome.bundle.camera.entity;

import static org.touchhome.bundle.api.util.TouchHomeUtils.FFMPEG_LOCATION;

import java.util.List;
import javax.persistence.Entity;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.model.HasEntityLog;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.video.AbilityToStreamHLSOverFFMPEG;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.touchhome.bundle.api.video.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.service.UsbCameraService;

@Entity
public class UsbCameraEntity extends BaseFFMPEGVideoStreamEntity<UsbCameraEntity, UsbCameraService>
    implements AbilityToStreamHLSOverFFMPEG<UsbCameraEntity>, HasEntityLog {

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
  @UIFieldSelectValueOnEmpty(label = "selection.audioSource")
  @RestartHandlerOnChange
  public String getAudioSource() {
    return getJsonData("asource");
  }

  public void setAudioSource(String value) {
    setJsonData("asource", value);
  }

  @UIField(order = 100, hideInView = true, type = UIFieldType.Chips)
  @RestartHandlerOnChange
  public List<String> getStreamOptions() {
    return getJsonDataList("stream");
  }

  public void setStreamOptions(String value) {
    setJsonData("stream", value);
  }

  @UIField(order = 90, hideInView = true)
  @RestartHandlerOnChange
  public int getStreamStartPort() {
    return getJsonData("streamPort", 35001);
  }

  public void setStreamStartPort(int value) {
    setJsonData("streamPort", value);
  }

  @Override
  public String getFolderName() {
    return "camera";
  }

  @Override
  public String toString() {
    return "usb" + getTitle();
  }

  @Override
  public String getDefaultName() {
    return "Usb camera";
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @Override
  protected void beforePersist() {
    super.beforePersist();
    setVideoCodec("libx264");
    setSnapshotOutOptions("-vsync vfr~~~-q:v 2~~~-update 1~~~-frames:v 10");
    setStreamOptions(
        "-vcodec libx264~~~-s 800x600~~~-bufsize:v 5M~~~-preset ultrafast~~~-vcodec libx264~~~-tune zerolatency~~~-b:v " +
            "2.5M");
  }

  @Override
  public Class<UsbCameraService> getEntityServiceItemClass() {
    return UsbCameraService.class;
  }

  @Override
  public UsbCameraService createService(EntityContext entityContext) {
    return new UsbCameraService(this, entityContext);
  }

  @Override
  public void logBuilder(EntityLogBuilder entityLogBuilder) {
    entityLogBuilder.addTopic("org.touchhome.bundle.camera", "entityID");
    entityLogBuilder.addTopic("org.touchhome.bundle.api.video", "entityID");
  }

  public static class SelectAudioSource implements DynamicOptionLoader {

    @Override
    public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
      return OptionModel.list(parameters.getEntityContext().getBean(FfmpegInputDeviceHardwareRepository.class)
          .getAudioDevices(FFMPEG_LOCATION));
    }
  }
}
