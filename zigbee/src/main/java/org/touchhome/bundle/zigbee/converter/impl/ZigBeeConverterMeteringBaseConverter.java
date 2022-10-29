package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.METERING;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineDivisor;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineMultiplier;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;

/**
 * ZigBee channel converter for instantaneous demand measurement
 */
@Log4j2
public class ZigBeeConverterMeteringBaseConverter extends ZigBeeInputBaseConverter {

  private double divisor = 1.0;
  private double multiplier = 1.0;

  public ZigBeeConverterMeteringBaseConverter(int attributeId) {
    super(METERING, attributeId, 3, REPORTING_PERIOD_DEFAULT_MAX, 1);
  }

  @Override
  protected void afterInitializeConverter() {
    this.divisor = determineDivisor(getZclCluster());
    this.multiplier = determineMultiplier(getZclCluster());
  }

  @Override
  protected boolean acceptEndpointExtra(ZclCluster cluster) {
    try {
      if (!cluster.discoverAttributes(false).get()
          && !cluster.isAttributeSupported(getAttributeId())) {
        return false;
      } else {
        ZclAttribute attribute = cluster.getAttribute(getAttributeId());
        if (attribute.readValue(Long.MAX_VALUE) == null) {
          return false;
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      log.warn("{}/{}: Exception discovering attributes in metering cluster", endpoint.getIeeeAddress(), endpoint.getEndpointId(), e);
      return false;
    }
    return true;
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    BigDecimal valueCalibrated = BigDecimal.valueOf((Integer) val * multiplier / divisor);
    updateChannelState(new DecimalType(valueCalibrated));
  }
}
