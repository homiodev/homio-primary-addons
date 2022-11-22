package org.touchhome.bundle.zigbee.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeAnnounceListener;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.ZigBeeNodeStatus;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.ZigBeeStatus;
import java.time.Duration;
import java.util.Collection;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP.ThreadContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.zigbee.ZigBeeIsAliveTracker;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition.EndpointDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeDefineEndpoints;
import org.touchhome.common.util.Lang;

@Getter
@Log4j2
public class ZigBeeDeviceService implements ZigBeeNetworkNodeListener, ZigBeeAnnounceListener, ServiceInstance<ZigBeeDeviceEntity> {

  private final Object entityUpdateSync = new Object();
  private final Object pollingSync = new Object();

  private final IeeeAddress nodeIeeeAddress;
  private final ZigBeeCoordinatorService coordinatorService;
  private final EntityContext entityContext;
  private final ZigBeeIsAliveTracker zigBeeIsAliveTracker;
  private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
  private final String entityID;
  private ThreadContext<Void> pollingJob = null;
  private int pollingPeriod = 86400;
  private ThreadContext<Void> nodeDiscoveryThreadContext;
  private ZigBeeDeviceEntity entity;
  @Setter
  private Integer expectedUpdateInterval;
  @Setter
  private Long expectedUpdateIntervalTimer;

  public ZigBeeDeviceService(ZigBeeCoordinatorService coordinatorService, IeeeAddress nodeIeeeAddress,
      EntityContext entityContext, ZigBeeDeviceEntity entity) {
    this.entityID = entity.getEntityID();
    log.info("[{}]: Creating zigBee device {}", entityID, nodeIeeeAddress);
    this.entity = entity;
    this.entityContext = entityContext;

    this.coordinatorService = coordinatorService;
    this.zigBeeIsAliveTracker = coordinatorService.getDiscoveryService().getZigBeeIsAliveTracker();
    this.zigBeeChannelConverterFactory = coordinatorService.getDiscoveryService().getChannelFactory();

    this.nodeIeeeAddress = nodeIeeeAddress;

    tryInitializeDevice();

    // register listener for reset timer if any updates from any endpoint
    entityContext.event().addEventListener(this.nodeIeeeAddress.toString(), state -> {
      zigBeeIsAliveTracker.resetTimer(this);
      entity.setLastAnswerFromEndpoints(System.currentTimeMillis());
    });
  }

  public void tryInitializeDevice() {
    if (coordinatorService.getEntity().getStatus() != Status.ONLINE) {
      entity.setNodeInitializationStatus(Status.UNKNOWN);
      stopPolling();
    } else if (entity.getNodeInitializationStatus() == Status.UNKNOWN) {
      log.debug("[{}]: Coordinator is ONLINE. Starting device initialisation. {}", entityID, nodeIeeeAddress);

      this.coordinatorService.addNetworkNodeListener(this);
      this.coordinatorService.addAnnounceListener(this);
      this.coordinatorService.rediscoverNode(nodeIeeeAddress);

      initializeZigBeeNode();
    }
  }

  /**
   * synchronized to handle from multiple threads
   */
  public synchronized void initializeZigBeeNode() {
    if (entity.getNodeInitializationStatus() == Status.UNKNOWN || entity.getNodeInitializationStatus() == Status.DONE) {
      entity.setStatus(Status.WAITING, null);
      entity.setNodeInitializationStatus(Status.WAITING);
      entityContext.bgp().builder("zigbee-node-init-" + this.nodeIeeeAddress)
          .delay(Duration.ofMillis(10)).execute(this::doNodeInitialisation);
    } else {
      log.warn("[{}]: Node {} initialization already started: {}", entityID, nodeIeeeAddress, entity.getNodeInitializationStatus());
    }
  }

  private void doNodeInitialisation() {
    log.info("[{}]: Initialization zigBee device {}", entityID, nodeIeeeAddress);
    if (this.entity.getStatus() != Status.WAITING) {
      log.error("[{}]: Something went wrong for node initialization workflow {}", entityID, nodeIeeeAddress);
    }
    this.entity.setStatus(Status.INITIALIZE, null);
    this.entity.setNodeInitializationStatus(Status.INITIALIZE);
    try {
      ZigBeeNode node = this.coordinatorService.getNode(nodeIeeeAddress);
      if (node == null) {
        log.debug("[{}]: Node not found {}", entityID, nodeIeeeAddress);
        throw new RuntimeException("zigbee.error.offline_node_not_found");
      }

      // Check if discovery is complete, and we know all the services the node supports
      if (!node.isDiscovered()) {
        log.debug("[{}]: Node has not finished discovery {}", entityID, nodeIeeeAddress);
        throw new RuntimeException("zigbee.error.offline_discovery_incomplete");
      }

      log.debug("[{}]: Start initialising ZigBee channels {}", entityID, nodeIeeeAddress);

      updateNodeDescription(node);
      createDynamicEndpoints();

      if (entity.getModelIdentifier() != null) {
        createMissingRequireEndpointClusters();
      }

      // Create missing endpoints in zsmartsystem.node
      for (ZigBeeEndpointEntity zigBeeEndpointEntity : entity.getEndpoints()) {
        ZigBeeEndpoint endpoint = node.getEndpoint(zigBeeEndpointEntity.getAddress());
        if (endpoint == null) {
          int profileId = ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION.getKey();
          log.debug("[{}]: Creating statically defined device {} endpoint {} with profile {}", entityID, nodeIeeeAddress,
              zigBeeEndpointEntity.getAddress(), ZigBeeProfileType.getByValue(profileId));
          endpoint = new ZigBeeEndpoint(node, zigBeeEndpointEntity.getAddress());
          endpoint.setProfileId(profileId);
          node.addEndpoint(endpoint);
        }
      }

      entity.setStatus(Status.WAITING, null);
      initializeZigBeeChannelConverters();

      // If this is an RFD then we reduce polling to the max to avoid wasting battery
      if (node.isReducedFunctionDevice()) {
        pollingPeriod = 1800;
      }

      int expectedUpdatePeriod = getExpectedUpdatePeriod();
      expectedUpdatePeriod = (expectedUpdatePeriod * 2) + 30;
      log.debug("[{}]: Setting ONLINE/OFFLINE {} timeout interval to: {}sec.", entityID, nodeIeeeAddress, expectedUpdatePeriod);
      zigBeeIsAliveTracker.addHandler(this, expectedUpdatePeriod);

      // Update the binding table.
      // We're not doing anything with the information here, but we want it up to date, so it's ready for use later.
      try {
        ZigBeeStatus zigBeeStatus = node.updateBindingTable().get();
        if (zigBeeStatus != ZigBeeStatus.SUCCESS) {
          log.debug("[{}]: Error getting binding table. {}. Actual status: <{}>", entityID, nodeIeeeAddress, zigBeeStatus);
        }
      } catch (Exception e) {
        log.error("[{}]: Exception getting binding table {}", entityID, nodeIeeeAddress, e);
      }
      entity.setStatusOnline();

      startPolling();
      this.entity.setNodeInitializationStatus(Status.DONE);
      log.debug("[{}]: Done initialising ZigBee device {}", entityID, nodeIeeeAddress);

      // Save the network state
      this.coordinatorService.serializeNetwork(node.getIeeeAddress());
    } catch (Exception ex) {
      entity.setStatusError(ex);
      this.entity.setNodeInitializationStatus(Status.UNKNOWN);
    }
  }

  private void createDynamicEndpoints() {
    // Dynamically create the zigBeeConverterEndpoints from the device
    // Process all the endpoints for this device and add all zigBeeConverterEndpoints as derived from the supported clusters
    log.info("[{}]: Try out to find zigbee endpoints {}", entityID, nodeIeeeAddress);
    for (ZigBeeEndpoint endpoint : this.coordinatorService.getNodeEndpoints(nodeIeeeAddress)) {
      Collection<ZigBeeBaseChannelConverter> cluster = zigBeeChannelConverterFactory.getZigBeeConverterEndpoints(endpoint, entityID);
      createEndpoints(endpoint.getEndpointId(), cluster, null);
    }
    log.info("[{}]: Dynamically created {} zigBeeConverterEndpoints {}", entityID, entity.getEndpoints().size(), nodeIeeeAddress);
  }

  private void createMissingRequireEndpointClusters() {
    for (EndpointDefinition ed : ZigBeeDefineEndpoints.getEndpointDefinitions(entity.getModelIdentifier())) {
      ZigBeeEndpointEntity foundEndpoint = entity.findEndpoint(ed.getInputCluster(), ed.getEndpoint());
      if (foundEndpoint == null) {
        log.info("[{}]: Add zigbee node <{}> missed require endpoint: <{}>", entityID, nodeIeeeAddress, ed);
        Collection<ZigBeeBaseChannelConverter> endpoints = zigBeeChannelConverterFactory.createConverterEndpoint(ed);
        createEndpoints(ed.getEndpoint(), endpoints, ed.getMetadata());
      }
    }
  }

  public void createEndpoints(int endpointId, Collection<ZigBeeBaseChannelConverter> clusters, JsonNode metadata) {
    ZigBeeDeviceService zigBeeDeviceService = entity.getService();

    String ieeeAddressStr = nodeIeeeAddress.toString();
    for (ZigBeeBaseChannelConverter cluster : clusters) {
      int clientCluster = cluster.getAnnotation().clientCluster();
      ZigBeeEndpointEntity endpointEntity = entity.findEndpoint(clientCluster, endpointId);

      if (endpointEntity == null) {
        String endpointName = cluster.getAnnotation().name().substring("zigbee:".length());
        endpointEntity = new ZigBeeEndpointEntity()
            .setEntityID(ieeeAddressStr + "_" + clientCluster + "_" + endpointId)
            .setIeeeAddress(ieeeAddressStr)
            .setClusterId(clientCluster);
        endpointEntity.setName(Lang.getServerMessageOptional("endpoint.name." + endpointName).orElse(endpointName));
        endpointEntity.setDescription(Lang.getServerMessageOptional("endpoint.description." + endpointName).orElse(""));
        endpointEntity.setAddress(endpointId);
        endpointEntity.setOwner(entity);

        endpointEntity.setPollingPeriod(cluster.getPollingPeriod());
        endpointEntity = entityContext.save(endpointEntity);
      }

      endpointEntity.setStatus(Status.WAITING, null);
      var endpointService = new ZigbeeEndpointService(entityContext, cluster, zigBeeDeviceService, endpointEntity, ieeeAddressStr, metadata);
      EntityService.entityToService.put(endpointEntity.getEntityID(), endpointService);
    }
  }

  private void initializeZigBeeChannelConverters() {
    for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
      try {
        if (endpoint.optService().isEmpty()) {
          log.info("[{}]: Endpoint not found in zigbee network {}", entityID, endpoint);
          throw new RuntimeException("Endpoint not found");
        }
        endpoint.setStatus(Status.INITIALIZE);
        ZigBeeBaseChannelConverter cluster = endpoint.getService().getCluster();

        if (!cluster.initializeDevice()) {
          log.info("[{}]: failed to initialize converter <{}>", entityID, endpoint);
          throw new RuntimeException("Failed to configure device");
        }

        if (!cluster.initializeConverter()) {
          log.info("[{}]: Failed to initialize converter {}", entityID, endpoint);
          throw new RuntimeException("Failed to initialize converter");
        }

        cluster.updateConfiguration();

        if (cluster.getEntity().isOutdated()) {
          log.info("Endpoint had been updated during cluster configuration");
          entityContext.save(cluster.getEntity());
        }

        if (cluster.getPollingPeriod() < pollingPeriod) {
          pollingPeriod = cluster.getPollingPeriod();
        }
        endpoint.setStatusOnline();
      } catch (Exception ex) {
        endpoint.setStatusError(ex);
      }
    }
    log.debug("[{}]: Channel initialisation complete {}", entityID, nodeIeeeAddress);
  }

  private int getExpectedUpdatePeriod() {
    int minInterval = Integer.MAX_VALUE;
    for (ZigBeeEndpointEntity endpoint : this.entity.getEndpoints()) {
      minInterval = Math.min(minInterval, endpoint.getService().getCluster().getMinPoolingInterval());
    }
    return minInterval;
  }

  public void aliveTimeoutReached() {
    entity.setStatus(Status.OFFLINE, "zigbee.error.alive_timeout_reached");
  }

  public void dispose() {
    log.debug("[{}]: Handler dispose {}", entityID, nodeIeeeAddress);

    stopPolling();

    if (nodeIeeeAddress != null) {
      if (this.coordinatorService != null) {
        this.coordinatorService.removeNetworkNodeListener(this);
        this.coordinatorService.removeAnnounceListener(this);
      }
    }

    for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
      endpoint.getService().getCluster().disposeConverter();
      endpoint.setStatus(Status.OFFLINE, "Dispose");
    }

    entity.setStatus(Status.OFFLINE, null);
    zigBeeIsAliveTracker.removeHandler(this);
  }

  private void stopPolling() {
    synchronized (pollingSync) {
      if (pollingJob != null) {
        pollingJob.cancel();
        pollingJob = null;
        log.debug("[{}]: Polling stopped {}", entityID, nodeIeeeAddress);
      }
    }
  }

  /**
   * Start polling channel updates
   */
  private void startPolling() {
    synchronized (pollingSync) {
      stopPolling();
      pollingPeriod = Math.max(pollingPeriod, 5);
      pollingPeriod = Math.min(pollingPeriod, 86400);

      // Polling starts almost immediately to get an immediate refresh
      // Add some random element to the period so that all things aren't synchronised
      int pollingPeriodMs = pollingPeriod * 1000 + new Random().nextInt(pollingPeriod * 100);
      pollingJob = entityContext.bgp().builder("zigbee-pooling-job-" + nodeIeeeAddress)
          .delay(Duration.ofMillis(new Random().nextInt(pollingPeriodMs)))
          .interval(Duration.ofMillis(pollingPeriodMs))
          .execute(this::pullChannels);
      log.debug("[{}]: Polling initialized at {}ms. {}", entityID, pollingPeriodMs, nodeIeeeAddress);
    }
  }

  public void pullChannels() {
    try {
      log.info("[{}]: Polling started for node {}", entityID, nodeIeeeAddress);

      for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
        if (endpoint.getStatus() == Status.ONLINE) {
          log.debug("[{}]: Polling {}", entityID, endpoint);
          ZigBeeBaseChannelConverter converter = endpoint.getService().getCluster();
          if (converter == null) {
            log.debug("[{}]: Polling aborted as no converter found for {}", entityID, endpoint);
          } else {
            converter.fireHandleRefresh();
          }
        }
      }

      log.info("[{}]: Polling done for node {}", entityID, nodeIeeeAddress);
    } catch (Exception e) {
      log.warn("[{}]: Polling aborted due to exception for node {}", entityID, nodeIeeeAddress, e);
    }
  }

  @Override
  public void deviceStatusUpdate(ZigBeeNodeStatus deviceStatus, Integer networkAddress, IeeeAddress ieeeAddress) {
    // A node has joined - or come back online
    if (!nodeIeeeAddress.equals(ieeeAddress)) {
      return;
    }
    // Use this to update channel information - e.g. bulb state will likely change when the device was powered off/on.
    startPolling();
  }

  @Override
  public void nodeAdded(ZigBeeNode node) {
    nodeUpdated(node);
  }

  @Override
  public void nodeUpdated(ZigBeeNode node) {
    // Make sure it's this node that's updated
    if (!node.getIeeeAddress().equals(nodeIeeeAddress)) {
      return;
    }
    log.debug("[{}]: Node {} has been updated. Fire initialization...", entityID, nodeIeeeAddress);
    initializeZigBeeNode();
  }

  @Override
  public void nodeRemoved(ZigBeeNode node) {
    if (!node.getIeeeAddress().equals(nodeIeeeAddress)) {
      return;
    }
    entity.setStatus(Status.OFFLINE, "zigbee.error.removed_by_dongle");
  }

  public void updateNodeDescription() {
    ZigBeeNode node = this.coordinatorService.getNode(nodeIeeeAddress);
    if (node == null) {
      throw new IllegalStateException("Unable to find node: <" + nodeIeeeAddress + ">");
    }
    updateNodeDescription(node);
  }

  @SneakyThrows
  private void updateNodeDescription(ZigBeeNode node) {
    if (nodeDiscoveryThreadContext != null && !nodeDiscoveryThreadContext.isStopped()) {
      return;
    }
    this.nodeDiscoveryThreadContext = entityContext.bgp().builder("discover-node-" + node.getIeeeAddress())
        .execute(() -> entity.updateFromNode(node, entityContext));
  }

  @Override
  public boolean entityUpdated(@NotNull ZigBeeDeviceEntity entity) {
    this.entity = entity;
    return false;
  }

  @Override
  public void destroy() {
    this.dispose();
  }

  @Override
  public boolean testService() {
    return false;
  }
}
