package org.touchhome.bundle.zigbee;

import org.junit.jupiter.api.Test;
import org.touchhome.bundle.zigbee.util.ClusterConfigurations;
import org.touchhome.bundle.zigbee.util.DeviceConfigurations;

public class StaticTest {

  @Test
  public void startupTest() {
    DeviceConfigurations.getDefineEndpoints();
    ClusterConfigurations.getClusterConfigurations();
  }
}
