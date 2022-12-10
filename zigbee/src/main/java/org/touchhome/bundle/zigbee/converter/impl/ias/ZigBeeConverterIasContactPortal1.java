package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Contact sensor
 */
@ZigBeeConverter(name = "zigbee:ias_contactportal1", linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Door")
public class ZigBeeConverterIasContactPortal1 extends ZigBeeConverterIas {

  public ZigBeeConverterIasContactPortal1() {
    super(ZoneTypeEnum.CONTACT_SWITCH, CIE_ALARM1);
  }
}
