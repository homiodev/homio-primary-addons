package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Converter for the IAS CO sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_cosensor", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "ias")
public class ZigBeeConverterIasCoDetector extends ZigBeeConverterIas {

  @Override
  public void initializeConverter() {
    bitTest = CIE_ALARM1;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.CO_SENSOR);
  }
}
