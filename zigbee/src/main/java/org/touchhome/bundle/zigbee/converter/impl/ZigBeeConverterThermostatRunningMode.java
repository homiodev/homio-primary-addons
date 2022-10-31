package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * The running mode of the thermostat
 * Converter for the thermostat running mode channel. This is a read-only channel the presents the current state of the thermostat.
 * <p>
 * ThermostatRunningMode represents the running mode of the thermostat. The thermostat running mode can only be Off, Cool or Heat. This attribute is intended to provide additional
 * information when the thermostat’s system mode is in auto mode. The attribute value is maintained to have the same value as the SystemMode attribute.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_runningmode",
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatRunningMode extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatRunningMode() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_THERMOSTATRUNNINGMODE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }
}
