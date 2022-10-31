package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;

/**
 * Vibration Sensor Alarm Converter for the IAS vibration sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_vibration", clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Sensor")
public class ZigBeeConverterIasVibration extends ZigBeeConverterIas {

  @Override
  public boolean initializeConverter() {
    bitTest = CIE_ALARM2;
    return super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
    return supportsIasChannel(endpoint, ZoneTypeEnum.VIBRATION_MOVEMENT_SENSOR);
  }
}
