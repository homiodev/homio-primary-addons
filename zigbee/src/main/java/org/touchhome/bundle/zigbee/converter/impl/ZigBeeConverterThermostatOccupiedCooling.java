package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * Converter for the thermostat occupied cooling setpoint channel. This specifies the cooling mode setpoint when the room is occupied. It shall be set to a value in the range
 * defined by the MinCoolSetpointLimit and MaxCoolSetpointLimit attributes.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_occupiedcooling",
    clientClusters = {ZclThermostatCluster.CLUSTER_ID})
public class ZigBeeConverterThermostatOccupiedCooling extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatOccupiedCooling() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_OCCUPIEDCOOLINGSETPOINT,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        Integer value = temperatureToValue(command);

        if (value == null) {
            log.warn("{}/{}: Thermostat occupied cooling setpoint {} [{}] was not processed", endpoint.getIeeeAddress(), endpoint.getEndpointId(),
                    command, command.getClass().getSimpleName());
            return;
        }

        attribute.writeValue(value);
    }*/

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
  }
}
