package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.Objects;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ReportingChangeModel;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

/**
 * The level of heating currently demanded by the thermostat
 */
@ZigBeeConverter(name = "zigbee:thermostat_heatingdemand", linkType = VariableType.Float,
    clientCluster = ZclThermostatCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterThermostatPiHeatingDemand extends ZigBeeInputBaseConverter {

  public ZigBeeConverterThermostatPiHeatingDemand() {
    super(ZclClusterType.THERMOSTAT, ZclThermostatCluster.ATTR_PIHEATINGDEMAND, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, false, true, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, Objects.requireNonNull(configReporting).getPollingPeriod());
  }

  @Override
  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    super.initialize(endpointService, endpoint);
    configReporting = new ZclReportingConfig(getEntity());
  }

  @Override
  public ReportingChangeModel getReportingChangeModel() {
    return new ReportingChangeModel(1, 1, 100);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToPercentDimensionless((Integer) val));
  }
}
