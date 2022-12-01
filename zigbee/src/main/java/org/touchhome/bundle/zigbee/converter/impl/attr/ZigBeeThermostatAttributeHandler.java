package org.touchhome.bundle.zigbee.converter.impl.attr;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster.ATTR_PICOOLINGDEMAND;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster.ATTR_PIHEATINGDEMAND;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster.ATTR_SYSTEMMODE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster.ATTR_THERMOSTATRUNNINGMODE;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import tec.uom.se.unit.Units;

public class ZigBeeThermostatAttributeHandler extends AttributeHandler {

  @Override
  public @Nullable State convertValue(Object val) {
    Integer value = (Integer) val;
    switch (zclAttribute.getId()) {
      case ATTR_PICOOLINGDEMAND:
      case ATTR_PIHEATINGDEMAND:
        return new QuantityType<>(value, Units.PERCENT);
      case ATTR_SYSTEMMODE:
      case ATTR_THERMOSTATRUNNINGMODE:
        return new DecimalType(value);
      default:
        return value == 0x8000 ? null : new QuantityType<>(BigDecimal.valueOf(value, 2), Units.CELSIUS);
    }
  }
}
