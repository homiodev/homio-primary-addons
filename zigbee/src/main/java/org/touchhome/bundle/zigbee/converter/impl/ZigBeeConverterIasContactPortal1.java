package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Contact sensor
 */
@ZigBeeConverter(name = "zigbee:ias_contactportal1", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Door")
public class ZigBeeConverterIasContactPortal1 extends ZigBeeConverterIas {

  @Override
  public void initializeConverter() {
    bitTest = CIE_ALARM1;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.CONTACT_SWITCH);
  }
}
