package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;

/**
 * The level of heating currently demanded by the thermostat
 */
@ZigBeeConverter(name = "zigbee:thermostat_heatingdemand", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatPiHeatingDemand extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatPiHeatingDemand() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_PIHEATINGDEMAND);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, false, true);
  }

  @Override
  protected void afterInitializeConverter() {
    configReporting = new ZclReportingConfig(getEntity(), 1, 1, 100);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToPercentDimensionless((Integer) val));
  }
}
