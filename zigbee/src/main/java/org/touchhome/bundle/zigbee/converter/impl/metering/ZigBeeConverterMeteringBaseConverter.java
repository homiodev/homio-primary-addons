package org.touchhome.bundle.zigbee.converter.impl.metering;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.METERING;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeInputBaseConverter;

/**
 * ZigBee channel converter for instantaneous demand measurement
 */
public abstract class ZigBeeConverterMeteringBaseConverter extends ZigBeeInputBaseConverter {

  private double divisor = 1.0;
  private double multiplier = 1.0;

  public ZigBeeConverterMeteringBaseConverter(int attributeId) {
    super(METERING, attributeId, 3, REPORTING_PERIOD_DEFAULT_MAX, 1, POLLING_PERIOD_HIGH);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  public void initializeConverter() {
    super.initializeConverter();
    this.divisor = readAttribute(zclCluster, ZclMeteringCluster.ATTR_DIVISOR, 1);
    this.multiplier = readAttribute(zclCluster, ZclMeteringCluster.ATTR_DIVISOR, 1);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    BigDecimal valueCalibrated = BigDecimal.valueOf((Integer) val * multiplier / divisor);
    updateChannelState(new DecimalType(valueCalibrated));
  }
}
