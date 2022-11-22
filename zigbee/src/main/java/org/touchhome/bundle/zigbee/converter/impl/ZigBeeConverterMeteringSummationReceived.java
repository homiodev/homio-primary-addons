package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * The total delivered from the metering system ZigBee channel converter for summation received measurement
 */
@ZigBeeConverter(name = "zigbee:metering_sumreceived", linkType = VariableType.Float,
    clientCluster = ZclMeteringCluster.CLUSTER_ID, category = "Number")
public class ZigBeeConverterMeteringSummationReceived extends ZigBeeConverterMeteringBaseConverter {

  public ZigBeeConverterMeteringSummationReceived() {
    super(ZclMeteringCluster.ATTR_CURRENTSUMMATIONRECEIVED);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return acceptEndpoint(endpoint, entityID, true, true);
  }
}
