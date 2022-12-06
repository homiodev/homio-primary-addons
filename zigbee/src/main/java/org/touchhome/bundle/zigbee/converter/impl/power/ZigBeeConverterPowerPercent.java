package org.touchhome.bundle.zigbee.converter.impl.power;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYPERCENTAGEREMAINING;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeInputBaseConverter;

/**
 * Converter for the battery percent channel.
 */
@ZigBeeConverter(name = "zigbee:battery_level", linkType = VariableType.Float,
    clientCluster = ZclPowerConfigurationCluster.CLUSTER_ID, category = "Battery")
public class ZigBeeConverterPowerPercent extends ZigBeeInputBaseConverter {

  public ZigBeeConverterPowerPercent() {
    super(POWER_CONFIGURATION, ATTR_BATTERYPERCENTAGEREMAINING);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(value / 2));
  }
}
