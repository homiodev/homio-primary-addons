package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import lombok.extern.log4j.Log4j2;

/**
 * ZigBee channel converter for summation delivered measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:metering_sumdelivered", clientClusters = {ZclMeteringCluster.CLUSTER_ID})
public class ZigBeeConverterMeteringSummationDelivered extends ZigBeeConverterMeteringInstantaneousDemand {

  public ZigBeeConverterMeteringSummationDelivered() {
    super(ZclMeteringCluster.ATTR_CURRENTSUMMATIONDELIVERED);
  }
}
