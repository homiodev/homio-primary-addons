package org.touchhome.bundle.zigbee;

import static org.touchhome.bundle.api.util.Constants.PRIMARY_COLOR;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

@Log4j2
@Getter
public
class ZigBeeDiscoveryService implements ZigBeeNetworkNodeListener {

  private final EntityContext entityContext;
  private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
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
    return coordinator.getService();
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

    entityContext.ui().addHeaderButton("zigbee-scan", PRIMARY_COLOR, duration, null);

    entityContext.bgp().builder("zigbee-scan-killer")
        .delay(Duration.ofSeconds(coordinator.getDiscoveryDuration()))
        .execute(() -> {
          log.info("Scanning stopped");
          scanStarted = false;
          entityContext.ui().removeHeaderButton("zigbee-scan");
        });
  }

  private void nodeDiscovered(ZigBeeNode node) {
    ZigBeeCoordinatorHandler coordinatorHandler = getCoordinatorHandler();
    if (node.getLogicalType() == NodeDescriptor.LogicalType.COORDINATOR || node.getNetworkAddress() == 0) {
      if (coordinatorHandler.getNodeIeeeAddress() == null) {
        coordinatorHandler.setNodeIeeeAddress(node.getIeeeAddress());
      }
      return;
    }

    entityContext.bgp().builder("zigbee-pooling-" + coordinator.getEntityID())
        .delay(Duration.ofMillis(10))
        .execute(() -> {
          log.info("{}: Starting ZigBee device discovery", node.getIeeeAddress());
          addZigBeeDevice(node);

          if (!node.isDiscovered()) {
            log.warn("{}: Node discovery not complete", node.getIeeeAddress());
            return;
          } else {
            log.debug("{}: Node discovery complete", node.getIeeeAddress());
          }

          coordinatorHandler.serializeNetwork(node.getIeeeAddress());
        });
  }

  /**
   * Add discovered not to DB and in memory
   */
  synchronized void addZigBeeDevice(ZigBeeNode node) {
    IeeeAddress ieeeAddress = node.getIeeeAddress();

    ZigBeeDeviceEntity entity = entityContext.getEntity(ZigBeeDeviceEntity.PREFIX + ieeeAddress.toString());
    if (entity == null) {
      entity = new ZigBeeDeviceEntity()
          .computeEntityID(ieeeAddress::toString)
          .setIeeeAddress(ieeeAddress.toString())
          .setLogicalType(node.getLogicalType())
          .setNetworkAddress(node.getNetworkAddress());

      entityContext.save(entity);
    }

    ZigBeeCoordinatorHandler coordinatorHandler = getCoordinatorHandler();
    boolean deviceAdded = coordinatorHandler.getZigBeeDevices().keySet().stream()
        .anyMatch(d -> d.equals(ieeeAddress.toString()));
    if (!deviceAdded) {
      ZigBeeDevice zigBeeDevice = new ZigBeeDevice(this, ieeeAddress, node, entityContext);
      coordinatorHandler.addZigBeeDevice(zigBeeDevice);
    }
  }
}
