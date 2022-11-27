package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYPERCENTAGEREMAINING;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;

/**
 * Converter for the battery percent channel.
 */
@ZigBeeConverter(name = "zigbee:battery_level", linkType = VariableType.Float,
    clientCluster = ZclPowerConfigurationCluster.CLUSTER_ID, category = "Battery")
public class ZigBeeConverterBatteryPercent extends ZigBeeInputBaseConverter {

  public ZigBeeConverterBatteryPercent() {
    super(POWER_CONFIGURATION, ATTR_BATTERYPERCENTAGEREMAINING, 600,
        REPORTING_PERIOD_DEFAULT_MAX, 1, POLLING_PERIOD_HIGH);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(value / 2));
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, true, entityContext);
  }
}
