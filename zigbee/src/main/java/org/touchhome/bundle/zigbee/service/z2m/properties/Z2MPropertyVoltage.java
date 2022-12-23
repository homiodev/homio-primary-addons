package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.api.util.Units;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyVoltage extends Z2MProperty {

  public Z2MPropertyVoltage() {
    super("voltage", "#32D1B9", "fa fa-fw fa-bolt", Units.MILLI_VOLT);
  }
}
