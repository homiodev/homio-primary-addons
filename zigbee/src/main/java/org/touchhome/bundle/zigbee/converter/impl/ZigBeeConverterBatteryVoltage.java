package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYVOLTAGE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

/**
 * Converter for the battery voltage channel.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:battery_voltage", clientClusters = {ZclPowerConfigurationCluster.CLUSTER_ID})
public class ZigBeeConverterBatteryVoltage extends ZigBeeInputBaseConverter {

  public ZigBeeConverterBatteryVoltage() {
    super(POWER_CONFIGURATION, ATTR_BATTERYVOLTAGE, 600, REPORTING_PERIOD_DEFAULT_MAX, 1);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    if (value == 0xFF) {
      // The value 0xFF indicates an invalid or unknown reading.
      return;
    }
    BigDecimal valueInVolt = BigDecimal.valueOf(value, 1);
    updateChannelState(new QuantityType<>(valueInVolt, Units.VOLT));
  }
}
