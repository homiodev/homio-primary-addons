package org.touchhome.bundle.zigbee.converter.impl.attr;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;

public class ZigBeeGeneralAttributeHandler extends AttributeHandler {

  private Integer precision;

  @Override
  public void initialize(boolean bindSuccess) throws Exception {
    this.precision = attributeConfiguration.getPrecision();
    super.initialize(bindSuccess);
  }

  @Override
  @Nullable
  public State convertValue(Object value) {
    if (precision != null) {
      return new DecimalType(BigDecimal.valueOf((Integer) value, 2));
    }
    return OnOffType.of(value);
  }
}
