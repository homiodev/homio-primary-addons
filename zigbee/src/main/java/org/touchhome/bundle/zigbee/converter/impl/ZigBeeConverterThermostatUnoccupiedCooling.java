package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Set the cooling temperature when the room is unoccupied Converter for the thermostat unoccupied cooling setpoint channel
 */
@ZigBeeConverter(name = "zigbee:thermostat_unoccupiedcooling", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatUnoccupiedCooling extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatUnoccupiedCooling() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_UNOCCUPIEDCOOLINGSETPOINT,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, false, true, entityContext);
  }
    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        Integer value = temperatureToValue(command);

        if (value == null) {
            log.warn("[{}]: Thermostat unoccupied cooling setpoint {} [{}] was not processed",
                    getEndpointEntity(), command, command.getClass().getSimpleName());
            return;
        }

        attribute.writeValue(value);
    }*/

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
  }
}
