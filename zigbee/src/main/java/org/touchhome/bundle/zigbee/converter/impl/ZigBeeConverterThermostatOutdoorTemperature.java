package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * Indicates the outdoor temperature provided by the thermostat
 * Converter for the thermostat outdoor temperature channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_outdoortemp",
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatOutdoorTemperature extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatOutdoorTemperature() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_OUTDOORTEMPERATURE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
  }
}
