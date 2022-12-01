package org.touchhome.bundle.zigbee.converter.impl.cluster;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster.ATTR_ONOFF;

import com.zsmartsystems.zigbee.zcl.ZclCommand;
import com.zsmartsystems.zigbee.zcl.ZclCommandListener;
import com.zsmartsystems.zigbee.zcl.ZclStatus;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OffCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OffWithEffectCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OnCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OnWithTimedOffCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.ToggleCommand;
import java.time.Duration;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.OnOffType;

@Log4j2
@Getter
public class ZigBeeSwitchApplication extends ZigBeeGeneralApplication implements ZclCommandListener {

  private ZclOnOffCluster clientCluster;

  @Override
  public void appStartup() {
    this.zclCluster = endpoint.getInputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (zclCluster != null) {
      super.appStartup();
    }
    this.clientCluster = (ZclOnOffCluster) endpoint.getOutputCluster(ZclOnOffCluster.CLUSTER_ID);
    if (clientCluster != null) {
      Status bindStatus = bindCluster(clientCluster);
      clientCluster.addCommandListener(this);
      if (zclCluster == null) {
        initializeAttribute(createAttribute(clientCluster, ATTR_ONOFF), bindStatus == Status.DONE);
      }
    }
  }

  @Override
  public boolean commandReceived(ZclCommand command) {
    log.debug("[{}]: ZigBee command received {}. {}", deviceEntityID, command, endpoint);
    if (command instanceof OnCommand) {
      getAttributeHandler(ATTR_ONOFF).ifPresent(handler -> handler.setValue(OnOffType.ON));
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }
    if (command instanceof OnWithTimedOffCommand) {
      getAttributeHandler(ATTR_ONOFF).ifPresent(handler -> handler.setValue(OnOffType.ON));
      OnWithTimedOffCommand timedCommand = (OnWithTimedOffCommand) command;
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
      startOffTimer(timedCommand.getOnTime() * 100);
      return true;
    }
    if (command instanceof OffCommand || command instanceof OffWithEffectCommand) {
      getAttributeHandler(ATTR_ONOFF).ifPresent(handler -> handler.setValue(OnOffType.OFF));
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }
    if (command instanceof ToggleCommand) {
      getAttributeHandler(ATTR_ONOFF).ifPresent(handler ->
          handler.setValue(handler.getValue().boolValue() ? OnOffType.OFF : OnOffType.ON));
      clientCluster.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }

    return false;
  }

  private void startOffTimer(int delay) {
    getEntityContext().bgp().builder("zigbee-switch-off-timer-" + getEntityID())
        .delay(Duration.ofSeconds(delay))
        .execute(() -> {
          log.debug("[{}]: OnOff auto OFF timer expired {}", deviceEntityID, endpoint);
          getAttributeHandler(ATTR_ONOFF).ifPresent(handler -> handler.setValue(OnOffType.OFF));
        });
  }
}
