package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster;
import lombok.extern.log4j.Log4j2;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclDoorLockCluster.ATTR_LOCKSTATE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.DOOR_LOCK;

/**
 * This channel supports changes through attribute updates to the door lock state. ON=Locked, OFF=Unlocked.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:door_state", clientClusters = {ZclDoorLockCluster.CLUSTER_ID})
public class ZigBeeConverterDoorLock extends ZigBeeInputBaseConverter {

    public ZigBeeConverterDoorLock() {
        super(DOOR_LOCK, ATTR_LOCKSTATE, 1, REPORTING_PERIOD_DEFAULT_MAX, null);
    }

    @Override
    protected boolean initializeDeviceFailed() {
        pollingPeriod = POLLING_PERIOD_HIGH;
        return true;
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
