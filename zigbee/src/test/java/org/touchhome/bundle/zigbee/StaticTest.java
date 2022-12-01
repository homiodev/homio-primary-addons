package org.touchhome.bundle.zigbee;

import org.junit.jupiter.api.Test;
import org.touchhome.bundle.zigbee.util.ZigBeeDefineEndpoints;
import org.touchhome.bundle.zigbee.util.ClusterConfigurations;

public class StaticTest {

  @Test
  public void startupTest() {
    ZigBeeDefineEndpoints.getDefineEndpoints();
    ClusterConfigurations.getClusterConfigurations();
  }
}
