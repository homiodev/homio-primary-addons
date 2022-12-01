package org.touchhome.bundle.zigbee.converter.impl.attr;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster.ATTR_DIVISOR;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster.ATTR_MULTIPLIER;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import java.math.BigDecimal;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;

public class ZigBeeMeteringAttributeHandler extends AttributeHandler {

  private double divisor = 1.0;
  private double multiplier = 1.0;

  @Override
  public void initialize(boolean context) throws Exception {
    super.initialize(context);
    this.divisor = readAttribute(zclCluster, ATTR_DIVISOR, 1);
    this.multiplier = readAttribute(zclCluster, ATTR_MULTIPLIER, 1);
  }

  @Override
  public State convertValue(Object val) {
    BigDecimal valueCalibrated = BigDecimal.valueOf((Integer) val * multiplier / divisor);
    return new DecimalType(valueCalibrated);
  }
}
