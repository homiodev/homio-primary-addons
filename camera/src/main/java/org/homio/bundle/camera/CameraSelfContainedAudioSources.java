package org.homio.bundle.camera;

import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.audio.SelfContainedAudioSourceContainer;
import org.homio.bundle.api.model.OptionModel;
import org.homio.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.bundle.camera.entity.OnvifCameraEntity;
import org.homio.bundle.camera.service.OnvifCameraService;
import org.onvif.ver10.schema.AudioSource;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CameraSelfContainedAudioSources implements SelfContainedAudioSourceContainer {

  private final EntityContext entityContext;

  @Override
  public Collection<OptionModel> getAudioSource() {
    Collection<OptionModel> models = new ArrayList<>();
    for (BaseFFMPEGVideoStreamEntity cameraEntity : entityContext.findAll(BaseFFMPEGVideoStreamEntity.class)) {
      // get sources from onvif audio streams
      if (cameraEntity.isStart() && cameraEntity instanceof OnvifCameraEntity) {
        OnvifCameraService service = (OnvifCameraService) cameraEntity.getService();
        for (AudioSource audioSource : service.getOnvifDeviceState().getMediaDevices().getAudioSources()) {
          models.add(OptionModel.of(audioSource.getToken()));
        }
      }
    }
    return models;
  }

  @Override
  public String getLabel() {
    return "CAMERA_SOURCES";
  }

  @Override
  public void play(String entityID) {

  }
}
