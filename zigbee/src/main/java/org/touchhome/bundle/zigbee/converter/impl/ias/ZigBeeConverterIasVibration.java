package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Vibration Sensor Alarm Converter for the IAS vibration sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_vibration", linkType = VariableType.Boolean,
    clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Sensor")
public class ZigBeeConverterIasVibration extends ZigBeeConverterIas {

  @Override
  public void initializeConverter() {
    bitTest = CIE_ALARM2;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.VIBRATION_MOVEMENT_SENSOR);
  }
}
