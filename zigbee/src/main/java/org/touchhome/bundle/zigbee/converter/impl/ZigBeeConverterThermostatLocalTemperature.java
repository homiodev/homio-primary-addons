package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * Indicates the local temperature provided by the thermostat Converter for the thermostat local temperature channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_localtemp",
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatLocalTemperature extends ZigBeeInputBaseConverter {

  private final int INVALID_TEMPERATURE = 0x8000;

  public ZigBeeConverterThermostatLocalTemperature() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_LOCALTEMPERATURE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("{}: ZigBee attribute reports {}", getEndpointEntity(), attribute);
    if (attribute.getClusterType() == ZclClusterType.THERMOSTAT
        && attribute.getId() == ZclThermostatCluster.ATTR_LOCALTEMPERATURE) {
      Integer value = (Integer) val;
      if (value != null && value != INVALID_TEMPERATURE) {
        updateChannelState(valueToTemperature(value));
      }
    }
  }
}
