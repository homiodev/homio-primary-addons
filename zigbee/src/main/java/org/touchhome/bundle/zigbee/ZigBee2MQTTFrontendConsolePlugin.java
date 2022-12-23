package org.touchhome.bundle.zigbee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginFrame;

@Getter
@RequiredArgsConstructor
public class ZigBee2MQTTFrontendConsolePlugin implements ConsolePluginFrame {

  private final EntityContext entityContext;
  private final FrameConfiguration value;

  @Override
  public int order() {
    return 500;
  }

  @Override
  public String getParentTab() {
    return "zigbee";
  }
}
