package org.homio.addon.camera.entity;

import static org.homio.api.util.CommonUtils.FFMPEG_LOCATION;

import java.util.List;
import jakarta.persistence.Entity;
import org.homio.api.EntityContext;
import org.homio.api.entity.RestartHandlerOnChange;
import org.homio.api.model.HasEntityLog;
import org.homio.api.model.OptionModel;
import org.homio.api.ui.action.DynamicOptionLoader;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldIgnore;
import org.homio.api.ui.field.UIFieldType;
import org.homio.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.homio.api.ui.field.selection.UIFieldSelection;
import org.homio.api.video.AbilityToStreamHLSOverFFMPEG;
import org.homio.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.api.video.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.homio.addon.camera.service.UsbCameraService;

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
      entityLogBuilder.addTopicFilterByEntityID("org.homio.addon.camera");
      entityLogBuilder.addTopicFilterByEntityID("org.homio.api.video");
  }

  public static class SelectAudioSource implements DynamicOptionLoader {

    @Override
    public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
      return OptionModel.list(parameters.getEntityContext().getBean(FfmpegInputDeviceHardwareRepository.class)
                                        .getAudioDevices(FFMPEG_LOCATION));
    }
  }
}
