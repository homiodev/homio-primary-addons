package org.touchhome.bundle.zigbee.converter.impl;

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
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.zigbee.converter.impl.command.TuyaButtonPressCommand;

/**
 * Kind - trigger Button Pressed Event Emits events when button is pressed
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:tuya_button", category = "Light",
    clientCluster = ZclOnOffCluster.CLUSTER_ID, serverClusters = {ZclOnOffCluster.CLUSTER_ID})
public class ZigBeeConverterTuyaButton extends ZigBeeInputBaseConverter implements ZclCommandListener {

  private final int INVALID_TEMPERATURE = 0x8000;
  private ZclCluster clientCluster = null;

  // Tuya devices sometimes send duplicate commands with the same tx id.
  // We keep track of the last received Tx id and ignore the duplicate.
  private Integer lastTxId = -1;

  public ZigBeeConverterTuyaButton() {
    super(ZclClusterType.ON_OFF, ZclThermostatCluster.ATTR_LOCALTEMPERATURE,
        1, REPORTING_PERIOD_DEFAULT_MAX, 10);
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("{}: ZigBee attribute reports {}", getEndpointEntity(), attribute);
    if (attribute.getClusterType() == ZclClusterType.THERMOSTAT
        && attribute.getId() == ZclThermostatCluster.ATTR_LOCALTEMPERATURE) {
      Integer value = (Integer) val;
      if (value != null && value != INVALID_TEMPERATURE) {
        updateChannelState(valueToTemperature(value));
      }
    }
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = POLLING_PERIOD_HIGH;
    return true;
  }

  @Override
  public boolean initializeDevice() {
    super.initializeDevice();
    // ZclCluster clientCluster = endpoint.getOutputCluster(ZclOnOffCluster.CLUSTER_ID);

    if (clientCluster == null) {
      log.error("{}: Error opening client cluster {} on endpoint", getEndpointEntity(), ZclOnOffCluster.CLUSTER_ID);
      return false;
    }

    /* TODO: try {
      CommandResult bindResponse = bind(clientCluster).get();
      if (!bindResponse.isSuccess()) {
        log.error("{}: Error 0x{} setting client binding for cluster {}", getEndpointEntity(),
            toHexString(bindResponse.getStatusCode()), ZclOnOffCluster.CLUSTER_ID);
      }
    } catch (InterruptedException | ExecutionException e) {
      log.error("{}: Exception setting client binding to cluster {}: {}", getEndpointEntity(),
          ZclOnOffCluster.CLUSTER_ID, e);
    }*/

    return true;
  }

  @Override
  public synchronized boolean initializeConverter() {
    super.initializeConverter();

    clientCluster = getOutputCluster(ZclOnOffCluster.CLUSTER_ID);

    if (clientCluster == null) {
      log.error("{}: Error opening device client controls", getEndpointEntity());
      return false;
    }

    clientCluster.addCommandListener(this);

    // Add Tuya-specific command
    //
    HashMap<Integer, Class<? extends ZclCommand>> commandMap = new HashMap<>();
    commandMap.put(TuyaButtonPressCommand.COMMAND_ID, TuyaButtonPressCommand.class);
    clientCluster.addClientCommands(commandMap);

    return true;
  }

  @Override
  public void disposeConverter() {
    super.disposeConverter();
    if (clientCluster != null) {
      clientCluster.removeCommandListener(ZigBeeConverterTuyaButton.this);
    }
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
    log.debug("{}: received command {}", getEndpointEntity(), command);
    Integer thisTxId = command.getTransactionId();
    if (Objects.equals(lastTxId, thisTxId)) {
      log.debug("{}: ignoring duplicate command {}", getEndpointEntity(), thisTxId);
    } else if (command instanceof TuyaButtonPressCommand) {
      TuyaButtonPressCommand tuyaButtonPressCommand = (TuyaButtonPressCommand) command;
      // TODO:   thing.triggerChannel(channel.getUID(), getEventType(tuyaButtonPressCommand.getPressType()));
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
    } else {
      log.warn("{}: received unknown command {}", getEndpointEntity(), command);
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
        log.warn("{}: received unknown pressType {}", getEndpointEntity(), pressType);
        return CommonTriggerEvents.SHORT_PRESSED;
    }
  }*/
}
