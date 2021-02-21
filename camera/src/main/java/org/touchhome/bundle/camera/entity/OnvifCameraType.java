package org.touchhome.bundle.camera.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.model.HasDescription;
import org.touchhome.bundle.camera.onvif.impl.*;
import org.touchhome.bundle.camera.onvif.util.OnvifCameraBrandHandler;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.AMCREST_HANDLER;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.INSTAR_HANDLER;

@Getter
@RequiredArgsConstructor
public enum OnvifCameraType implements HasDescription {
    onvif(HttpOnlyBrandHandler.class, null),
    amcrest(AmcrestBrandHandler.class, AMCREST_HANDLER),
    dahua(DahuaBrandHandler.class, null),
    doorbird(DoorBirdBrandHandler.class, null),
    foscam(FoscamBrandHandler.class, null),
    hikvision(HikvisionBrandHandler.class, null),
    instar(InstarBrandHandler.class, INSTAR_HANDLER);

    private final Class<? extends OnvifCameraBrandHandler> cameraHandlerClass;
    private final String handlerName;

    @Override
    public String getDescription() {
        return Lang.getServerMessage("description." + name());
    }
}
