package org.touchhome.bundle.camera;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.netty.NettyUtils;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.handler.BaseCameraHandler;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.CameraBrandHandlerDescription;
import org.touchhome.bundle.camera.scanner.OnvifCameraHttpScanner;
import org.touchhome.bundle.camera.setting.CameraStatusSetting;
import org.touchhome.bundle.camera.ui.RestartHandlerOnChange;

import java.lang.reflect.Method;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class CameraEntryPoint implements BundleEntryPoint {

    private final EntityContext entityContext;

    @SneakyThrows
    private static boolean detectIfRequireRestartHandler(Object oldCameraEntity, Object cameraEntity) {
        if (oldCameraEntity == null) { // in case if just created
            return false;
        }
        Method[] methods = MethodUtils.getMethodsWithAnnotation(cameraEntity.getClass(), RestartHandlerOnChange.class, true, false);
        for (Method method : methods) {
            Object newValue = MethodUtils.invokeMethod(cameraEntity, method.getName());
            Object oldValue = MethodUtils.invokeMethod(oldCameraEntity, method.getName());
            if (!Objects.equals(newValue, oldValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int order() {
        return 300;
    }

    public void init() {
        // fulfill camera brands
        for (Class<? extends BaseOnvifCameraBrandHandler> brandHandlerClass :
                entityContext.getClassesWithParent(BaseOnvifCameraBrandHandler.class, "org.touchhome.bundle.camera.onvif")) {
            CameraCoordinator.cameraBrands.put(brandHandlerClass.getSimpleName(),
                    new CameraBrandHandlerDescription(brandHandlerClass));
        }

        // listen if camera entity removed
        entityContext.event().addEntityRemovedListener(BaseVideoCameraEntity.class, "camera-remove-listener", cameraEntity -> {
            CameraCoordinator.removeSpdMessage(cameraEntity.getIeeeAddress());
            // remove camera handler
            BaseCameraHandler handler = NettyUtils.removeBootstrapServer(cameraEntity.getEntityID());
            if (handler != null) {
                handler.dispose();
                handler.deleteDirectories();
            }
        });

        // lister start/stop status, changes
        entityContext.event().addEntityUpdateListener(BaseVideoCameraEntity.class, "camera-change-listener", (cameraEntity, oldCameraEntity) -> {
            BaseCameraHandler cameraHandler = cameraEntity.getCameraHandler();
            if (cameraEntity.isStart() && !cameraHandler.isHandlerInitialized()) {
                cameraHandler.initialize(cameraEntity);
            } else if (!cameraEntity.isStart() && cameraHandler.isHandlerInitialized()) {
                cameraHandler.disposeAndSetStatus(Status.OFFLINE, "Camera not started");
            } else if (cameraHandler.isHandlerInitialized() && detectIfRequireRestartHandler(oldCameraEntity, cameraEntity)) {
                cameraHandler.restart("Restart camera handler", cameraEntity, true);
            } else {
                cameraHandler.setCameraEntity(cameraEntity); // to avoid optimistic lock
            }
            // change camera name if possible
            if (oldCameraEntity != null && !Objects.equals(cameraEntity.getName(), oldCameraEntity.getName())) {
                if (cameraHandler instanceof OnvifCameraHandler) {
                    ((OnvifCameraHandler) cameraHandler).getOnvifDeviceState().getInitialDevices().setName(cameraEntity.getName());
                }
            }
        });

        // fire initialize camera if start is true
        for (BaseVideoCameraEntity cameraEntity : entityContext.findAll(BaseVideoCameraEntity.class)) {
            if (cameraEntity.isStart()) {
                cameraEntity.getCameraHandler().initialize(cameraEntity);
            }
        }

        // fire rescan whole possible items to see if ip address has been changed
        entityContext.getBean(OnvifCameraHttpScanner.class).executeScan(entityContext, null, null, true);
    }

    @Override
    public Class<? extends SettingPluginStatus> getBundleStatusSetting() {
        return CameraStatusSetting.class;
    }
}
