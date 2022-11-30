package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Converter for the IAS low battery indicator.
 */
@ZigBeeConverter(name = "zigbee:ias_lowbattery", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "ias")
public class ZigBeeConverterIasLowBattery extends ZigBeeConverterIas {

  @Override
  public void initializeConverter() {
    bitTest = CIE_BATTERY;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return hasIasZoneInputCluster(endpoint, entityID);
  }
}
