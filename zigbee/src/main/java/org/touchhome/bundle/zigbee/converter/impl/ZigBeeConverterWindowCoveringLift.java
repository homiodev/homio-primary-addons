package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.WINDOW_COVERING;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeCommand;
import com.zsmartsystems.zigbee.zcl.clusters.ZclWindowCoveringCluster;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;

// TODO: not working
@Log4j2
@ZigBeeConverter(name = "zigbee:sensor_occupancy", description = "Occupancy level",
    linkType = DeviceChannelLinkType.Boolean, clientClusters = {ZclWindowCoveringCluster.CLUSTER_ID})
public class ZigBeeConverterWindowCoveringLift extends ZigBeeInputBaseConverter {

  public ZigBeeConverterWindowCoveringLift() {
    super(WINDOW_COVERING, ZclWindowCoveringCluster.ATTR_CURRENTPOSITIONLIFTPERCENTAGE);
  }

  @Override
  public boolean initializeDevice() {
    ZclWindowCoveringCluster serverCluster = (ZclWindowCoveringCluster) endpoint
        .getInputCluster(ZclWindowCoveringCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("{}: Error opening device window covering controls", endpoint.getIeeeAddress());
      return false;
    }

      /*  ZclReportingConfig reporting = new ZclReportingConfig(channel);

        try {
            CommandResult bindResponse = bind(serverCluster).get();
            if (bindResponse.isSuccess()) {
                // Configure reporting
                ZclAttribute attribute = serverCluster
                        .getAttribute(ZclWindowCoveringCluster.ATTR_CURRENTPOSITIONLIFTPERCENTAGE);
                CommandResult reportingResponse = attribute.setReporting(reporting.getReportingTimeMin(),
                        reporting.getReportingTimeMax(), reporting.getReportingChange()).get();
                handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, reporting.getPollingPeriod());
            } else {
                log.debug("{}: Error 0x{} setting server binding", endpoint.getIeeeAddress(),
                        Integer.toHexString(bindResponse.getStatusCode()));
                pollingPeriod = POLLING_PERIOD_HIGH;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("{}: Exception setting reporting ", endpoint.getIeeeAddress(), e);
        }*/

    return true;
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = POLLING_PERIOD_HIGH;
    return true;
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
            log.debug("{}: Command was not converted - {}", endpoint.getIeeeAddress(), command);
            return;
        }

        clusterServer.sendCommand(zclCommand);*/
    return null;
  }

    /*@Override
    public Channel getChannel(ThingUID thingUID, ZigBeeEndpoint endpoint) {
        ZclWindowCoveringCluster serverCluster = (ZclWindowCoveringCluster) endpoint
                .getInputCluster(ZclWindowCoveringCluster.CLUSTER_ID);
        if (serverCluster == null) {
            log.trace("{}/{}: Window covering cluster not found", endpoint.getIeeeAddress());
            return null;
        }

        try {
            if (serverCluster.discoverCommandsReceived(false).get()) {
                if (!(serverCluster.getSupportedCommandsReceived().contains(WindowCoveringDownClose.COMMAND_ID)
                        && serverCluster.getSupportedCommandsReceived().contains(WindowCoveringUpOpen.COMMAND_ID))) {
                    log.trace("{}/{}: Window covering cluster up/down commands not supported",
                            endpoint.getIeeeAddress());
                    return null;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("{}/{}: Exception discovering received commands in window covering cluster",
                    endpoint.getIeeeAddress(), e);
        }

        return ChannelBuilder
                .create(createChannelUID(thingUID, endpoint, ZigBeeBindingConstants.CHANNEL_NAME_WINDOWCOVERING_LIFT),
                        ZigBeeBindingConstants.ITEM_TYPE_ROLLERSHUTTER)
                .withType(ZigBeeBindingConstants.FCHANNEL_WINDOWCOVERING_LIFT)
                .withLabel(ZigBeeBindingConstants.CHANNEL_LABEL_WINDOWCOVERING_LIFT)
                .withAutoUpdatePolicy(AutoUpdatePolicy.VETO).withProperties(createProperties(endpoint)).build();
    }*/
}
