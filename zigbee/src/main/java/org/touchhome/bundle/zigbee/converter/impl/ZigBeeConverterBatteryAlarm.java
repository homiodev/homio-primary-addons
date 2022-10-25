package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYALARMSTATE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.StringType;

/**
 * Converter for a battery alarm channel.
 * <p>
 * This converter relies on reports for the BatteryAlarmState attribute of the power configuration cluster, setting the state of the battery alarm channel depending on the bits set
 * in the BatteryAlarmState.
 * <p>
 * Possible future improvements:
 * <ul>
 * <li>The BatteryAlarmState provides battery level information for up to three batteries; this converter only considers
 * the information for the first battery.
 * <li>Devices might use alarms from the Alarms cluster instead of the BatteryAlarmState attribute to indicate battery
 * alarms. This is currently not supported by this converter.
 * <li>Devices might permit to configure the four battery level/voltage thresholds on which battery alarms are signaled;
 * such configuration is currently not supported.
 * </ul>
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:battery_alarm", clientClusters = {ZclPowerConfigurationCluster.CLUSTER_ID})
public class ZigBeeConverterBatteryAlarm extends ZigBeeInputBaseConverter {

  public static final String STATE_OPTION_BATTERY_THRESHOLD_1 = "threshold1";
  public static final String STATE_OPTION_BATTERY_THRESHOLD_2 = "threshold2";
  public static final String STATE_OPTION_BATTERY_THRESHOLD_3 = "threshold3";
  public static final String STATE_OPTION_BATTERY_NO_THRESHOLD = "noThreshold";
  private static final String STATE_OPTION_BATTERY_MIN_THRESHOLD = "minThreshold";
  private static final int ALARMSTATE_MIN_REPORTING_INTERVAL = (int) ofMinutes(10).getSeconds();
  private static final int ALARMSTATE_MAX_REPORTING_INTERVAL = (int) ofHours(2).getSeconds();

  private static final int MIN_THRESHOLD_BITMASK = 0b0001;
  private static final int THRESHOLD_1_BITMASK = 0b0010;
  private static final int THRESHOLD_2_BITMASK = 0b0100;
  private static final int THRESHOLD_3_BITMASK = 0b1000;

  private static final int BATTERY_ALARM_POLLING_PERIOD = (int) ofMinutes(30).getSeconds();

  public ZigBeeConverterBatteryAlarm() {
    super(POWER_CONFIGURATION, ATTR_BATTERYALARMSTATE, ALARMSTATE_MIN_REPORTING_INTERVAL,
        ALARMSTATE_MAX_REPORTING_INTERVAL, null);
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = BATTERY_ALARM_POLLING_PERIOD;
    log.debug("{}/{}: Could not bind to the power configuration cluster; polling battery alarm state every {} seconds",
        endpoint.getIeeeAddress(), endpoint.getEndpointId(), BATTERY_ALARM_POLLING_PERIOD);
    return false;
  }

  @Override
  protected boolean acceptEndpointExtra(ZclCluster cluster) {
    try {
      if (!cluster.discoverAttributes(false).get() && !cluster.isAttributeSupported(ATTR_BATTERYALARMSTATE)) {
        log.trace("{}/{}: Power configuration cluster battery alarm state not supported", endpoint.getIeeeAddress(), endpoint.getEndpointId());
        return false;
      }
    } catch (InterruptedException | ExecutionException e) {
      log.warn("{}/{}: Exception discovering attributes in power configuration cluster", endpoint.getIeeeAddress(), endpoint.getEndpointId(), e);
      return false;
    }
    return true;
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    log.debug("{}/{}: ZigBee attribute reports {}", endpoint.getIeeeAddress(), endpoint.getEndpointId(), attribute);

    // The value is a 32-bit bitmap, represented by an Integer
    Integer value = (Integer) val;

    if ((value & MIN_THRESHOLD_BITMASK) != 0) {
      updateChannelState(new StringType(STATE_OPTION_BATTERY_MIN_THRESHOLD));
    } else if ((value & THRESHOLD_1_BITMASK) != 0) {
      updateChannelState(new StringType(STATE_OPTION_BATTERY_THRESHOLD_1));
    } else if ((value & THRESHOLD_2_BITMASK) != 0) {
      updateChannelState(new StringType(STATE_OPTION_BATTERY_THRESHOLD_2));
    } else if ((value & THRESHOLD_3_BITMASK) != 0) {
      updateChannelState(new StringType(STATE_OPTION_BATTERY_THRESHOLD_3));
    } else {
      updateChannelState(new StringType(STATE_OPTION_BATTERY_NO_THRESHOLD));
    }
  }
}
