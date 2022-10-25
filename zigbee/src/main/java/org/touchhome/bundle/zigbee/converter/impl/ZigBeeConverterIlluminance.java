package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ILLUMINANCE_MEASUREMENT;

import com.zsmartsystems.zigbee.zcl.clusters.ZclIlluminanceMeasurementCluster;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;

/**
 * Converter for the illuminance channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:measurement_illuminance", description = "Illuminance level", linkType = DeviceChannelLinkType.Float,
    clientClusters = {ZclIlluminanceMeasurementCluster.CLUSTER_ID})
public class ZigBeeConverterIlluminance extends ZigBeeInputBaseConverter {

  private static BigDecimal CHANGE_DEFAULT = new BigDecimal(5000);
  private static BigDecimal CHANGE_MIN = new BigDecimal(10);
  private static BigDecimal CHANGE_MAX = new BigDecimal(20000);

  public ZigBeeConverterIlluminance() {
    super(ILLUMINANCE_MEASUREMENT, ZclIlluminanceMeasurementCluster.ATTR_MEASUREDVALUE, POLLING_PERIOD_DEFAULT);
  }

  @Override
  public void updateConfiguration() {
    updateServerPoolingPeriod();
  }

   /* @Override
    protected void afterInitializeConverter() {
        configReporting.setAnalogue(CHANGE_DEFAULT, CHANGE_MIN, CHANGE_MAX);
    }*/
}
