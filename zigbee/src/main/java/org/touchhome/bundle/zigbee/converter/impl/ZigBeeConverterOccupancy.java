package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOccupancySensingCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;

/**
 * Indicates if an occupancy sensor is triggered
 * Converter for the occupancy sensor.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:sensor_occupancy", linkType = DeviceChannelLinkType.Boolean, clientCluster =
    ZclOccupancySensingCluster.CLUSTER_ID, category = "Motion")
public class ZigBeeConverterOccupancy extends ZigBeeInputBaseConverter {

  public ZigBeeConverterOccupancy() {
    super(ZclClusterType.OCCUPANCY_SENSING, ZclOccupancySensingCluster.ATTR_OCCUPANCY,
        1, REPORTING_PERIOD_DEFAULT_MAX, null);
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
