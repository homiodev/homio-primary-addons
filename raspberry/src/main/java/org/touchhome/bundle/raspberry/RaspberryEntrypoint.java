package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.raspberry.gpio.GpioPin;

@Component
@RequiredArgsConstructor
public class RaspberryEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  public void init() {
    for (RaspberryDeviceEntity entity : entityContext.findAll(RaspberryDeviceEntity.class)) {
      createVariableGroup(entity);
    }
    entityContext.event().addEntityCreateListener(RaspberryDeviceEntity.class, "rpi-gen-create", this::createVariableGroup);
    entityContext.event().addEntityRemovedListener(RaspberryDeviceEntity.class, "rpi-gen-drop",  entity -> {
      entityContext.var().removeGroup(entity.getEntityID());
    });
  }

  private void createVariableGroup(RaspberryDeviceEntity entity) {
    entityContext.var().createGroup(entity.getEntityID(), "Raspberry[" + entity.getTitle() + "]", true, "fab fa-raspberry-pi", "#C70039");
    // ensure variable exists
    for (GpioPin gpioPin : RaspberryGpioPin.getGpioPins()) {
      entityContext.var().createVariable(entity.getEntityID(), "rpi_" + entity.getEntityID() + "_" + gpioPin.getAddress(),
          gpioPin.getName(), VariableType.Boolean, gpioPin.getDescription(), gpioPin.getColor());
    }
  }

  @Override
  public int order() {
    return 300;
  }

  @Override
  public BundleImageColorIndex getBundleImageColorIndex() {
    return BundleImageColorIndex.ONE;
  }
}
