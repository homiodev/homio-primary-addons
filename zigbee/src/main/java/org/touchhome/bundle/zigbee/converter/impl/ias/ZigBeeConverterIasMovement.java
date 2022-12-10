package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Movement Sensor Alarm Converter for the IAS movement sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_movement", linkType = VariableType.Boolean,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Sensor")
public class ZigBeeConverterIasMovement extends ZigBeeConverterIas {

  public ZigBeeConverterIasMovement() {
    super(ZoneTypeEnum.VIBRATION_MOVEMENT_SENSOR);
  }

  @Override
  public void initialize() {
    bitTest = CIE_ALARM1;
    super.initialize();
  }
}
