package org.touchhome.bundle.zigbee.converter.impl.attr;

import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import tec.uom.se.unit.Units;

public class ZigBeePowerAttributeHandler extends AttributeHandler {

  public static final String STATE_OPTION_BATTERY_THRESHOLD_1 = "threshold1";
  public static final String STATE_OPTION_BATTERY_THRESHOLD_2 = "threshold2";
  public static final String STATE_OPTION_BATTERY_THRESHOLD_3 = "threshold3";
  public static final String STATE_OPTION_BATTERY_NO_THRESHOLD = "noThreshold";
  private static final String STATE_OPTION_BATTERY_MIN_THRESHOLD = "minThreshold";

  private static final int MIN_THRESHOLD_BITMASK = 0b0001;
  private static final int THRESHOLD_1_BITMASK = 0b0010;
  private static final int THRESHOLD_2_BITMASK = 0b0100;
  private static final int THRESHOLD_3_BITMASK = 0b1000;

  @Override
  public State convertValue(Object val) {
    switch (zclAttribute.getId()) {
      case ZclPowerConfigurationCluster.ATTR_BATTERYALARMSTATE:
        return convertPowerAlarm(val);
      case ZclPowerConfigurationCluster.ATTR_BATTERYVOLTAGE:
        return convertVoltage(val);
      case ZclPowerConfigurationCluster.ATTR_BATTERYPERCENTAGEREMAINING:
        return new DecimalType((Integer) val / 2);
    }
    return null;
  }

  private State convertVoltage(Object val) {
    Integer value = (Integer) val;
    if (value == 0xFF) {
      // The value 0xFF indicates an invalid or unknown reading.
      return null;
    }
    BigDecimal valueInVolt = BigDecimal.valueOf(value, 1);
    return new QuantityType<>(valueInVolt, Units.VOLT);
  }

  private State convertPowerAlarm(Object val) {
    // The value is a 32-bit bitmap, represented by an Integer
    Integer value = (Integer) val;

    if ((value & MIN_THRESHOLD_BITMASK) != 0) {
      return new StringType(STATE_OPTION_BATTERY_MIN_THRESHOLD);
    } else if ((value & THRESHOLD_1_BITMASK) != 0) {
      return new StringType(STATE_OPTION_BATTERY_THRESHOLD_1);
    } else if ((value & THRESHOLD_2_BITMASK) != 0) {
      return new StringType(STATE_OPTION_BATTERY_THRESHOLD_2);
    } else if ((value & THRESHOLD_3_BITMASK) != 0) {
      return new StringType(STATE_OPTION_BATTERY_THRESHOLD_3);
    } else {
      return new StringType(STATE_OPTION_BATTERY_NO_THRESHOLD);
    }
  }
}
