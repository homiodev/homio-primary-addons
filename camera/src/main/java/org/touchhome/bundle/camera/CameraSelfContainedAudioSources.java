package org.touchhome.bundle.camera;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.onvif.ver10.schema.AudioSource;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.audio.SelfContainedAudioSourceContainer;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@Component
@RequiredArgsConstructor
public class CameraSelfContainedAudioSources implements SelfContainedAudioSourceContainer {

    private final EntityContext entityContext;

    @Override
    public Collection<OptionModel> getAudioSource() {
        Collection<OptionModel> models = new ArrayList<>();
        for (BaseVideoCameraEntity cameraEntity : entityContext.findAll(BaseVideoCameraEntity.class)) {
            // get sources from onvif audio streams
            if (cameraEntity.isStart() && cameraEntity instanceof OnvifCameraEntity) {
                OnvifCameraHandler cameraHandler = (OnvifCameraHandler) cameraEntity.getCameraHandler();
                for (AudioSource audioSource : cameraHandler.getOnvifDeviceState().getMediaDevices().getAudioSources()) {
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
