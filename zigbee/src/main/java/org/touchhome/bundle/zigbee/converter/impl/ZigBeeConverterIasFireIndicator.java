package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;


/**
 * Fire Indication Alarm Converter for the IAS fire indicator.
 */
@ZigBeeConverter(name = ZigBeeConverterIasFireIndicator.CLUSTER_NAME, clientCluster =
    ZclIasZoneCluster.CLUSTER_ID, linkType = VariableType.Boolean, category = "SmokeDetector")
public class ZigBeeConverterIasFireIndicator extends ZigBeeConverterIas {

  public static final String CLUSTER_NAME = "zigbee:ias_fire";

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_ALARM1;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
    return supportsIasChannel(endpoint, ZoneTypeEnum.FIRE_SENSOR);
  }
}
