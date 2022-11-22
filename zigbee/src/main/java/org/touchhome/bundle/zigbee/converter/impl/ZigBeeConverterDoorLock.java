package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster.ATTR_LOCKSTATE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.DOOR_LOCK;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclDoorLockConfig;

/**
 * Locks and unlocks the door and maintains the lock state This channel supports changes through attribute updates to the door lock state. ON=Locked, OFF=Unlocked.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:door_state", linkType = VariableType.Boolean, clientCluster = ZclDoorLockCluster.CLUSTER_ID, category = "Door")
public class ZigBeeConverterDoorLock extends ZigBeeInputBaseConverter {

  public ZigBeeConverterDoorLock() {
    super(DOOR_LOCK, ATTR_LOCKSTATE, 1, REPORTING_PERIOD_DEFAULT_MAX, null);
  }

  @Override
  protected boolean initializeDeviceFailed() {
    pollingPeriod = POLLING_PERIOD_HIGH;
    return true;
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return super.acceptEndpoint(endpoint, entityID, false, false);
  }

  @Override
  public boolean initializeDevice() {
    ZclDoorLockCluster serverCluster = getInputCluster(ZclDoorLockCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("[{}]: Error opening device door lock controls {}", entityID, endpoint);
      return false;
    }

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting - no faster than once per second - no slower than 2 hours.
        ZclAttribute attribute = serverCluster.getAttribute(ZclDoorLockCluster.ATTR_LOCKSTATE);
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
  protected void afterInitializeConverter() {
    this.configDoorLock = new ZclDoorLockConfig(getEntity(), getZclCluster());
  }

  /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        if (command == OnOffType.ON) {
            cluster.lockDoorCommand(new ByteArray(new byte[0]));
        } else {
            cluster.unlockDoorCommand(new ByteArray(new byte[0]));
        }
    }*/
}
