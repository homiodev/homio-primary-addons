package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOccupancySensingCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.OnOffType;


/**
 * Indicates if an occupancy sensor is triggered Converter for the occupancy sensor.
 */
@ZigBeeConverter(name = "zigbee:sensor_occupancy", linkType = VariableType.Boolean,
    clientCluster = ZclOccupancySensingCluster.CLUSTER_ID, category = "Motion")
public class ZigBeeConverterOccupancy extends ZigBeeInputBaseConverter {

  public ZigBeeConverterOccupancy() {
    super(ZclClusterType.OCCUPANCY_SENSING, ZclOccupancySensingCluster.ATTR_OCCUPANCY,
        1, REPORTING_PERIOD_DEFAULT_MAX, null, null);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    if (value != null && value == 1) {
      updateChannelState(OnOffType.ON);
    } else {
      updateChannelState(OnOffType.OFF);
    }
  }
}
