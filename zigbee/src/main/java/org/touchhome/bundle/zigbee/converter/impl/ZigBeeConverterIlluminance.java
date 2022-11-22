package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ILLUMINANCE_MEASUREMENT;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIlluminanceMeasurementCluster;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;

/**
 * Indicates the current illuminance in lux Converter for the illuminance channel
 */
@ZigBeeConverter(name = "zigbee:measurement_illuminance", linkType = VariableType.Float,
    clientCluster = ZclIlluminanceMeasurementCluster.CLUSTER_ID, category = "Illuminance")
public class ZigBeeConverterIlluminance extends ZigBeeInputBaseConverter {

  public ZigBeeConverterIlluminance() {
    super(ILLUMINANCE_MEASUREMENT, ZclIlluminanceMeasurementCluster.ATTR_MEASUREDVALUE);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return super.acceptEndpoint(endpoint, entityID, false, false);
  }

  @Override
  protected void afterInitializeConverter() {
    configReporting = new ZclReportingConfig(getEntity(), 5000, 10, 20000);
  }
}
