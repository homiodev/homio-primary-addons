package org.touchhome.bundle.zigbee;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.touchhome.bundle.api.util.Constants.PRIMARY_COLOR;

@Log4j2
@Getter
public
class ZigBeeDiscoveryService implements ZigBeeNetworkNodeListener {

    private final EntityContext entityContext;
    private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final ZigBeeDeviceUpdateValueListener deviceUpdateListener;
    private final ZigBeeIsAliveTracker zigBeeIsAliveTracker = new ZigBeeIsAliveTracker();

    private volatile boolean scanStarted = false;

    @Setter
    private ZigbeeCoordinatorEntity coordinator;

    public ZigBeeDiscoveryService(EntityContext entityContext) {
        this.entityContext = entityContext;
        this.zigBeeChannelConverterFactory = entityContext.getBean(ZigBeeChannelConverterFactory.class);
        this.deviceUpdateListener = entityContext.getBean(ZigBeeDeviceUpdateValueListener.class);
    }

    @Override
    public void nodeAdded(ZigBeeNode node) {
        if (!coordinator.isJoinDeviceDuringScanOnly() || scanStarted) {
            nodeDiscovered(node);
        }
    }

    @Override
    public void nodeRemoved(ZigBeeNode node) {
        log.debug("Node removed: <{}>", node);
    }

    @Override
    public void nodeUpdated(ZigBeeNode node) {
        log.debug("Node updated: <{}>", node);
    }

    public ZigBeeCoordinatorHandler getCoordinatorHandler() {
        return coordinator.getZigBeeCoordinatorHandler();
    }

    public void startScan() {
        if (scanStarted) {
            return;
        }
        log.info("Start scanning...");
        scanStarted = true;

        for (ZigBeeNode node : getCoordinatorHandler().getNodes()) {
            if (node.getNetworkAddress() == 0) {
                continue;
            }

            nodeDiscovered(node);
        }

        int duration = coordinator.getDiscoveryDuration();
        getCoordinatorHandler().scanStart(duration);

        this.entityContext.ui().addHeaderButton("zigbee-scan", PRIMARY_COLOR, duration, null);

        scheduler.schedule(() -> {
            log.info("Scanning stopped");
            scanStarted = false;
            this.entityContext.ui().removeHeaderButton("zigbee-scan");
        }, duration, TimeUnit.SECONDS);
    }

    private void nodeDiscovered(ZigBeeNode node) {
        ZigBeeCoordinatorHandler coordinatorHandler = getCoordinatorHandler();
        if (node.getLogicalType() == NodeDescriptor.LogicalType.COORDINATOR || node.getNetworkAddress() == 0) {
            if (coordinatorHandler.getNodeIeeeAddress() == null) {
                coordinatorHandler.setNodeIeeeAddress(node.getIeeeAddress());
            }
            return;
        }

        Runnable pollingRunnable = () -> {
            log.info("{}: Starting ZigBee device discovery", node.getIeeeAddress());

            ZigBeeDeviceEntity entity = entityContext.getEntity(ZigBeeDeviceEntity.PREFIX + node.getIeeeAddress().toString());
            if (entity == null) {
                entity = new ZigBeeDeviceEntity().computeEntityID(() -> node.getIeeeAddress().toString())
                        .setIeeeAddress(node.getIeeeAddress().toString()).setNetworkAddress(node.getNetworkAddress());
                entityContext.save(entity);
            }

            addZigBeeDevice(node.getIeeeAddress());

            if (!node.isDiscovered()) {
                log.warn("{}: Node discovery not complete", node.getIeeeAddress());
                return;
            } else {
                log.debug("{}: Node discovery complete", node.getIeeeAddress());
            }

            coordinatorHandler.serializeNetwork(node.getIeeeAddress());
        };

        scheduler.schedule(pollingRunnable, 10, TimeUnit.MILLISECONDS);
    }

    synchronized void addZigBeeDevice(IeeeAddress ieeeAddress) {
        ZigBeeCoordinatorHandler coordinatorHandler = getCoordinatorHandler();
        boolean deviceAdded = coordinatorHandler.getZigBeeDevices().keySet().stream()
                .anyMatch(d -> d.equals(ieeeAddress.toString()));
        if (!deviceAdded) {
            ZigBeeDevice zigBeeDevice = new ZigBeeDevice(this, ieeeAddress);
            coordinatorHandler.addZigBeeDevice(zigBeeDevice);
        }
    }
}
