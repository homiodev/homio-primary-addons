package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.raspberry.console.GpioConsolePlugin;

@Component
@RequiredArgsConstructor
public class RaspberryEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  public void init() {
    entityContext.getBean(RaspberryGPIOService.class).init();
    entityContext.getBean(GpioConsolePlugin.class).init();
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
