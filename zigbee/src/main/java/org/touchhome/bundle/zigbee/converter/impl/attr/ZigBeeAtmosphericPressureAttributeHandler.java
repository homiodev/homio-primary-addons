package org.touchhome.bundle.zigbee.converter.impl.attr;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster.ATTR_MEASUREDVALUE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster.ATTR_SCALEDVALUE;
import static tec.uom.se.unit.Units.PASCAL;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;

public class ZigBeeAtmosphericPressureAttributeHandler extends AttributeHandler {

  @Override
  public @Nullable State convertValue(Object value) {
    State enhancedScaleValue = getAttributeHandler(ATTR_SCALEDVALUE).map(ah -> ah.getValue()).orElse(null);
    Integer enhancedScale = null;
    if (enhancedScaleValue != null) {
      enhancedScale = enhancedScaleValue.intValue() * -1;
    }
    if (zclAttribute.getId() == ATTR_SCALEDVALUE && enhancedScale != null) {
      return new QuantityType<>(BigDecimal.valueOf((Integer) value, enhancedScale), PASCAL);
    }
    if (zclAttribute.getId() == ATTR_MEASUREDVALUE && enhancedScale == null) {
      return new QuantityType<>(BigDecimal.valueOf((Integer) value, 0), PASCAL);
    }
    return null;
  }
}
