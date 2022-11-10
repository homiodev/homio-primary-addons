package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import lombok.extern.log4j.Log4j2;

/**
 * The total delivered from the metering system ZigBee channel converter for summation received measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:metering_sumreceived", clientCluster = ZclMeteringCluster.CLUSTER_ID, category = "Number")
public class ZigBeeConverterMeteringSummationReceived extends ZigBeeConverterMeteringBaseConverter {

  public ZigBeeConverterMeteringSummationReceived() {
    super(ZclMeteringCluster.ATTR_CURRENTSUMMATIONRECEIVED);
  }
}
