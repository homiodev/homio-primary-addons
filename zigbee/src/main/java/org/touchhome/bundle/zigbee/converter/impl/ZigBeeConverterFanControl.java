package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.FAN_CONTROL;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclFanControlCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.config.ZclFanControlConfig;

/**
 * Set the fan mode This channel supports fan control
 */
@ZigBeeConverter(name = "zigbee:fancontrol", linkType = VariableType.Float, clientCluster = ZclFanControlCluster.CLUSTER_ID, category = "HVAC")
public class ZigBeeConverterFanControl extends ZigBeeInputBaseConverter {

  private static final int MODE_OFF = 0;
  private static final int MODE_LOW = 1;
  private static final int MODE_MEDIUM = 2;
  private static final int MODE_HIGH = 3;
  private static final int MODE_ON = 4;
  private static final int MODE_AUTO = 5;

  public ZigBeeConverterFanControl() {
    super(FAN_CONTROL, ZclFanControlCluster.ATTR_FANMODE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, null, POLLING_PERIOD_HIGH);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void initializeReportConfigurations() {
    configFanControl = new ZclFanControlConfig(getEntity(), zclCluster, log);
  }

  @Override
  public void updateConfiguration() {
    if (configFanControl != null) {
      configFanControl.updateConfiguration(getEntity());
    }
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
