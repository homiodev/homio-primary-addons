package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclAnalogInputBasicCluster.ATTR_DESCRIPTION;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclAnalogInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ReportingChangeModel;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

/**
 * Analog Input Sensor(Switch) Indicates a analog input sensor state Converter for the binary input sensor.
 */
@ZigBeeConverter(name = "zigbee:analoginput", linkType = VariableType.Float,
    clientCluster = ZclAnalogInputBasicCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterAnalogInput extends ZigBeeInputBaseConverter {

  public ZigBeeConverterAnalogInput() {
    super(ZclClusterType.ANALOG_INPUT_BASIC, ZclAnalogInputBasicCluster.ATTR_PRESENTVALUE, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  public ReportingChangeModel getReportingChangeModel() {
    return new ReportingChangeModel(1, 1, 100);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    super.initialize(endpointService, endpoint);
    configReporting = new ZclReportingConfig(getEntity());
  }
}
