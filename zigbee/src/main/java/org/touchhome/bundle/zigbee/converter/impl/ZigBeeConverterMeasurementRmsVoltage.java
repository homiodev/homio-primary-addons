package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ELECTRICAL_MEASUREMENT;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

/**
 * The current RMS voltage measurement
 * ZigBee channel converter for RMS voltage measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:electrical_rmsvoltage", clientCluster = ZclElectricalMeasurementCluster.CLUSTER_ID,
    category = "Energy")
public class ZigBeeConverterMeasurementRmsVoltage extends ZigBeeInputBaseConverter {

  private Integer divisor;
  private Integer multiplier;
  public ZigBeeConverterMeasurementRmsVoltage() {
    super(ELECTRICAL_MEASUREMENT, ZclElectricalMeasurementCluster.ATTR_RMSVOLTAGE, 3,
        REPORTING_PERIOD_DEFAULT_MAX, 1);
  }

  static Integer determineDivisor(ZclCluster zclCluster) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(ZclMeteringCluster.ATTR_DIVISOR);
    Integer value = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? 1 : value;
  }

  static Integer determineMultiplier(ZclCluster zclCluster) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(ZclMeteringCluster.ATTR_MULTIPLIER);
    Integer value = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? 1 : value;
  }

  @Override
  protected void afterInitializeConverter() {
    this.divisor = determineDivisor(getZclCluster());
    this.multiplier = determineMultiplier(getZclCluster());
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = POLLING_PERIOD_HIGH;
    return true;
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    BigDecimal valueInVolts = BigDecimal.valueOf((long) value * multiplier / divisor);
    updateChannelState(new QuantityType<>(valueInVolts, Units.VOLT));
  }
}
