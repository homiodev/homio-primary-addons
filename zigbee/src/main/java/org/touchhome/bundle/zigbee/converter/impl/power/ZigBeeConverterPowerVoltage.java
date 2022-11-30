package org.touchhome.bundle.zigbee.converter.impl.power;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYVOLTAGE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeInputBaseConverter;
import tec.uom.se.unit.Units;

/**
 * Battery Voltage The current battery voltage
 */
@ZigBeeConverter(name = "zigbee:battery_voltage", linkType = VariableType.Float,
    clientCluster = ZclPowerConfigurationCluster.CLUSTER_ID, category = "Energy")
public class ZigBeeConverterPowerVoltage extends ZigBeeInputBaseConverter {

  public ZigBeeConverterPowerVoltage() {
    super(POWER_CONFIGURATION, ATTR_BATTERYVOLTAGE, 600, REPORTING_PERIOD_DEFAULT_MAX, 1, null);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    if (value == 0xFF) {
      // The value 0xFF indicates an invalid or unknown reading.
      return;
    }
    BigDecimal valueInVolt = BigDecimal.valueOf(value, 1);
    updateChannelState(new QuantityType<>(valueInVolt, Units.VOLT));
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, true, entityContext);
  }
}
