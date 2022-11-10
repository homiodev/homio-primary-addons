package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclFanControlCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;

/**
 * Set the fan mode This channel supports fan control
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:fancontrol", clientCluster = ZclFanControlCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterFanControl extends ZigBeeInputBaseConverter {

  private static final int MODE_OFF = 0;
  private static final int MODE_LOW = 1;
  private static final int MODE_MEDIUM = 2;
  private static final int MODE_HIGH = 3;
  private static final int MODE_ON = 4;
  private static final int MODE_AUTO = 5;

  public ZigBeeConverterFanControl() {
    super(ZclClusterType.FAN_CONTROL, ZclFanControlCluster.ATTR_FANMODE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, null);
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = POLLING_PERIOD_HIGH;
    return true;
  }

  @Override
  public boolean initializeDevice() {
    ZclFanControlCluster serverCluster = getInputCluster(ZclFanControlCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("{}: Error opening device fan controls", getEndpointEntity());
      return false;
    }

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting
        ZclAttribute attribute = serverCluster.getAttribute(ZclFanControlCluster.ATTR_FANMODE);
        CommandResult reportingResponse = attribute.setReporting(1, REPORTING_PERIOD_DEFAULT_MAX).get();
        handleReportingResponseHigh(reportingResponse);
      } else {
        pollingPeriod = POLLING_PERIOD_HIGH;
      }
    } catch (InterruptedException | ExecutionException e) {
      log.error("{}: Exception setting reporting {}", getEndpointEntity(), e);
      return false;
    }
    return true;
  }

  @Override
  protected void handleReportingResponseDuringInitializeDevice(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        int value;
        if (command instanceof OnOffType) {
            value = command == OnOffType.ON ? MODE_ON : MODE_OFF;
        } else if (command instanceof DecimalType) {
            value = ((DecimalType) command).intValue();
        } else {
            log.debug("{}: Unabled to convert fan mode {}", getEndpointEntity(),endpoint.getEndpointId(), command);
            return;
        }

        fanModeAttribute.writeValue(value);
    }*/

  //ZclAttribute fanSequenceAttribute = cluster.getAttribute(ZclFanControlCluster.ATTR_FANMODESEQUENCE);
  // Integer sequence = (Integer) fanSequenceAttribute.readValue(Long.MAX_VALUE);
        /*if (sequence != null) {
            List<StateOption> options = new ArrayList<>();
            switch (sequence) {
                case 0:
                    options.add(new StateOption("1", "Low"));
                    options.add(new StateOption("2", "Medium"));
                    options.add(new StateOption("3", "High"));
                case 1:
                    options.add(new StateOption("1", "Low"));
                    options.add(new StateOption("3", "High"));
                    break;
                case 2:
                    options.add(new StateOption("1", "Low"));
                    options.add(new StateOption("2", "Medium"));
                    options.add(new StateOption("3", "High"));
                    options.add(new StateOption("5", "Auto"));
                    break;
                case 3:
                    options.add(new StateOption("1", "Low"));
                    options.add(new StateOption("3", "High"));
                    options.add(new StateOption("5", "Auto"));
                    break;
                case 4:
                    options.add(new StateOption("4", "On"));
                    options.add(new StateOption("5", "Auto"));
                    break;
                default:
                    log.error("{}: Unknown fan mode sequence {}", getEndpointEntity(),endpoint.getEndpointId(), sequence);
                    break;
            }

            stateDescription = new StateDescription(BigDecimal.ZERO, BigDecimal.valueOf(9), BigDecimal.valueOf(1), "",
                    false, options);
        }*/
}
