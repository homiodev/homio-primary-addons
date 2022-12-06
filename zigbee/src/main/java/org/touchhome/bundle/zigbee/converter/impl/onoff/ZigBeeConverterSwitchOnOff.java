package org.touchhome.bundle.zigbee.converter.impl.onoff;

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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.config.ZclOnOffSwitchConfig;
import org.touchhome.bundle.zigbee.converter.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * This channel supports changes through attribute updates, and also through received commands. This allows a switch that is not connected to a load to send commands, or a switch
 * that is connected to a load to send status (or both!).
 */
@ZigBeeConverter(name = "zigbee:switch_onoff", linkType = VariableType.Boolean,
    serverClusters = {ZclOnOffCluster.CLUSTER_ID}, clientCluster = ZclOnOffCluster.CLUSTER_ID, category = "Light")
public class ZigBeeConverterSwitchOnOff extends ZigBeeBaseChannelConverter
    implements ZclAttributeListener, ZclCommandListener {

  private final AtomicBoolean currentOnOffState = new AtomicBoolean(true);
  private ZclOnOffCluster clusterOnOffClient;
  private ZclOnOffCluster clusterOnOffServer;
  private ZclAttribute attributeServer;
  private ScheduledExecutorService updateScheduler;
  private ScheduledFuture<?> updateTimer = null;

  @Override
  public void initializeDevice() {
    ZclOnOffCluster clientCluster = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);
    ZclOnOffCluster serverCluster = getInputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (clientCluster == null && serverCluster == null) {
      log.error("[{}]: Error opening device on/off controls {}", entityID, this.endpoint);
      throw new RuntimeException("Error opening device on/off controls");
    }

    if (serverCluster != null) {
      try {
        this.configReporting = new ZclReportingConfig(getEntity());
        CommandResult bindResponse = bind(serverCluster).get();
        if (bindResponse.isSuccess()) {
          ZclAttribute attribute = serverCluster.getAttribute(ZclOnOffCluster.ATTR_ONOFF);
          CommandResult reportingResponse = attribute
              .setReporting(configReporting.getReportingTimeMin(), configReporting.getReportingTimeMax()).get();
          handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, configReporting.getPollingPeriod());
        } else {
          log.debug("[{}]: Error 0x{} setting server binding {}", entityID, Integer.toHexString(bindResponse.getStatusCode()), this.endpoint);
          pollingPeriod = POLLING_PERIOD_HIGH;
        }
      } catch (Exception e) {
        log.error("[{}]: Exception setting reporting {}", entityID, this.endpoint, e);
      }
    }

    if (clientCluster != null) {
      try {
        CommandResult bindResponse = bind(clientCluster).get();
        if (!bindResponse.isSuccess()) {
          log.error("[{}]: Error 0x{} setting client binding {}", entityID, Integer.toHexString(bindResponse.getStatusCode()), this.endpoint);
        }
      } catch (Exception e) {
        log.error("[{}]: Exception setting binding {}", entityID, this.endpoint, e);
      }
    }
  }

  @Override
  public void initializeConverter() {
    updateScheduler = Executors.newSingleThreadScheduledExecutor();

    clusterOnOffClient = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);
    clusterOnOffServer = getInputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (clusterOnOffClient == null && clusterOnOffServer == null) {
      log.error("[{}]: Error opening device on/off controls {}", entityID, endpoint);
      throw new RuntimeException("Error opening device on/off controls");
    }

    if (clusterOnOffServer != null) {
      configOnOff = new ZclOnOffSwitchConfig(getEntity(), clusterOnOffServer, log);
      configReporting = new ZclReportingConfig(getEntity());
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
  }

  @Override
  public void disposeConverter() {
    log.debug("[{}]: Closing device on/off cluster {}", entityID, endpoint);

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
    if (configReporting != null) {
      return configReporting.getPollingPeriod();
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
      log.warn("[{}]: OnOff converter is not linked to a server and cannot accept commands {}", entityID, endpoint);
      return null;
    }

    if (command instanceof ZclOnOffCommand) {
      return clusterOnOffServer.sendCommand((ZclOnOffCommand) command);
    }
    return null;
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    if (endpoint.getInputCluster(ZclOnOffCluster.CLUSTER_ID) == null
        && endpoint.getOutputCluster(ZclOnOffCluster.CLUSTER_ID) == null) {
      log.trace("[{}]: OnOff cluster not found {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public void updateConfiguration() {
    if (clusterOnOffServer == null) {
      return;
    }
    if (configReporting != null && configReporting.updateConfiguration(getEntity())) {
      updateServerPollingPeriodNoChange(clusterOnOffServer, ZclOnOffCluster.ATTR_ONOFF);
    }
    if (configOnOff != null) {
      configOnOff.updateConfiguration(getEntity());
    }
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
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
    log.debug("[{}]: ZigBee command received {}. {}", entityID, command, endpoint);
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
      log.debug("[{}]: OnOff auto OFF timer expired {}", entityID, endpoint);
      updateChannelState(OnOffType.OFF);
      updateTimer = null;
    }, delay, TimeUnit.MILLISECONDS);
  }

  @Override
  public void assembleActions(UIInputBuilder uiInputBuilder) {
    super.assembleActions(uiInputBuilder);
    if (clusterOnOffServer != null) {
      uiInputBuilder.addButton("on", "fas fa-toggle-off", "", (entityContext, params) -> {
        clusterOnOffServer.sendCommand(new OnCommand());
        return null;
      });
      uiInputBuilder.addButton("on", "fas fa-toggle-on", "", (entityContext, params) -> {
        clusterOnOffServer.sendCommand(new OffCommand());
        return null;
      });
    }
  }
}
