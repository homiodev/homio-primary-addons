package org.touchhome.bundle.zigbee.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeAnnounceListener;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.ZigBeeNode.ZigBeeNodeState;
import com.zsmartsystems.zigbee.ZigBeeNodeStatus;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.ZigBeeStatus;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP.ThreadContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.util.ClusterAttributeConfiguration;
import org.touchhome.bundle.zigbee.util.DeviceConfiguration;
import org.touchhome.bundle.zigbee.util.DeviceConfiguration.EndpointDefinition;
import org.touchhome.bundle.zigbee.util.DeviceConfigurations;
import org.touchhome.common.util.Lang;

@Getter
@Log4j2
public class ZigBeeDeviceService implements ZigBeeNetworkNodeListener, ZigBeeAnnounceListener, ServiceInstance<ZigBeeDeviceEntity> {

  private final Set<String> DEVICE_INITIALIZED = new HashSet<>();

  private final Object entityUpdateSync = new Object();
  private final Object pollingSync = new Object();
  private final Object initializeSync = new Object();

  private final IeeeAddress nodeIeeeAddress;
  private final ZigBeeCoordinatorService coordinatorService;
  private final EntityContext entityContext;
  private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
  private final String entityID;
  @Getter
  private ThreadContext<Void> pollingJob = null;
  private int pollingPeriod = 1800;
  private ZigBeeDeviceEntity entity;
  private Integer expectedUpdateInterval;
  private int initializeZigBeeNodeRequests = 0;
  @Getter
  private double progress = -1;

  public ZigBeeDeviceService(ZigBeeCoordinatorService coordinatorService, IeeeAddress nodeIeeeAddress,
      EntityContext entityContext, ZigBeeDeviceEntity entity) {
    this.entityID = entity.getEntityID();
    log.info("[{}]: Creating zigBee device {}", entityID, nodeIeeeAddress);
    this.entity = entity;
    this.entityContext = entityContext;

    this.coordinatorService = coordinatorService;
    // this.zigBeeIsAliveTracker = coordinatorService.getDiscoveryService().getZigBeeIsAliveTracker();
    this.zigBeeChannelConverterFactory = coordinatorService.getDiscoveryService().getChannelFactory();

    this.nodeIeeeAddress = nodeIeeeAddress;
    this.coordinatorService.addNetworkNodeListener(this);
    this.coordinatorService.addAnnounceListener(this);

    if (this.coordinatorService.getEntity().getStatus().isOnline()) {
      this.coordinatorOnline();
    } else {
      this.coordinatorOffline();
    }

    // register listener for reset timer if any updates from any endpoint
    entityContext.event().addEventListener(this.nodeIeeeAddress.toString(), state -> {
      entity.setLastAnswerFromEndpoints(System.currentTimeMillis());
    });
  }

  public void tryInitializeZigBeeNode() {
    if (coordinatorService.getEntity().getStatus() == Status.ONLINE) {
      log.info("[{}]: Coordinator is ONLINE. Starting device initialisation. {}", entityID, nodeIeeeAddress);

      this.coordinatorService.rediscoverNode(nodeIeeeAddress);

      initializeZigBeeNode();
    } else {
      log.warn("[{}]: Unable to initialize device. Coordinator in '{}' state",
          entityID, coordinatorService.getEntity().getStatus());
    }
  }

  /**
   * synchronized to handle from multiple threads
   */
  public void initializeZigBeeNode() {
    synchronized (initializeSync) {
      Status initStatus = entity.getNodeInitializationStatus();
      if (initStatus == Status.UNKNOWN || initStatus == Status.ERROR) {
        this.initializeZigBeeNodeRequests = 0;
        log.debug("[{}]: Request initialize zigbee node {}", entityID, nodeIeeeAddress);
        entity.setStatus(Status.WAITING, null);
        entity.setNodeInitializationStatus(Status.WAITING);
        entityContext.bgp().builder("zigbee-node-init-" + this.nodeIeeeAddress)
            .delay(Duration.ofMillis(10)).execute(() -> {
              try {
                doNodeInitialisation();
              } catch (Exception ex) {
                log.error("[{}]: Unknown error during node initialization", entityID, ex);
              } finally {
                if (this.initializeZigBeeNodeRequests > 0) {
                  this.initializeZigBeeNodeRequests = 0;
                  reInitializeZigBeeNode();
                }
              }
            });
      } else {
        log.info("[{}]: Node {} initialization already started: {}", entityID, nodeIeeeAddress, initStatus);
        this.initializeZigBeeNodeRequests++;
      }
    }
  }

  /**
   * Maybe fired after initializeZigBeeNode() in case if some nodes had been updated during main node initialization process
   */
  private void reInitializeZigBeeNode() {
    if (!entity.getStatus().isOnline()) { // if previous initialization was failed
      log.warn("Request re-initialize zigbee node entity due status: {}", entity.getStatus());
      initializeZigBeeNode();
    } else {
      // just updated part of entity
      ZigBeeNode node = this.coordinatorService.getNode(nodeIeeeAddress);
      String modelIdentifier = entity.getModelIdentifier();
      entity.updateFromNode(node, entityContext);
      // create missing endpoints if model identifier fetched only in N-th nodeUpdated(...)
      // TODO: not tested!!! because looks like new endpoints not initialized?
      if (entity.getModelIdentifier() != null && !entity.getModelIdentifier().equals(modelIdentifier)) {
        log.warn("[{}]: Model identifier fetched after next initialize", entityID);
        createMissingEndpointInZigBeeNetwork(node);
      }
    }
  }

  private void doNodeInitialisation() {
    progress = 0;
    log.info("[{}]: Initialization zigBee device {}", entityID, nodeIeeeAddress);
    if (this.entity.getNodeInitializationStatus() != Status.WAITING) {
      log.error("[{}]: Something went wrong for node initialization workflow {}", entityID, nodeIeeeAddress);
    }
    this.entity.setStatus(Status.INITIALIZE, null);
    this.entity.setNodeInitializationStatus(Status.INITIALIZE);
    try {
      pollingPeriod = 86400;
      ZigBeeNode node = this.coordinatorService.getNode(nodeIeeeAddress);
      if (node == null) {
        log.debug("[{}]: Node not found {}", entityID, nodeIeeeAddress);
        throw new RuntimeException("zigbee.error.offline_node_not_found");
      }

      // Check if discovery is complete, and we know all the services the node supports
      if (!node.isDiscovered()) {
        log.warn("[{}]: Node has not finished discovery {}", entityID, nodeIeeeAddress);
        entity.setNodeInitializationStatus(Status.UNKNOWN);
        entity.setStatus(Status.NOT_READY);
        return;
      }

      log.info("[{}]: Start initialising ZigBee channels {}", entityID, nodeIeeeAddress);
      log.info("[{}]: Initial endpoints: {}", entityID, node.getEndpoints().stream().map(ZigBeeEndpoint::toString)
          .collect(Collectors.joining("\n")));

      entity.updateFromNode(node, entityContext);
      addToProgress(5);

      if (entity.getModelIdentifier() != null) {
        createMissingEndpointInZigBeeNetwork(node);
      }

      createDynamicEndpoints();
      // Progress = 50

      tryInitializeDevice(entity.getEndpoints());
      // Progress = 70

      entity.setStatus(Status.WAITING, null);
      final double initChannelDelta = 30D / entity.getEndpoints().size();
      initializeZigBeeChannelConverters(entity.getEndpoints(), () -> {
        addToProgress(initChannelDelta);
      });

      // If this is an RFD then we reduce polling to the max to avoid wasting battery
      if (node.isReducedFunctionDevice()) {
        pollingPeriod = 1800;
        log.debug("[{}]: {}: Thing is RFD, using long poll period of {}sec", entityID, nodeIeeeAddress, pollingPeriod);
      }

      int expectedUpdatePeriod = getExpectedUpdatePeriod();
      expectedUpdateInterval = (expectedUpdatePeriod * 2) + 30;
      log.debug("[{}]: Setting ONLINE/OFFLINE {} timeout interval to: {}sec.", entityID, nodeIeeeAddress, expectedUpdateInterval);
      entity.setLastAnswerFromEndpoints(System.currentTimeMillis());
      coordinatorService.getRegisteredDevices().add(this);

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
      entity.setNodeInitializationStatus(Status.DONE);
      log.debug("[{}]: Done initialising ZigBee device {}", entityID, nodeIeeeAddress);

      // Save the network state
      coordinatorService.serializeNetwork(node.getIeeeAddress());
    } catch (Exception ex) {
      entity.setStatusError(ex);
      this.entity.setNodeInitializationStatus(Status.UNKNOWN);
    } finally {
      addToProgress(100); // reset progress
    }
  }

  private void addToProgress(double value) {
    progress += value;
    if (progress > 100) {
      progress = -1;
    }
    entityContext.ui().updateItem(getEntity(), "initProgress", entity.getInitProgress());
  }

  private void createDynamicEndpoints() {
    // Dynamically create the zigBeeConverterEndpoints from the device
    // Process all the endpoints for this device and add all zigBeeConverterEndpoints as derived from the supported clusters
    log.info("[{}]: Try out to find zigbee endpoints {}", entityID, nodeIeeeAddress);
    Collection<ZigBeeEndpoint> nodeEndpoints = this.coordinatorService.getNodeEndpoints(nodeIeeeAddress);
    double delta = 45D / (nodeEndpoints.size() * zigBeeChannelConverterFactory.getConverterCount());
    for (ZigBeeEndpoint endpoint : nodeEndpoints) {
      Collection<ZigBeeBaseChannelConverter> matchConverters = zigBeeChannelConverterFactory.
          findAllMatchConverters(endpoint, entityID, entityContext, () -> addToProgress(delta));
      createEndpoints(endpoint.getEndpointId(), matchConverters);
    }
    log.info("[{}]: Dynamically created {} zigBeeConverterEndpoints {}", entityID, entity.getEndpoints().size(), nodeIeeeAddress);
  }

  private void createMissingEndpointInZigBeeNetwork(ZigBeeNode node) {
    DeviceConfigurations.getDeviceDefinition(entity.getModelIdentifier()).ifPresent(dd -> {
      log.info("[{}]: Found device '{}' definition", entityID, entity.getModelIdentifier());
      for (EndpointDefinition ed : dd.getEndpoints()) {
        ZigBeeEndpoint endpoint = node.getEndpoint(ed.getEndpoint());
        if (endpoint == null) {
          int profileId = ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION.getKey();
          log.debug("[{}]: Creating statically defined device {} endpoint {} with profile {}", entityID, nodeIeeeAddress,
              ed.getEndpoint(), ZigBeeProfileType.getByValue(profileId));
          endpoint = new ZigBeeEndpoint(node, ed.getEndpoint());
          endpoint.setProfileId(profileId);
          node.addEndpoint(endpoint);
        }

        // add input clusters if found any
        List<Integer> inputClusters = processClusters(endpoint.getInputClusterIds(), ed.getInputClusters());
        if (!inputClusters.isEmpty()) {
          endpoint.setInputClusterIds(inputClusters);
          node.updateEndpoint(endpoint);
        }
      }
    });
  }

  public void createEndpoints(int endpointId, Collection<ZigBeeBaseChannelConverter> matchConverters) {
    String ieeeAddressStr = nodeIeeeAddress.toString();
    for (ZigBeeBaseChannelConverter cluster : matchConverters) {
      int clientCluster = cluster.getAnnotation().clientCluster();
      ZigBeeEndpointEntity endpointEntity = entity.findEndpoint(endpointId);

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

        cluster.configureNewEndpointEntity(endpointEntity);
        endpointEntity = entityContext.save(endpointEntity);
      }

      if (!EntityService.entityToService.containsKey(endpointEntity.getEntityID())) {
        endpointEntity.setStatus(Status.WAITING, null);
        Optional<DeviceConfiguration> deviceDefinition = DeviceConfigurations.getDeviceDefinition(entity.getModelIdentifier());
        JsonNode metadata = deviceDefinition.map(dd -> {
          for (EndpointDefinition endpoint : dd.getEndpoints()) {
            if (endpoint.getEndpoint() == endpointId) {
              return endpoint.getMetadata();
            }
          }
          return null;
        }).orElse(null);
        var endpointService = new ZigbeeEndpointService(entityContext, cluster, this, endpointEntity, ieeeAddressStr, metadata);
        EntityService.entityToService.put(endpointEntity.getEntityID(), endpointService);
      }
    }
  }

  private void initializeZigBeeChannelConverters(Collection<ZigBeeEndpointEntity> endpoints, Runnable runUnit) {
    for (ZigBeeEndpointEntity endpoint : endpoints) {
      try {
        endpoint.setStatus(Status.INITIALIZE);
        ZigBeeBaseChannelConverter cluster = endpoint.getService().getCluster();

        try {
          cluster.initializeConverter();
        } catch (Exception ex) {
          log.info("[{}]: Failed to initialize converter {}", entityID, endpoint);
          continue;
        }

        if (cluster.getPollingPeriod() < pollingPeriod) {
          pollingPeriod = cluster.getPollingPeriod();
        }

        cluster.fireHandleRefresh();

        endpoint.setStatusOnline();
      } catch (Exception ex) {
        endpoint.setStatusError(ex);
      } finally {
        runUnit.run();
      }
    }
    log.debug("[{}]: Channel initialisation complete {}", entityID, nodeIeeeAddress);
  }

  private void tryInitializeDevice(Collection<ZigBeeEndpointEntity> endpoints) {
    if (DEVICE_INITIALIZED.add(entity.getIeeeAddress())) {
      double initDeviceDelta = 20D / entity.getEndpoints().size();
      if (!tryInitializeDeviceEndpoints(endpoints, () -> addToProgress(initDeviceDelta))) {
        DEVICE_INITIALIZED.remove(entity.getIeeeAddress());
      }
    } else {
      addToProgress(20D);
      log.debug("[{}]: Device {} initialization will be skipped as the device is already initialized",
          entityID, nodeIeeeAddress);
    }
  }

  private boolean tryInitializeDeviceEndpoints(Collection<ZigBeeEndpointEntity> endpoints, Runnable runUnit) {
    for (ZigBeeEndpointEntity endpoint : endpoints) {
      ZigBeeBaseChannelConverter cluster = endpoint.getService().getCluster();
      endpoint.setDeviceInitializeStatus(Status.RUNNING);
      try {
        cluster.initializeDevice();
        endpoint.setDeviceInitializeStatus(Status.DONE);
        runUnit.run();
      } catch (Exception ex) {
        endpoint.setDeviceInitializeStatus(Status.ERROR);
        log.error("[{}]: failed to initialize device <{}>", entityID, endpoint);
        return false;
      }
    }
    return true;
  }

  private int getExpectedUpdatePeriod() {
    int minInterval = Integer.MAX_VALUE;
    for (ZigBeeEndpointEntity endpoint : this.entity.getEndpoints()) {
      minInterval = Math.min(minInterval, endpoint.getService().getCluster().getMinPoolingInterval());
    }
    return minInterval;
  }

  public void dispose() {
    log.debug("[{}]: Handler dispose {}", entityID, nodeIeeeAddress);

    stopPolling();

    if (nodeIeeeAddress != null) {
      if (this.coordinatorService != null) {
        this.coordinatorService.dispose(this);
      }
    }

    for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
      endpoint.getService().getCluster().disposeConverter();
      endpoint.setStatus(Status.OFFLINE, "Dispose");
    }

    entity.setStatus(Status.OFFLINE, null);
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
        log.debug("[{}]: Polling {}", entityID, endpoint);
        try {
          endpoint.getService().getCluster().fireHandleRefresh();
        } catch (Exception ex) {
          log.error("[{}]: Error handleRefresh endpoint {}", entityID, endpoint);
        }
      }

      log.info("[{}]: Polling done for node {}", entityID, nodeIeeeAddress);
    } catch (Exception e) {
      log.error("[{}]: Polling aborted due to exception for node {}", entityID, nodeIeeeAddress, e);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ZigBeeDeviceService service = (ZigBeeDeviceService) o;

    return entityID.equals(service.entityID);
  }

  @Override
  public int hashCode() {
    return entityID.hashCode();
  }

  public void checkOffline() {
    if ((System.currentTimeMillis() - entity.getLastAnswerFromEndpoints()) / 1000 > expectedUpdateInterval) {
      log.warn("[{}]: Timeout has been reached for zigBeeDevice {}. No answer from endpoints during {}sec.",
          entityID, nodeIeeeAddress, expectedUpdateInterval);
      entity.setNodeStatus(ZigBeeNodeState.UNKNOWN);
      entity.setStatus(Status.OFFLINE, "zigbee.error.alive_timeout_reached");
    }
  }

  public void coordinatorOffline() {
    this.entity.setStatus(Status.OFFLINE);
    this.entity.setNodeInitializationStatus(Status.UNKNOWN);
    stopPolling();
  }

  public void coordinatorOnline() {
    log.info("[{}]: Fire discovery node: {}", entityID, nodeIeeeAddress);
    getCoordinatorService().rediscoverNode(nodeIeeeAddress);
    initializeZigBeeNode();
  }

  private List<Integer> processClusters(Collection<Integer> initialClusters, Set<Integer> newClusters) {
    if (newClusters == null || newClusters.size() == 0) {
      return Collections.emptyList();
    }

    Set<Integer> clusters = new HashSet<>();
    clusters.addAll(initialClusters);
    clusters.addAll(newClusters);
    if (clusters.size() == initialClusters.size()) {
      return Collections.emptyList();
    }
    return new ArrayList<>(clusters);
  }
}
