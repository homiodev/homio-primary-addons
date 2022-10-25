package org.touchhome.bundle.camera;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.onvif.ver10.schema.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

@Log4j2
@RestController
@RequestMapping("/rest/camera")
@RequiredArgsConstructor
public class CameraController {

  private final EntityContext entityContext;

  @GetMapping("/ffmpegWithProfiles")
  public List<OptionModel> getAllFFmpegWithProfiles() {
    List<OptionModel> list = new ArrayList<>();
    for (BaseFFMPEGVideoStreamEntity videoStreamEntity : entityContext.findAll(BaseFFMPEGVideoStreamEntity.class)) {
      if (videoStreamEntity.getStatus() == Status.ONLINE && videoStreamEntity.isStart()) {
        if (videoStreamEntity instanceof OnvifCameraEntity) {
          for (Profile profile : ((OnvifCameraHandler) videoStreamEntity.getVideoHandler()).getOnvifDeviceState()
              .getProfiles()) {
            list.add(OptionModel.of(videoStreamEntity.getEntityID() + "/" + profile.getToken(),
                videoStreamEntity.getTitle() + " (" +
                    profile.getVideoEncoderConfiguration().getResolution().toString() + ")"));
          }
        } else {
          list.add(OptionModel.of(videoStreamEntity.getEntityID(), videoStreamEntity.getTitle()));
        }
      }
    }
    return list;
  }

    /*@GetMapping("/profiles")
    public List<OptionModel> getCameraProfiles(@RequestParam(name = FFMPEG_CAMERA_MENU, required = false) String cameraEntityID) {
        if (cameraEntityID != null) {
            BaseFFMPEGVideoStreamEntity entity = entityContext.getEntity(cameraEntityID);
            if (entity instanceof OnvifCameraEntity) {
                return ((OnvifCameraHandler) entity.getVideoHandler()).getOnvifDeviceState().getProfiles()
                        .stream().map(s -> OptionModel.of(s.getToken(), s.getVideoEncoderConfiguration().getResolution()
                        .toString())).collect(Collectors.toList());
            }
        }
        return OptionModel.list("default");
    }*/
}
