package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;

@Component
@RequiredArgsConstructor
public class RaspberryEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  public void init() {
    entityContext.var().createGroup("rpi", "Raspberry", true, "fab fa-raspberry-pi", "#C70039");
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
