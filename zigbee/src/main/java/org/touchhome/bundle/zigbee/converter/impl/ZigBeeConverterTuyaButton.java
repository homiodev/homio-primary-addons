package org.touchhome.bundle.zigbee.converter.impl;

import static java.lang.Integer.toHexString;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.ZclCommand;
import com.zsmartsystems.zigbee.zcl.ZclCommandListener;
import com.zsmartsystems.zigbee.zcl.ZclStatus;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclThermostatCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.command.TuyaButtonPressCommand;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

/**
 * Kind - trigger Button Pressed Event Emits events when button is pressed
 */
@ZigBeeConverter(name = "zigbee:tuya_button", category = "Light", linkType = VariableType.Boolean,
    clientCluster = ZclOnOffCluster.CLUSTER_ID, serverClusters = {ZclOnOffCluster.CLUSTER_ID})
public class ZigBeeConverterTuyaButton extends ZigBeeInputBaseConverter implements ZclCommandListener {

  private final int INVALID_TEMPERATURE = 0x8000;
  private ZclCluster clientCluster = null;

  // Tuya devices sometimes send duplicate commands with the same tx id.
  // We keep track of the last received Tx id and ignore the duplicate.
  private Integer lastTxId = -1;

  public ZigBeeConverterTuyaButton() {
    super(ZclClusterType.ON_OFF, ZclThermostatCluster.ATTR_LOCALTEMPERATURE, null);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, Objects.requireNonNull(configReporting).getPollingPeriod());
  }

  @Override
  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    super.initialize(endpointService, endpoint);
    configReporting = new ZclReportingConfig(getEntity());
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() == ZclClusterType.THERMOSTAT
        && attribute.getId() == ZclThermostatCluster.ATTR_LOCALTEMPERATURE) {
      Integer value = (Integer) val;
      if (value != null && value != INVALID_TEMPERATURE) {
        updateChannelState(valueToTemperature(value));
      }
    }
  }

  @Override
  public void initializeDevice() throws Exception {
    ZclCluster clientCluster = endpoint.getOutputCluster(ZclOnOffCluster.CLUSTER_ID);

    if (clientCluster == null) {
      log.error("[{}]: Error opening client cluster {} on {}", entityID, ZclOnOffCluster.CLUSTER_ID, endpoint);
      throw new RuntimeException("Error opening client cluster");
    }

    // check server cluster
    super.initializeDevice();

    // bind client cluster
    try {
      CommandResult bindResponse = bind(clientCluster).get();
      if (!bindResponse.isSuccess()) {
        log.error("[{}]: Error 0x{} setting client binding for cluster {}. {}", entityID,
            toHexString(bindResponse.getStatusCode()), ZclOnOffCluster.CLUSTER_ID, endpoint);
      }
    } catch (InterruptedException | ExecutionException e) {
      log.error("[{}]: Exception setting client binding to cluster {}: {}", entityID,
          ZclOnOffCluster.CLUSTER_ID, endpoint, e);
    }
  }

  @Override
  public synchronized void initializeConverter() {
    super.initializeConverter();

    clientCluster = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);

    if (clientCluster == null) {
      log.error("[{}]: Error opening device client controls {}", entityID, endpoint);
      throw new RuntimeException("Error opening device client controls");
    }

    clientCluster.addCommandListener(this);

    // Add Tuya-specific command
    //
    HashMap<Integer, Class<? extends ZclCommand>> commandMap = new HashMap<>();
    commandMap.put(TuyaButtonPressCommand.COMMAND_ID, TuyaButtonPressCommand.class);
    clientCluster.addClientCommands(commandMap);
  }

  @Override
  public void disposeConverter() {
    super.disposeConverter();
    if (clientCluster != null) {
      clientCluster.removeCommandListener(ZigBeeConverterTuyaButton.this);
    }
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    // This converter is used only for channels specified in static thing types, and cannot be used to construct
    // channels based on an endpoint alone.
    return false;
  }

  @Override
  public void handleRefresh() {
    // nothing to do, as we only listen to commands
  }

  /*@Override
  public Channel getChannel(ThingUID thingUID, ZigBeeEndpoint endpoint) {
    // This converter is used only for channels specified in static thing types, and cannot be used to construct
    // channels based on an endpoint alone.
    return null;
  }*/

  @Override
  public boolean commandReceived(ZclCommand command) {
    log.debug("[{}]: received command {}. {}", entityID, command, endpoint);
    Integer thisTxId = command.getTransactionId();
    if (Objects.equals(lastTxId, thisTxId)) {
      log.debug("[{}]: ignoring duplicate command {}. {}", entityID, thisTxId, endpoint);
    } else if (command instanceof TuyaButtonPressCommand) {
      TuyaButtonPressCommand tuyaButtonPressCommand = (TuyaButtonPressCommand) command;
      // TODO:   thing.triggerChannel(channel.getUID(), getEventType(tuyaButtonPressCommand.getPressType()));
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
    } else {
      log.warn("[{}]: received unknown command {}. {}", entityID, command, endpoint);
    }

    lastTxId = thisTxId;
    return true;
  }

  /*private String getEventType(Integer pressType) {
    switch (pressType) {
      case 0:
        return CommonTriggerEvents.SHORT_PRESSED;
      case 1:
        return CommonTriggerEvents.DOUBLE_PRESSED;
      case 2:
        return CommonTriggerEvents.LONG_PRESSED;
      default:
        log.warn("[{}]: received unknown pressType {}", endpoint, pressType);
        return CommonTriggerEvents.SHORT_PRESSED;
    }
  }*/
}
