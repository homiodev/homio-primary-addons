package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Motion presence sensor Converter for the IAS presence sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_motionpresence", linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Motion")
public class ZigBeeConverterIasMotionPresence extends ZigBeeConverterIas {

  public ZigBeeConverterIasMotionPresence() {
    super(ZoneTypeEnum.MOTION_SENSOR, CIE_ALARM2);
  }
}
