package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * The total delivered from the metering system ZigBee channel converter for summation delivered measurement
 */
@ZigBeeConverter(name = "zigbee:metering_sumdelivered", linkType = VariableType.Float,
    clientCluster = ZclMeteringCluster.CLUSTER_ID, category = "Number")
public class ZigBeeConverterMeteringSummationDelivered extends ZigBeeConverterMeteringBaseConverter {

  public ZigBeeConverterMeteringSummationDelivered() {
    super(ZclMeteringCluster.ATTR_CURRENTSUMMATIONDELIVERED);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, true, true);
  }
}
