package org.touchhome.bundle.zigbee.model.service;

import static org.touchhome.bundle.api.util.Constants.PRIMARY_COLOR;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.ZigBeeIsAliveTracker;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

@Log4j2
@Getter
@RequiredArgsConstructor
public class ZigBeeDiscoveryService implements ZigBeeNetworkNodeListener {

  private final ZigBeeIsAliveTracker zigBeeIsAliveTracker = new ZigBeeIsAliveTracker();

  private final EntityContext entityContext;
  private final ZigBeeChannelConverterFactory channelFactory;

  private volatile boolean scanStarted = false;

  @Setter
  private ZigbeeCoordinatorEntity coordinator;

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

  public void startScan() {
    if (scanStarted) {
      return;
    }
    log.info("Start scanning...");
    scanStarted = true;

    for (ZigBeeNode node : coordinator.getService().getNodes()) {
      if (node.getNetworkAddress() == 0) {
        continue;
      }

      nodeDiscovered(node);
    }

    int duration = coordinator.getDiscoveryDuration();
    coordinator.getService().scanStart(duration);

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
    ZigBeeCoordinatorService coordinatorService = coordinator.getService();
    if (node.getLogicalType() == NodeDescriptor.LogicalType.COORDINATOR || node.getNetworkAddress() == 0) {
      coordinatorService.setNodeIeeeAddress(node.getIeeeAddress());
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

          coordinatorService.serializeNetwork(node.getIeeeAddress());
        });
  }

  /**
   * Add discovered not to DB and in memory
   */
  private synchronized void addZigBeeDevice(ZigBeeNode node) {
    IeeeAddress ieeeAddress = node.getIeeeAddress();

    ZigBeeDeviceEntity entity = entityContext.getEntity(ZigBeeDeviceEntity.PREFIX + ieeeAddress.toString());
    if (entity == null) {
      entity = new ZigBeeDeviceEntity()
          .computeEntityID(ieeeAddress::toString)
          .setIeeeAddress(ieeeAddress.toString())
          .setLogicalType(node.getLogicalType())
          .setNetworkAddress(node.getNetworkAddress());

      entity = entityContext.save(entity);
    }
    entity.getService().tryInitializeDevice();
  }
}
