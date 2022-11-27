package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster.ATTR_LOCKSTATE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.DOOR_LOCK;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.config.ZclDoorLockConfig;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;

/**
 * Locks and unlocks the door and maintains the lock state This channel supports changes through attribute updates to the door lock state. ON=Locked, OFF=Unlocked.
 */
@ZigBeeConverter(name = "zigbee:door_state", linkType = VariableType.Boolean, clientCluster = ZclDoorLockCluster.CLUSTER_ID, category = "Door")
public class ZigBeeConverterDoorLock extends ZigBeeInputBaseConverter {

  public ZigBeeConverterDoorLock() {
    super(DOOR_LOCK, ATTR_LOCKSTATE, 1, REPORTING_PERIOD_DEFAULT_MAX, null, POLLING_PERIOD_HIGH);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  public void initialize(ZigbeeEndpointService endpointService, ZigBeeEndpoint endpoint) {
    super.initialize(endpointService, endpoint);
    this.configDoorLock = new ZclDoorLockConfig(getEntity(), getZclCluster(), log);
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
