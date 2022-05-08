package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclBinaryInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;

/**
 * Converter for the binary input sensor.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:binaryinput", clientClusters = {ZclBinaryInputBasicCluster.CLUSTER_ID})
public class ZigBeeConverterBinaryInput extends ZigBeeInputBaseConverter {

    public ZigBeeConverterBinaryInput() {
        super(ZclClusterType.BINARY_INPUT_BASIC, ZclBinaryInputBasicCluster.ATTR_PRESENTVALUE, 1,
                REPORTING_PERIOD_DEFAULT_MAX, null);
    }
}
