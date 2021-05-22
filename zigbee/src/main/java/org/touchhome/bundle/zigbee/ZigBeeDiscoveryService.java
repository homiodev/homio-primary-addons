package org.touchhome.bundle.zigbee;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.setting.ZigBeeDiscoveryDurationSetting;
import org.touchhome.bundle.zigbee.setting.advanced.ZigBeeJoinDeviceDuringScanOnlySetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.touchhome.bundle.api.util.Constants.PRIMARY_COLOR;

@Log4j2
@Getter
class ZigBeeDiscoveryService implements ZigBeeNetworkNodeListener {

    private final EntityContext entityContext;
    private final Supplier<ZigBeeCoordinatorHandler> coordinatorHandler;
    private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
    private final ScheduledExecutorService scheduler;
    private final ZigBeeDeviceUpdateValueListener deviceUpdateListener;
    private final ZigBeeIsAliveTracker zigBeeIsAliveTracker;

    private volatile boolean scanStarted = false;

    ZigBeeDiscoveryService(EntityContext entityContext, Supplier<ZigBeeCoordinatorHandler> coordinatorHandler,
                           ZigBeeIsAliveTracker zigBeeIsAliveTracker,
                           ZigBeeChannelConverterFactory zigBeeChannelConverterFactory,
                           ScheduledExecutorService scheduler,
                           ZigBeeDeviceUpdateValueListener deviceUpdateListener) {
        this.entityContext = entityContext;
        this.coordinatorHandler = coordinatorHandler;
        this.zigBeeIsAliveTracker = zigBeeIsAliveTracker;
        this.zigBeeChannelConverterFactory = zigBeeChannelConverterFactory;
        this.scheduler = scheduler;
        this.deviceUpdateListener = deviceUpdateListener;
    }

    @Override
    public void nodeAdded(ZigBeeNode node) {
        boolean forceAdd = !entityContext.setting().getValue(ZigBeeJoinDeviceDuringScanOnlySetting.class);
        if (forceAdd || scanStarted) {
            ZigBeeDiscoveryService.this.nodeDiscovered(coordinatorHandler.get(), node);
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
        return coordinatorHandler.get();
    }

    void startScan() {
        if (scanStarted) {
            return;
        }
        log.info("Start scanning...");
        scanStarted = true;

        for (ZigBeeNode node : coordinatorHandler.get().getNodes()) {
            if (node.getNetworkAddress() == 0) {
                continue;
            }

            nodeDiscovered(coordinatorHandler.get(), node);
        }

        Integer duration = entityContext.setting().getValue(ZigBeeDiscoveryDurationSetting.class);
        coordinatorHandler.get().scanStart(duration);

        this.entityContext.ui().addHeaderButton("zigbee-scan", PRIMARY_COLOR, duration, null);

        scheduler.schedule(() -> {
            log.info("Scanning stopped");
            scanStarted = false;
            this.entityContext.ui().removeHeaderButton("zigbee-scan");
        }, duration, TimeUnit.SECONDS);
    }

    private void nodeDiscovered(ZigBeeCoordinatorHandler coordinator, final ZigBeeNode node) {
        if (node.getLogicalType() == NodeDescriptor.LogicalType.COORDINATOR || node.getNetworkAddress() == 0) {
            if (this.coordinatorHandler.get().getNodeIeeeAddress() == null) {
                this.coordinatorHandler.get().setNodeIeeeAddress(node.getIeeeAddress());
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

            coordinator.serializeNetwork(node.getIeeeAddress());
        };

        scheduler.schedule(pollingRunnable, 10, TimeUnit.MILLISECONDS);
    }

    synchronized void addZigBeeDevice(IeeeAddress ieeeAddress) {
        boolean deviceAdded = this.coordinatorHandler.get().getZigBeeDevices().keySet().stream()
                .anyMatch(d -> d.equals(ieeeAddress.toString()));
        if (!deviceAdded) {
            ZigBeeDevice zigBeeDevice = new ZigBeeDevice(this, ieeeAddress);
            this.coordinatorHandler.get().addZigBeeDevice(zigBeeDevice);
        }
    }
}
