package org.touchhome.bundle.zigbee.converter.impl.ias;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;


/**
 * Fire Indication Alarm Converter for the IAS fire indicator.
 */
@ZigBeeConverter(name = ZigBeeConverterIasFireIndicator.CLUSTER_NAME, linkType = VariableType.Float,
                 clientCluster = ZclIasZoneCluster.CLUSTER_ID, category = "SmokeDetector")
public class ZigBeeConverterIasFireIndicator extends ZigBeeConverterIas {

  public static final String CLUSTER_NAME = "zigbee:ias_fire";

  public ZigBeeConverterIasFireIndicator() {
    super(ZoneTypeEnum.FIRE_SENSOR);
  }

  @Override
  public void initialize() {
    bitTest = CIE_ALARM1;
    super.initialize();
  }
}
