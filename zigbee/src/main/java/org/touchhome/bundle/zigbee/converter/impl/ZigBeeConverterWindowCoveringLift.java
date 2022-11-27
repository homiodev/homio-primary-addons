package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.WINDOW_COVERING;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeCommand;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclWindowCoveringCluster;
import com.zsmartsystems.zigbee.zcl.clusters.windowcovering.WindowCoveringDownClose;
import com.zsmartsystems.zigbee.zcl.clusters.windowcovering.WindowCoveringUpOpen;
import java.util.Objects;
import java.util.concurrent.Future;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

/**
 * Sets the window covering level - supporting open/close and up/down type commands Window Covering Lift Sets the window covering level - supporting open/close and up/down type
 * commands
 */
@ZigBeeConverter(name = "zigbee:windowcovering_lift", linkType = VariableType.Boolean,
    category = "Blinds", clientCluster = ZclWindowCoveringCluster.CLUSTER_ID)
public class ZigBeeConverterWindowCoveringLift extends ZigBeeInputBaseConverter {

  public ZigBeeConverterWindowCoveringLift() {
    super(WINDOW_COVERING, ZclWindowCoveringCluster.ATTR_CURRENTPOSITIONLIFTPERCENTAGE, POLLING_PERIOD_HIGH);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, Objects.requireNonNull(configReporting).getPollingPeriod());
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    ZclWindowCoveringCluster serverCluster = (ZclWindowCoveringCluster) endpoint
        .getInputCluster(ZclWindowCoveringCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.trace("[{}]: Window covering cluster not found {}", entityID, endpoint);
      return false;
    }

    try {
      if (serverCluster.discoverCommandsReceived(false).get()) {
        if (!(serverCluster.getSupportedCommandsReceived().contains(WindowCoveringDownClose.COMMAND_ID)
            && serverCluster.getSupportedCommandsReceived().contains(WindowCoveringUpOpen.COMMAND_ID))) {
          log.debug("[{}]: Window covering cluster up/down commands not supported {}",
              entityID, endpoint.getIeeeAddress());
          return false;
        }
      }
    } catch (Exception e) {
      log.warn("[{}]: Exception discovering received commands in window covering cluster {}", entityID, endpoint, e);
      return false;
    }
    return true;
  }

  @Override
  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    super.initialize(endpointService, endpoint);
    configReporting = new ZclReportingConfig(getEntity());
  }

  @Override
  public void initializeDevice() {
    ZclWindowCoveringCluster serverCluster = getInputCluster(ZclWindowCoveringCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("[{}]: Error opening device window covering controls {}", entityID, endpoint);
      throw new RuntimeException("Error opening device window covering controls");
    }

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting
        ZclAttribute attribute = serverCluster.getAttribute(ZclWindowCoveringCluster.ATTR_CURRENTPOSITIONLIFTPERCENTAGE);
        ZigBeeEndpointEntity endpointEntity = getEndpointService().getEntity();
        CommandResult reportingResponse = attribute.setReporting(
            endpointEntity.getReportingTimeMin(),
            endpointEntity.getReportingTimeMax(),
            endpointEntity.getReportingChange()).get();
        handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, endpointEntity.getPollingPeriod());
      } else {
        log.debug("[{}]: Error 0x{} setting server binding", entityID,
            Integer.toHexString(bindResponse.getStatusCode()));
        pollingPeriod = POLLING_PERIOD_HIGH;
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting ", entityID, e);
    }
  }

  @Override
  public Future<CommandResult> handleCommand(ZigBeeCommand command) {
    // ZclWindowCoveringCommand zclCommand = null;
    // UpDown MoveStop Percent Refresh
        /*if (command instanceof UpDownType) {
            switch ((UpDownType) command) {
                case UP:
                    zclCommand = new WindowCoveringUpOpen();
                    break;
                case DOWN:
                    zclCommand = new WindowCoveringDownClose();
                    break;
                default:
                    break;
            }
        } else if (command instanceof StopMoveType) {
            switch ((StopMoveType) command) {
                case STOP:
                    zclCommand = new WindowCoveringStop();
                    break;
                default:
                    break;
            }
        } else if (command instanceof PercentType) {
            zclCommand = new WindowCoveringGoToLiftPercentage(((PercentType) command).intValue());
        }

        if (command == null) {
            log.debug("[{}]: Command was not converted - {}", getEndpointEntity(), command);
            return;
        }

        clusterServer.sendCommand(zclCommand);*/
    return null;
  }
}
