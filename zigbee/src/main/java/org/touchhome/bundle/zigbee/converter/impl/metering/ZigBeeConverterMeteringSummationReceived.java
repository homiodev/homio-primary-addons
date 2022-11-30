package org.touchhome.bundle.zigbee.converter.impl.metering;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

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
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, true, true, entityContext);
  }
}
