package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Indicates if a device is tampered with Converter for the IAS tamper.
 */
@ZigBeeConverter(name = "zigbee:ias_tamper", linkType = VariableType.Float,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Alarm")
public class ZigBeeConverterIasTamper extends ZigBeeConverterIas {

  @Override
  public void initializeConverter() {
    bitTest = CIE_TAMPER;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return hasIasZoneInputCluster(endpoint, entityID);
  }
}
