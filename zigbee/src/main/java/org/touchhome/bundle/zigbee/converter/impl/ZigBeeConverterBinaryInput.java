package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclBinaryInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Binary Input Sensor(Switch) Indicates a binary input sensor state Converter for the binary input sensor.
 */
@ZigBeeConverter(name = "zigbee:binaryinput", linkType = VariableType.Float,
    clientCluster = ZclBinaryInputBasicCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterBinaryInput extends ZigBeeInputBaseConverter {

  public ZigBeeConverterBinaryInput() {
    super(ZclClusterType.BINARY_INPUT_BASIC, ZclBinaryInputBasicCluster.ATTR_PRESENTVALUE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, null, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }
}
