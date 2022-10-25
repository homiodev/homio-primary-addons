package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYPERCENTAGEREMAINING;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;

/**
 * Converter for the battery percent channel.
 */
@Log4j2
@ZigBeeConverter(name = "system:battery-level", clientClusters = {ZclPowerConfigurationCluster.CLUSTER_ID})
public class ZigBeeConverterBatteryPercent extends ZigBeeInputBaseConverter {

  public ZigBeeConverterBatteryPercent() {
    super(POWER_CONFIGURATION, ATTR_BATTERYPERCENTAGEREMAINING, 600,
        REPORTING_PERIOD_DEFAULT_MAX, 1);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(value / 2));
  }
}
