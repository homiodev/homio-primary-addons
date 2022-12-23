package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.api.util.Units;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyIlluminance extends Z2MProperty {

  public Z2MPropertyIlluminance() {
    super("illuminance", "#89D636", "fa fa-fw fa-sun", Units.LUX);
  }
}
