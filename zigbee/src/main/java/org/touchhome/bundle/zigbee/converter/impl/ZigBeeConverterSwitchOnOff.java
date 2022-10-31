package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeCommand;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCommand;
import com.zsmartsystems.zigbee.zcl.ZclCommandListener;
import com.zsmartsystems.zigbee.zcl.ZclStatus;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OffCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OffWithEffectCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OnCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OnWithTimedOffCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.ToggleCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.ZclOnOffCommand;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclOnOffSwitchConfig;

/**
 * This channel supports changes through attribute updates, and also through received commands. This allows a switch that is not connected to a load to send commands, or a switch
 * that is connected to a load to send status (or both!).
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:switch_onoff", linkType = DeviceChannelLinkType.Boolean,
    serverClusters = {ZclOnOffCluster.CLUSTER_ID}, clientCluster = ZclOnOffCluster.CLUSTER_ID, category = "Light")
public class ZigBeeConverterSwitchOnOff extends ZigBeeBaseChannelConverter
    implements ZclAttributeListener, ZclCommandListener {

  private final AtomicBoolean currentOnOffState = new AtomicBoolean(true);
  private ZclOnOffCluster clusterOnOffClient;
  private ZclOnOffCluster clusterOnOffServer;
  private ZclAttribute attributeServer;
  private ZclOnOffSwitchConfig configOnOff;
  private ScheduledExecutorService updateScheduler;
  private ScheduledFuture<?> updateTimer = null;

  @Override
  public boolean initializeDevice() {
    pollingPeriod = REPORTING_PERIOD_DEFAULT_MAX;
    ZclOnOffCluster clientCluster = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);
    ZclOnOffCluster serverCluster = getInputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (clientCluster == null && serverCluster == null) {
      log.error("{}: Error opening device on/off controls", getEndpointEntity());
      return false;
    }

    if (serverCluster != null) {
      try {
        CommandResult bindResponse = bind(serverCluster).get();
        if (bindResponse.isSuccess()) {
          updateServerPoolingPeriod(serverCluster, ZclOnOffCluster.ATTR_ONOFF, false);
        } else {
          log.debug("{}: Error 0x{} setting server binding", getEndpointEntity(), Integer.toHexString(bindResponse.getStatusCode()));
          pollingPeriod = POLLING_PERIOD_HIGH;
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("{}: Exception setting reporting {}", getEndpointEntity(), e);
      }
    }

    if (clientCluster != null) {
      try {
        CommandResult bindResponse = bind(clientCluster).get();
        if (!bindResponse.isSuccess()) {
          log.error("{}: Error 0x{} setting client binding", getEndpointEntity(), Integer.toHexString(bindResponse.getStatusCode()));
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("{}: Exception setting binding {}", getEndpointEntity(), e);
      }
    }

    return true;
  }

  @Override
  public boolean initializeConverter() {
    updateScheduler = Executors.newSingleThreadScheduledExecutor();

    clusterOnOffClient = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);
    clusterOnOffServer = getInputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (clusterOnOffClient == null && clusterOnOffServer == null) {
      log.error("{}: Error opening device on/off controls", getEndpointEntity());
      return false;
    }

    if (clusterOnOffServer != null) {
      // Add the listener
      clusterOnOffServer.addAttributeListener(this);
      configOnOff = new ZclOnOffSwitchConfig();
      configOnOff.initialize(clusterOnOffServer);
    }

    if (clusterOnOffClient != null) {
      // Add the command listener
      clusterOnOffClient.addCommandListener(this);
    }

    if (clusterOnOffServer != null) {
      // Add the listener
      clusterOnOffServer.addAttributeListener(this);
      attributeServer = clusterOnOffServer.getAttribute(ZclOnOffCluster.ATTR_ONOFF);
    }

    return true;
  }

  @Override
  public void disposeConverter() {
    log.debug("{}: Closing device on/off cluster", getEndpointEntity());

    if (clusterOnOffClient != null) {
      clusterOnOffClient.removeCommandListener(this);
    }
    if (clusterOnOffServer != null) {
      clusterOnOffServer.removeAttributeListener(this);
    }

    stopOffTimer();
    updateScheduler.shutdownNow();
  }

  @Override
  public int getPollingPeriod() {
    if (clusterOnOffServer != null) {
      return getEndpointEntity().getPoolingPeriod();
    }
    return Integer.MAX_VALUE;
  }

  @Override
  protected void handleRefresh() {
    if (attributeServer != null) {
      attributeServer.readValue(0);
    }
  }

  @Override
  public Future<CommandResult> handleCommand(final ZigBeeCommand command) {
    if (clusterOnOffServer == null) {
      log.warn("{}: OnOff converter is not linked to a server and cannot accept commands", getEndpointEntity());
      return null;
    }

    if (command instanceof ZclOnOffCommand) {
      return clusterOnOffServer.sendCommand((ZclOnOffCommand) command);
    }
    return null;
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
    if (getInputCluster(ZclOnOffCluster.CLUSTER_ID) == null
        && endpoint.getOutputCluster(ZclOnOffCluster.CLUSTER_ID) == null) {
      log.trace("{}: OnOff cluster not found", getEndpointEntity());
      return false;
    }

    return true;
  }

  @Override
  public void updateConfiguration() {
    if (clusterOnOffServer == null) {
      return;
    }
    updateServerPoolingPeriod(clusterOnOffServer, ZclOnOffCluster.ATTR_ONOFF, true);

    configOnOff.updateConfiguration();
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("{}: ZigBee attribute reports {}", getEndpointEntity(), attribute);
    if (attribute.getClusterType() == ZclClusterType.ON_OFF && attribute.getId() == ZclOnOffCluster.ATTR_ONOFF) {
      Boolean value = (Boolean) val;
      if (value != null && value) {
        updateChannelState(OnOffType.ON);
      } else {
        updateChannelState(OnOffType.OFF);
      }
    }
  }

  @Override
  public boolean commandReceived(ZclCommand command) {
    log.debug("{}: ZigBee command received {}", getEndpointEntity(), command);
    if (command instanceof OnCommand) {
      currentOnOffState.set(true);
      updateChannelState(OnOffType.ON);
      clusterOnOffClient.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }
    if (command instanceof OnWithTimedOffCommand) {
      currentOnOffState.set(true);
      updateChannelState(OnOffType.ON);
      OnWithTimedOffCommand timedCommand = (OnWithTimedOffCommand) command;
      clusterOnOffClient.sendDefaultResponse(command, ZclStatus.SUCCESS);
      startOffTimer(timedCommand.getOnTime() * 100);
      return true;
    }
    if (command instanceof OffCommand || command instanceof OffWithEffectCommand) {
      currentOnOffState.set(false);
      updateChannelState(OnOffType.OFF);
      clusterOnOffClient.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }
    if (command instanceof ToggleCommand) {
      currentOnOffState.set(!currentOnOffState.get());
      updateChannelState(currentOnOffState.get() ? OnOffType.ON : OnOffType.OFF);
      clusterOnOffClient.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }

    return false;
  }

  private void stopOffTimer() {
    if (updateTimer != null) {
      updateTimer.cancel(true);
      updateTimer = null;
    }
  }

  private void startOffTimer(int delay) {
    stopOffTimer();

    updateTimer = updateScheduler.schedule(() -> {
      log.debug("{}: OnOff auto OFF timer expired", getEndpointEntity());
      updateChannelState(OnOffType.OFF);
      updateTimer = null;
    }, delay, TimeUnit.MILLISECONDS);
  }
}
