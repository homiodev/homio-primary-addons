package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;


/**
 * Converter for the IAS water sensor.
 */
@ZigBeeConverter(name = ZigBeeConverterIasWaterSensor.CLUSTER_NAME, clientCluster =
    ZclIasZoneCluster.CLUSTER_ID, linkType = VariableType.Boolean, category = "Sensor")
public class ZigBeeConverterIasWaterSensor extends ZigBeeConverterIas {

  public static final String CLUSTER_NAME = "zigbee:ias_water";

  public ZigBeeConverterIasWaterSensor() {
    super(ZoneTypeEnum.WATER_SENSOR, CIE_ALARM1);
  }
}
