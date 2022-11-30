package org.touchhome.bundle.zigbee.converter.impl.thermostat;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeInputBaseConverter;

/**
 * Indicates the local temperature provided by the thermostat Converter for the thermostat local temperature channel
 */
@ZigBeeConverter(name = "zigbee:thermostat_localtemp", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatLocalTemperature extends ZigBeeInputBaseConverter {

  private final int INVALID_TEMPERATURE = 0x8000;

  public ZigBeeConverterThermostatLocalTemperature() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_LOCALTEMPERATURE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, 10, null);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() == ZclClusterType.THERMOSTAT
        && attribute.getId() == ZclThermostatCluster.ATTR_LOCALTEMPERATURE) {
      Integer value = (Integer) val;
      if (value != null && value != INVALID_TEMPERATURE) {
        updateChannelState(valueToTemperature(value));
      }
    }
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, false, true, entityContext);
  }
}
