package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Set the heating temperature when the room is unoccupied Converter for the thermostat unoccupied heating setpoint channel
 */
@ZigBeeConverter(name = "zigbee:thermostat_unoccupiedheating", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatUnoccupiedHeating extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatUnoccupiedHeating() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_UNOCCUPIEDHEATINGSETPOINT,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, false, true);
  }
    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        Integer value = temperatureToValue(command);

        if (value == null) {
            log.warn("[{}]: Thermostat unoccupied heating setpoint {} [{}] was not processed",
                    getEndpointEntity(), command, command.getClass().getSimpleName());
            return;
        }

        attribute.writeValue(value);
    }*/

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
  }
}
