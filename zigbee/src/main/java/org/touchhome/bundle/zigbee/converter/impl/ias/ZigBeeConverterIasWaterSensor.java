package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;


/**
 * Converter for the IAS water sensor.
 */
@ZigBeeConverter(name = ZigBeeConverterIasWaterSensor.CLUSTER_NAME, clientCluster =
    ZclIasZoneCluster.CLUSTER_ID, linkType = VariableType.Boolean, category = "Sensor")
public class ZigBeeConverterIasWaterSensor extends ZigBeeConverterIas {

  public static final String CLUSTER_NAME = "zigbee:ias_water";

  @Override
  public void initializeConverter() {
    bitTest = CIE_ALARM1;
    super.initializeConverter();
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return supportsIasChannel(endpoint, entityID, ZoneTypeEnum.WATER_SENSOR);
  }
}
