package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Indicates if a device is tampered with Converter for the IAS tamper.
 */
@ZigBeeConverter(name = "zigbee:ias_tamper", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Alarm")
public class ZigBeeConverterIasTamper extends ZigBeeConverterIas {

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_TAMPER;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return hasIasZoneInputCluster(endpoint, entityID);
  }
}
