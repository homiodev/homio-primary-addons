package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclFanControlCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclFanControlConfig;

/**
 * Set the fan mode This channel supports fan control
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:fancontrol", linkType = VariableType.Float, clientCluster = ZclFanControlCluster.CLUSTER_ID, category = "HVAC")
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
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return super.acceptEndpoint(endpoint, entityID, false, false);
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
      log.error("[{}]: Error opening device fan controls {}", entityID, endpoint);
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
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting {}", entityID, e, e);
      return false;
    }
    return true;
  }

  @Override
  protected void handleReportingResponseDuringInitializeDevice(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void afterInitializeConverter() {
    configFanControl = new ZclFanControlConfig(getEntity(), getZclCluster());
  }

 /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        int value;
        if (command instanceof OnOffType) {
            value = command == OnOffType.ON ? MODE_ON : MODE_OFF;
        } else if (command instanceof DecimalType) {
            value = ((DecimalType) command).intValue();
        } else {
            log.debug("[{}]: Unabled to convert fan mode {}", getEndpointEntity(),endpoint.getEndpointId(), command);
            return;
        }

        fanModeAttribute.writeValue(value);
    }*/
}
