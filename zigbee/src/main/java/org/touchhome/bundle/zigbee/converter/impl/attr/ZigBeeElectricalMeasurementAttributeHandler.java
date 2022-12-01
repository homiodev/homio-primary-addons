package org.touchhome.bundle.zigbee.converter.impl.attr;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster.ATTR_ACPOWERDIVISOR;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster.ATTR_ACPOWERMULTIPLIER;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster.ATTR_ACTIVEPOWER;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster.ATTR_RMSCURRENT;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster.ATTR_RMSVOLTAGE;

import java.math.BigDecimal;
import javax.measure.Unit;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import tec.uom.se.unit.Units;

public class ZigBeeElectricalMeasurementAttributeHandler extends AttributeHandler {

  private Integer divisor;
  private Integer multiplier;

  @Override
  public void initialize(boolean bindSuccess) throws Exception {
    super.initialize(bindSuccess);
    this.divisor = readAttribute(zclCluster, ATTR_ACPOWERDIVISOR, 1);
    this.multiplier = readAttribute(zclCluster, ATTR_ACPOWERMULTIPLIER, 1);
  }

  @Override
  public State convertValue(Object val) {
    Integer value = (Integer) val;
    BigDecimal valueInWatt = BigDecimal.valueOf((long) value * multiplier / divisor);
    return new QuantityType<>(valueInWatt, getUnit());
  }

  private Unit getUnit() {
    switch (zclAttribute.getId()) {
      case ATTR_RMSCURRENT:
        return Units.AMPERE;
      case ATTR_RMSVOLTAGE:
        return Units.VOLT;
      case ATTR_ACTIVEPOWER:
        return Units.WATT;
      default:
        return null;
    }
  }
}
