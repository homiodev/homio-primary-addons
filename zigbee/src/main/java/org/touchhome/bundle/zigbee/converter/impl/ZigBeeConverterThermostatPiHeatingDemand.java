package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * The level of heating currently demanded by the thermostat
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_heatingdemand", clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatPiHeatingDemand extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatPiHeatingDemand() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_PIHEATINGDEMAND);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToPercentDimensionless((Integer) val));
  }
}
