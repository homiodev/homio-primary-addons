package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Converter for the IAS low battery indicator.
 */
@ZigBeeConverter(name = "zigbee:ias_lowbattery", linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "ias")
public class ZigBeeConverterIasLowBattery extends ZigBeeConverterIas {

  public ZigBeeConverterIasLowBattery() {
    super(null);
  }

  @Override
  public void initialize() {
    bitTest = CIE_BATTERY;
    super.initialize();
  }
}
