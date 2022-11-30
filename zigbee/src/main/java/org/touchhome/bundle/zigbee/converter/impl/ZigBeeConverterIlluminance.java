package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ILLUMINANCE_MEASUREMENT;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIlluminanceMeasurementCluster;
import java.util.Objects;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.config.ReportingChangeModel;
import org.touchhome.bundle.zigbee.converter.config.ZclReportingConfig;

/**
 * Indicates the current illuminance in lux Converter for the illuminance channel
 */
@ZigBeeConverter(name = "zigbee:measurement_illuminance", linkType = VariableType.Float,
    clientCluster = ZclIlluminanceMeasurementCluster.CLUSTER_ID, category = "Illuminance")
public class ZigBeeConverterIlluminance extends ZigBeeInputBaseConverter {

  public ZigBeeConverterIlluminance() {
    super(ILLUMINANCE_MEASUREMENT, ZclIlluminanceMeasurementCluster.ATTR_MEASUREDVALUE, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, Objects.requireNonNull(configReporting).getPollingPeriod());
  }

  @Override
  protected void initializeReportConfigurations() {
    configReporting = new ZclReportingConfig(getEntity());
  }

  @Override
  public ReportingChangeModel getReportingChangeModel() {
    return new ReportingChangeModel(5000, 10, 20000);
  }
}
