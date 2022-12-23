package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;
import tech.units.indriya.unit.Units;

public class Z2MPropertyTemperature extends Z2MProperty {

  public Z2MPropertyTemperature() {
    super("device_temperature", "#36B9D6", "fa fa-fw fa-thermometer-three-quarters", Units.CELSIUS);
  }
}
