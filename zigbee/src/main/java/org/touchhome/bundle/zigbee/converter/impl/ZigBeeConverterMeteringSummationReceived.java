package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import lombok.extern.log4j.Log4j2;

/**
 * ZigBee channel converter for summation received measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:metering_sumreceived", clientClusters = {ZclMeteringCluster.CLUSTER_ID})
public class ZigBeeConverterMeteringSummationReceived extends ZigBeeConverterMeteringInstantaneousDemand {

    @Override
    public int getInputAttributeId() {
        return ZclMeteringCluster.ATTR_CURRENTSUMMATIONRECEIVED;
    }
}
