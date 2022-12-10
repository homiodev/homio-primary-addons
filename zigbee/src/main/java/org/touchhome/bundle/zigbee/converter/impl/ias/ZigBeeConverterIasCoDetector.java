package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Converter for the IAS CO sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_cosensor", linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "ias")
public class ZigBeeConverterIasCoDetector extends ZigBeeConverterIas {

  public ZigBeeConverterIasCoDetector() {
    super(ZoneTypeEnum.CO_SENSOR, CIE_ALARM1);
  }
}