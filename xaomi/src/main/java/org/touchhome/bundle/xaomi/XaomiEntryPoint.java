package org.touchhome.bundle.xaomi;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;

@Log4j2
@Component
@RequiredArgsConstructor
public class XaomiEntryPoint implements BundleEntryPoint {

  public void init() {

  }

  @Override
  public int order() {
    return 700;
  }
}
