package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclMultistateInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

@ZigBeeConverter(name = "zigbee:multistateinput", linkType = VariableType.Float,
                 clientCluster = ZclMultistateInputBasicCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterMultiStateInput extends ZigBeeInputBaseConverter<ZclMultistateInputBasicCluster> {

  public ZigBeeConverterMultiStateInput() {
    super(ZclClusterType.MULTISTATE_INPUT_BASIC, ZclMultistateInputBasicCluster.ATTR_PRESENTVALUE);
  }
}
