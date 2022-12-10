package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * Vibration Sensor Alarm Converter for the IAS vibration sensor.
 */
@ZigBeeConverter(name = "zigbee:ias_vibration", linkType = VariableType.Boolean,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "Sensor")
public class ZigBeeConverterIasVibration extends ZigBeeConverterIas {

  public ZigBeeConverterIasVibration() {
    super(ZoneTypeEnum.VIBRATION_MOVEMENT_SENSOR);
  }

  @Override
  public void initialize() {
    bitTest = CIE_ALARM2;
    super.initialize();
  }
}
