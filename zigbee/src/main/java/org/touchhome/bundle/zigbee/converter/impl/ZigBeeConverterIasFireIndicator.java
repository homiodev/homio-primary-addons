package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;


/**
 * Fire Indication Alarm Converter for the IAS fire indicator.
 */
@ZigBeeConverter(name = ZigBeeConverterIasFireIndicator.CLUSTER_NAME, linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "SmokeDetector")
public class ZigBeeConverterIasFireIndicator extends ZigBeeConverterIas {

  public static final String CLUSTER_NAME = "zigbee:ias_fire";

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_ALARM1;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.FIRE_SENSOR);
  }
}
