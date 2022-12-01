package org.touchhome.bundle.zigbee.converter.impl.attr;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.util.ClusterAttributeConfiguration;
import tec.uom.se.unit.Units;

public class ZigBeeTemperatureAttributeHandler extends AttributeHandler {

  @Override
  public @Nullable State convertValue(Object value) {
    return new QuantityType<>(BigDecimal.valueOf((Integer) value, 2), Units.CELSIUS);
  }
}
