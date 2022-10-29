package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * The level of cooling currently demanded by the thermostat
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_coolingdemand", clientClusters = {ZclThermostatCluster.CLUSTER_ID})
public class ZigBeeConverterThermostatPiCoolingDemand extends ZigBeeInputBaseConverter {

  /*private static BigDecimal CHANGE_DEFAULT = new BigDecimal(1);
  private static BigDecimal CHANGE_MIN = new BigDecimal(1);
  private static BigDecimal CHANGE_MAX = new BigDecimal(100);*/

  public ZigBeeConverterThermostatPiCoolingDemand() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_PICOOLINGDEMAND);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToPercentDimensionless((Integer) val));
  }
}
