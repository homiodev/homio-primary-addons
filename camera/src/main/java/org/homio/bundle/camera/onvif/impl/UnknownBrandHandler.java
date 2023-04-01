package org.homio.bundle.camera.onvif.impl;

import org.homio.bundle.camera.onvif.brand.BaseOnvifCameraBrandHandler;
import org.homio.bundle.camera.service.OnvifCameraService;

public class UnknownBrandHandler extends BaseOnvifCameraBrandHandler {

  public UnknownBrandHandler(OnvifCameraService service) {
    super(service);
  }
}
