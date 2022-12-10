package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * IAS System Alarm Channel Converter for the IAS Standard CIE System sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_standard_system", linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterIasCieSystem extends ZigBeeConverterIas {

  public ZigBeeConverterIasCieSystem() {
    super(ZoneTypeEnum.STANDARD_CIE);
  }

  @Override
  public void initialize() {
    bitTest = CIE_ALARM1;
    super.initialize();
  }
}
