package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMultistateInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

@ZigBeeConverter(name = "zigbee:multistateinput", linkType = VariableType.Float,
    clientCluster = ZclMultistateInputBasicCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterMultiStateInput extends ZigBeeInputBaseConverter {

  public ZigBeeConverterMultiStateInput() {
    super(ZclClusterType.MULTISTATE_INPUT_BASIC, ZclMultistateInputBasicCluster.ATTR_PRESENTVALUE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, null, POLLING_PERIOD_HIGH);
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
