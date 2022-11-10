package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * Set the heating temperature when the room is occupied Converter for the thermostat occupied heating setpoint channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:thermostat_occupiedheating",
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatOccupiedHeating extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatOccupiedHeating() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_OCCUPIEDHEATINGSETPOINT,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        Integer value = temperatureToValue(command);

        if (value == null) {
            log.warn("{}: Thermostat occupied heating setpoint {} [{}] was not processed", getEndpointEntity(),
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
