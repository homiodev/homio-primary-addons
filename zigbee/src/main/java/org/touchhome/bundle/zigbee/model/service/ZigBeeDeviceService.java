package org.touchhome.bundle.zigbee.model.service;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeAnnounceListener;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeNetworkNodeListener;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.ZigBeeNode.ZigBeeNodeState;
import com.zsmartsystems.zigbee.ZigBeeNodeStatus;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.ZigBeeStatus;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP.ThreadContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.cluster.ZigBeeGeneralApplication;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.util.ClusterConfiguration;
import org.touchhome.bundle.zigbee.util.ClusterConfigurations;
import org.touchhome.bundle.zigbee.util.DeviceDefinition.EndpointDefinition;
import org.touchhome.bundle.zigbee.util.ZigBeeDefineEndpoints;

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
  // private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
  private final String entityID;
  private ThreadContext<Void> pollingJob = null;
  private ZigBeeDeviceEntity entity;
  private Integer expectedUpdateInterval;
  private int initializeZigBeeNodeRequests = 0;
  private double progress = -1;

  public ZigBeeDeviceService(ZigBeeCoordinatorService coordinatorService, IeeeAddress nodeIeeeAddress,
      EntityContext entityContext, ZigBeeDeviceEntity entity) {
    this.entityID = entity.getEntityID();
    log.info("[{}]: Creating zigBee device {}", entityID, nodeIeeeAddress);
    this.entity = entity;
    this.entityContext = entityContext;
    this.coordinatorService = coordinatorService;
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
      if (entity.getModelIdentifier() != null && !entity.getModelIdentifier().equals(modelIdentifier)) {
        Map<Integer, ZigBeeEndpoint> endpoints = getEndpoints().stream().collect(Collectors.toMap(ZigBeeEndpoint::getEndpointId, e -> e));
        createMissingRequireEndpointClusters(endpoints, node);
      }
      // create missing applications
      Map<Integer, ZigBeeEndpoint> endpoints = getEndpoints().stream().collect(Collectors.toMap(ZigBeeEndpoint::getEndpointId, e -> e));
      Map<String, Callable<Void>> tasks = buildApplications(endpoints);
      if (!tasks.isEmpty()) {
        entityContext.bgp().runInBatchAndGet("init-device-clusters-" + entityID, Duration.ofMinutes(10), tasks.size(),
            tasks, progress -> {
            });
      }
    }
  }

  private void doNodeInitialisation() {
    progress = 0;
    log.info("[{}]: Initialization zigBee device {}", entityID, nodeIeeeAddress);
    if (entity.getStatus() != Status.WAITING) {
      log.error("[{}]: Something went wrong for node initialization workflow {}", entityID, nodeIeeeAddress);
    }
    entity.setStatus(Status.INITIALIZE, null);
    entity.setNodeInitializationStatus(Status.INITIALIZE);
    try {
      ZigBeeNode node = coordinatorService.getNode(nodeIeeeAddress);
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
      setProgress(10);

      entity.setStatus(Status.WAITING, null);

      Map<Integer, ZigBeeEndpoint> endpoints = getEndpoints().stream().collect(Collectors.toMap(ZigBeeEndpoint::getEndpointId, e -> e));
      createDynamicEndpoints(endpoints);

      if (entity.getModelIdentifier() != null) {
        createMissingRequireEndpointClusters(endpoints, node);
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
      entity.setNodeInitializationStatus(Status.ERROR);
    } finally {
      setProgress(100); // reset progress
    }
  }

  public Collection<ZigBeeEndpoint> getEndpoints() {
    return this.coordinatorService.getNodeEndpoints(nodeIeeeAddress);
  }

  // keep endpoint - map<clusterID - applications>
  private final Map<Integer, Map<Integer, ZigBeeGeneralApplication>> endpointToApplications = new HashMap<>();

  private void createDynamicEndpoints(Map<Integer, ZigBeeEndpoint> endpoints) {
    // Dynamically create the zigBeeConverterEndpoints from the device
    // Process all the endpoints for this device and add all zigBeeConverterEndpoints as derived from the supported clusters
    log.info("[{}]: Try out to find zigbee endpoints {}", entityID, nodeIeeeAddress);
    Map<String, Callable<Void>> tasks = buildApplications(endpoints);

    if (!tasks.isEmpty()) {
      entityContext.bgp().runInBatchAndGet("init-device-clusters-" + entityID, Duration.ofMinutes(10), tasks.size(),
          tasks, progress -> setProgress(10 + progress * 87));
    }
    log.info("[{}]: Dynamically created {} applications for node {}", entityID, tasks.size(), nodeIeeeAddress);
  }

  private Map<String, Callable<Void>> buildApplications(Map<Integer, ZigBeeEndpoint> endpoints) {
    Map<String, Callable<Void>> tasks = new HashMap<>();
    for (ZigBeeEndpoint endpoint : endpoints.values()) {
      Map<Integer, ZigBeeGeneralApplication> clusterApplications = endpointToApplications.computeIfAbsent(endpoint.getEndpointId(), ep -> new HashMap<>());
      for (ZclClusterType zclClusterType : ZclClusterType.values()) {
        if (!clusterApplications.containsKey(zclClusterType.getId())) {
          ClusterConfiguration clusterConfiguration = ClusterConfigurations.getClusterConfigurations().get(zclClusterType.getId());
          if (clusterConfiguration != null && clusterConfiguration.acceptEndpoint(endpoint)) {
            ZigBeeGeneralApplication application = clusterConfiguration.newZigBeeApplicationInstance(endpoint, this);
            clusterApplications.put(zclClusterType.getId(), application);
            tasks.put("init-app-" + application.getEntityID(), () -> {
              application.appStartup();
              return null;
            });
          }
        }
      }
    }
    return tasks;
  }

  private void createMissingRequireEndpointClusters(Map<Integer, ZigBeeEndpoint> endpoints, ZigBeeNode node) {
    for (EndpointDefinition ed : ZigBeeDefineEndpoints.getEndpointDefinitions(entity.getModelIdentifier())) {

      ZigBeeEndpoint zigBeeEndpoint = endpoints.get(ed.getEndpoint());
      boolean endpointCreated = false;
      if (zigBeeEndpoint == null) {
        int profileId = ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION.getKey();
        log.info("[{}]: Creating statically defined device {} endpoint {} with profile {}", entityID, nodeIeeeAddress,
            ed.getEndpoint(), ZigBeeProfileType.getByValue(profileId));
        zigBeeEndpoint = new ZigBeeEndpoint(node, ed.getEndpoint());
        zigBeeEndpoint.setProfileId(profileId);
        endpointCreated = true;
      }

      List<Integer> inputClusters = processClusters(zigBeeEndpoint.getInputClusterIds(), ed.getInputClusters());
      List<Integer> outputClusters = processClusters(zigBeeEndpoint.getOutputClusterIds(), ed.getOutputClusters());
      if (!inputClusters.isEmpty() || !outputClusters.isEmpty()) {
        if (!inputClusters.isEmpty()) {
          zigBeeEndpoint.setInputClusterIds(inputClusters);
        }
        if (!outputClusters.isEmpty()) {
          zigBeeEndpoint.setOutputClusterIds(outputClusters);
        }
        if (endpointCreated) {
          log.debug("[{}]: Added new endpoint: {}", entityID, zigBeeEndpoint);
          node.addEndpoint(zigBeeEndpoint);
        } else {
          log.debug("[{}]: Updated endpoint: {}", entityID, zigBeeEndpoint);
          node.updateEndpoint(zigBeeEndpoint);
        }
      }
    }
  }

  public List<ZigBeeGeneralApplication> getZigBeeApplications() {
    List<ZigBeeGeneralApplication> applications = new ArrayList<>();
    for (Map<Integer, ZigBeeGeneralApplication> yetMap : endpointToApplications.values()) {
      applications.addAll(yetMap.values());
    }
    return applications;
  }

  private int getExpectedUpdatePeriod() {
    Set<Integer> intervals = new HashSet<>();
    for (ZigBeeGeneralApplication zigBeeApplication : getZigBeeApplications()) {
      for (AttributeHandler attributeHandler : zigBeeApplication.getAttributes().values()) {
        intervals.add(attributeHandler.getPollingPeriod());
        intervals.add(attributeHandler.getMinimalReportingPeriod());
      }
    }
    return Collections.min(intervals);
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
      pollingJob = entityContext.bgp().builder("zigbee-pooling-job-" + nodeIeeeAddress)
          .interval(Duration.ofSeconds(60)).execute(() -> pullChannels(false));
    }
  }

  public void pullChannels(boolean force) {
    try {
      log.info("[{}]: Polling started for node {}", entityID, nodeIeeeAddress);
      for (ZigBeeGeneralApplication zigBeeApplication : getZigBeeApplications()) {
        try {
          zigBeeApplication.handleRefresh(force);
        } catch (Exception ex) {
          log.error("[{}]: Error handleRefresh app {} for node {}", entityID, zigBeeApplication, nodeIeeeAddress);
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
    log.debug("[{}]: Handler dispose {}", entityID, nodeIeeeAddress);
    stopPolling();
    if (nodeIeeeAddress != null) {
      this.coordinatorService.dispose(this);
    }
    entity.setStatus(Status.OFFLINE, null);
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

  private void setProgress(double value) {
    progress = value;
    if (progress >= 100) {
      progress = -1;
    }
    entityContext.ui().updateItem(getEntity(), "initProgress", entity.getInitProgress());
  }

  private List<Integer> processClusters(Collection<Integer> initialClusters, List<Integer> newClusters) {
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
}
