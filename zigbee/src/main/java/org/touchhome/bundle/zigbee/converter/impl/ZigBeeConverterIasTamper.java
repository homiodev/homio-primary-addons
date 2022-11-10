package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;

/**
 * Indicates if a device is tampered with Converter for the IAS tamper.
 */
@ZigBeeConverter(name = "zigbee:ias_tamper", clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Alarm")
public class ZigBeeConverterIasTamper extends ZigBeeConverterIas {

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_TAMPER;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
    return hasIasZoneInputCluster(endpoint);
  }

}
