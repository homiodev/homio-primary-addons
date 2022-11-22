package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * The running mode of the thermostat Converter for the thermostat running mode channel. This is a read-only channel the presents the current state of the thermostat.
 * <p>
 * ThermostatRunningMode represents the running mode of the thermostat. The thermostat running mode can only be Off, Cool or Heat. This attribute is intended to provide additional
 * information when the thermostat’s system mode is in auto mode. The attribute value is maintained to have the same value as the SystemMode attribute.
 */
@ZigBeeConverter(name = "zigbee:thermostat_runningmode", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatRunningMode extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatRunningMode() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_THERMOSTATRUNNINGMODE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, false, true);
  }
}
