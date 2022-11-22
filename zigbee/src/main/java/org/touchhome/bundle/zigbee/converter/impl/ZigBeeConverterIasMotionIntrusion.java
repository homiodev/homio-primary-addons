package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Motion intrusion sensor Converter for the IAS motion sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_motionintrusion", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Motion")
public class ZigBeeConverterIasMotionIntrusion extends ZigBeeConverterIas {

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_ALARM1;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.MOTION_SENSOR);
  }
}
