package org.touchhome.bundle.camera.onvif.brand;

import lombok.SneakyThrows;
import org.touchhome.bundle.camera.onvif.impl.CameraBrandHandler;
import org.touchhome.bundle.camera.onvif.impl.OnvifBrandHandler;
import org.touchhome.bundle.camera.service.OnvifCameraService;

public class CameraBrandHandlerDescription {

  public static CameraBrandHandlerDescription DEFAULT_BRAND = new CameraBrandHandlerDescription(OnvifBrandHandler.class);

  private final Class<? extends BaseOnvifCameraBrandHandler> brandHandler;
  private final CameraBrandHandler cameraBrandHandler;

  @SneakyThrows
  public CameraBrandHandlerDescription(Class<? extends BaseOnvifCameraBrandHandler> brandHandler) {
    this.brandHandler = brandHandler;
    this.cameraBrandHandler = brandHandler.getDeclaredAnnotation(CameraBrandHandler.class);
  }

  public String getID() {
    return brandHandler.getSimpleName();
  }

  public String getName() {
    return cameraBrandHandler.name();
  }

  public String getHandlerName() {
    return cameraBrandHandler.handlerName();
  }

  @SneakyThrows
  public BaseOnvifCameraBrandHandler newInstance(OnvifCameraService service) {
    return brandHandler.getConstructor(OnvifCameraService.class).newInstance(service);
  }
}
