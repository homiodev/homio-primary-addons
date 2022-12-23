package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.api.util.Units;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyBattery extends Z2MProperty {

  public Z2MPropertyBattery() {
    super("battery", "#32D1B9", "fa fa-fw fa-battery-full", Units.PERCENT);
  }
}
