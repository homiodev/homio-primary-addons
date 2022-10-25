package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.RELATIVE_HUMIDITY_MEASUREMENT;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclRelativeHumidityMeasurementCluster;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;

/**
 * Converter for the relative humidity channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:measurement_relativehumidity", description = "Relative humidity", clientClusters = {
    ZclRelativeHumidityMeasurementCluster.CLUSTER_ID}, linkType = DeviceChannelLinkType.Float)
public class ZigBeeConverterRelativeHumidity extends ZigBeeInputBaseConverter {

  public ZigBeeConverterRelativeHumidity() {
    super(RELATIVE_HUMIDITY_MEASUREMENT, ZclRelativeHumidityMeasurementCluster.ATTR_MEASUREDVALUE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, 10);
    pollingPeriod = REPORTING_PERIOD_DEFAULT_MAX;
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(BigDecimal.valueOf(value, 2)));
  }
}
