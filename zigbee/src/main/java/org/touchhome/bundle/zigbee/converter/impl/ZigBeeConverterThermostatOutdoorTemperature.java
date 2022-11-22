package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Indicates the outdoor temperature provided by the thermostat Converter for the thermostat outdoor temperature channel
 */
@ZigBeeConverter(name = "zigbee:thermostat_outdoortemp", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatOutdoorTemperature extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatOutdoorTemperature() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_OUTDOORTEMPERATURE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, false, true);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
  }
}
