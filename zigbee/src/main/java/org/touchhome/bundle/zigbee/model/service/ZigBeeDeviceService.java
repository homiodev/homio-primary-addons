package org.touchhome.bundle.zigbee.model.service;

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
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP.ThreadContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.zigbee.ZigBeeIsAliveTracker;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition.EndpointDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeDefineEndpoints;

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

  private ThreadContext<Void> pollingJob = null;
  private int pollingPeriod = 86400;
  private ThreadContext<Void> nodeDiscoveryThreadContext;
  private ZigBeeDeviceEntity entity;

  @Setter
  private Integer expectedUpdateInterval;

  @Setter
  private Long expectedUpdateIntervalTimer;

  public ZigBeeDeviceService(ZigBeeCoordinatorService coordinatorService, IeeeAddress nodeIeeeAddress, EntityContext entityContext) {
    log.info("{}: Creating zigBee device", nodeIeeeAddress);
    this.entityContext = entityContext;

    this.coordinatorService = coordinatorService;
    this.zigBeeIsAliveTracker = coordinatorService.getDiscoveryService().getZigBeeIsAliveTracker();
    this.zigBeeChannelConverterFactory = coordinatorService.getDiscoveryService().getChannelFactory();

    this.nodeIeeeAddress = nodeIeeeAddress;
    this.coordinatorService.addNetworkNodeListener(this);
    this.coordinatorService.addAnnounceListener(this);

    tryInitializeDevice();

    // register listener for reset timer if any updates from any endpoint
    entityContext.event().addEventListener(this.nodeIeeeAddress.toString(), state -> {
      zigBeeIsAliveTracker.resetTimer(this);
      entity.setStatusOnline();
    });
  }

  public void tryInitializeDevice() {
    if (coordinatorService.getEntity().getStatus() != Status.ONLINE) {
      log.trace("{}: Coordinator is unknown or not online.", nodeIeeeAddress);
      entity.setStatus(Status.OFFLINE, "Coordinator unknown status");
      stopPolling();
    } else if (!entity.getStatus().isOnline() && isInitializeFinished()) {
      log.debug("{}: Coordinator is ONLINE. Starting device initialisation.", nodeIeeeAddress);
      this.coordinatorService.rediscoverNode(nodeIeeeAddress);
      initialiseZigBeeNode();
    }
  }

  public void initialiseZigBeeNode() {
    if (!isInitializeFinished()) {
      throw new IllegalStateException("Node <" + nodeIeeeAddress + "> initialization already started");
    }
    entity.setStatus(Status.WAITING);
    entity.setNodeInitializationStatus(Status.WAITING);
    entityContext.bgp().builder("zigbee-node-init-" + this.nodeIeeeAddress)
        .delay(Duration.ofMillis(10)).execute(this::doNodeInitialisation);
  }

  private synchronized void doNodeInitialisation() {
    this.entity.setStatus(Status.RUNNING);
    this.entity.setNodeInitializationStatus(Status.RUNNING);
    try {
      log.info("{}: Initialize zigBee device", nodeIeeeAddress);
      ZigBeeNode node = this.coordinatorService.getNode(nodeIeeeAddress);
      if (node == null) {
        log.debug("{}: Node not found", nodeIeeeAddress);
        entity.setStatus(Status.OFFLINE, "zigbee.error.offline_node_not_found");
        return;
      }

      // Check if discovery is complete, and we know all the services the node supports
      if (!node.isDiscovered()) {
        log.debug("{}: Node has not finished discovery", nodeIeeeAddress);
        entity.setStatus(Status.OFFLINE, "zigbee.error.offline_discovery_incomplete");
        return;
      }

      log.debug("{}: Start initialising ZigBee channels", nodeIeeeAddress);

      for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
        endpoint.setStatus(Status.OFFLINE, "Uninitialised");
      }

      updateNodeDescription(node);

      // for remove old one if exists
      entityContext.event().removeEntityUpdateListener(this.entity.getEntityID(), "zigbee-change-listener");

      createDynamicEndpoints();

      if (entity.getModelIdentifier() != null) {
        createMissingRequireEndpointClusters();
      }

      // Create missing endpoints in zsmartsystem.node
      for (ZigBeeEndpointEntity zigBeeEndpointEntity : entity.getEndpoints()) {
        ZigBeeEndpoint endpoint = node.getEndpoint(zigBeeEndpointEntity.getEndpointId());
        if (endpoint == null) {
          int profileId = ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION.getKey();
          log.debug("{}: Creating statically defined device endpoint {} with profile {}", nodeIeeeAddress,
              zigBeeEndpointEntity.getEndpointId(), ZigBeeProfileType.getByValue(profileId));
          endpoint = new ZigBeeEndpoint(node, zigBeeEndpointEntity.getEndpointId());
          endpoint.setProfileId(profileId);
          node.addEndpoint(endpoint);
        }
      }

      if (!initializeZigBeeChannelConverters()) {
        log.error("{}: Unable to create zigbee converters", nodeIeeeAddress);
        return;
      }

      boolean foundAnyCluster = this.entity.getEndpoints().stream().noneMatch(e -> e.getStatus() == Status.ONLINE);
      if (!foundAnyCluster) {
        log.warn("{}: No supported clusters found", nodeIeeeAddress);
        entity.setStatus(Status.OFFLINE, "zigbee.error.no_cluster_found");
        return;
      }

      // If this is an RFD then we reduce polling to the max to avoid wasting battery
      if (node.isReducedFunctionDevice()) {
        pollingPeriod = 1800;
      }

      int expectedUpdatePeriod = getExpectedUpdatePeriod();
      expectedUpdatePeriod = (expectedUpdatePeriod * 2) + 30;
      log.debug("{}: Setting ONLINE/OFFLINE timeout interval to: {}", nodeIeeeAddress, expectedUpdatePeriod);
      zigBeeIsAliveTracker.addHandler(this, expectedUpdatePeriod);

      // Update the binding table.
      // We're not doing anything with the information here, but we want it up to date, so it's ready for use later.
      try {
        ZigBeeStatus zigBeeStatus = node.updateBindingTable().get();
        if (zigBeeStatus != ZigBeeStatus.SUCCESS) {
          log.debug("{}: Error getting binding table. Actual status: <{}>", nodeIeeeAddress, zigBeeStatus);
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("{}: Exception getting binding table ", nodeIeeeAddress, e);
      }
      entity.setStatusOnline();

      startPolling();

      log.debug("{}: Done initialising ZigBee device", nodeIeeeAddress);

      // Save the network state
      this.coordinatorService.serializeNetwork(node.getIeeeAddress());
    } finally {
      this.entity.setNodeInitializationStatus(Status.DONE);
    }
  }

  private void createDynamicEndpoints() {
    // Dynamically create the zigBeeConverterEndpoints from the device
    // Process all the endpoints for this device and add all zigBeeConverterEndpoints as derived from the supported clusters
    for (ZigBeeEndpoint endpoint : this.coordinatorService.getNodeEndpoints(nodeIeeeAddress)) {
      log.debug("{}: Checking endpoint zigBeeConverterEndpoints", nodeIeeeAddress);
      Collection<ZigBeeBaseChannelConverter> cluster = zigBeeChannelConverterFactory.getZigBeeConverterEndpoints(endpoint);
      entity.createEndpoints(entityContext, endpoint.getIeeeAddress().toString(), endpoint.getEndpointId(), cluster);
    }
    log.debug("{}: Dynamically created {} zigBeeConverterEndpoints", nodeIeeeAddress, entity.getEndpoints().size());
  }

  private void createMissingRequireEndpointClusters() {
    for (EndpointDefinition endpointDefinition : ZigBeeDefineEndpoints.getEndpointDefinitions(entity.getModelIdentifier())) {
      ZigBeeEndpointEntity foundEndpoint = entity.findEndpoint(null, endpointDefinition.getInputCluster(),
          endpointDefinition.getEndpoint(), endpointDefinition.getTypeId());
      if (foundEndpoint == null) {
        log.info("Add zigbee node <{}> missed require endpoint: <{}>", nodeIeeeAddress, endpointDefinition);
        Collection<ZigBeeBaseChannelConverter> endpoints = zigBeeChannelConverterFactory.createConverterEndpoint(endpointDefinition);
        entity.createEndpoints(entityContext, nodeIeeeAddress.toString(), endpointDefinition.getEndpoint(), endpoints);
      }
    }
  }

  private boolean initializeZigBeeChannelConverters() {
    try {
      // TODO: do same as: node.getIeeeAddress() ??????
      ZigBeeCoordinatorService coordinatorHandler = this.coordinatorService;
      ZigBeeNode node = coordinatorHandler.getNode(nodeIeeeAddress);

      for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
        ZigBeeBaseChannelConverter channelConverter = endpoint.getService().getChannel();

        // set endpoint service target <ZigBeeEndpoint>
        endpoint.getService().setZigBeeEndpoint(coordinatorHandler.getEndpoint(node.getIeeeAddress(), endpoint.getEndpointId()));

       /* if (handler == null) {
          zigBeeDeviceEndpoint.setStatusError("No handler found");
          log.debug("{}: No handler found for {}", nodeIeeeAddress, zigBeeConverterEndpoint);
          continue;
        } */

        if (!channelConverter.initializeDevice()) {
          endpoint.setStatusError("Failed to configure device");
          log.info("{}: failed to initialise device converter <{}>", nodeIeeeAddress, endpoint);
          continue;
        }

        if (!channelConverter.initializeConverter()) {
          endpoint.setStatusError("Failed to initialise converter");
          log.info("{}: Channel {} failed to initialise converter", nodeIeeeAddress, endpoint);
          continue;
        }

        // TODO: ????????
        channelConverter.updateConfiguration(/*new Configuration(), channel.getConfiguration().getProperties()*/);

        if (channelConverter.getPollingPeriod() < pollingPeriod) {
          pollingPeriod = channelConverter.getPollingPeriod();
        }
      }
    } catch (Exception e) {
      log.error("{}: Exception creating zigBeeConverterEndpoints ", nodeIeeeAddress, e);
      entity.setStatus(Status.OFFLINE, "zigbee.error.handler_initializing_error");
      return false;
    }
    log.debug("{}: Channel initialisation complete", nodeIeeeAddress);
    return true;
  }

  private int getExpectedUpdatePeriod() {
    int minInterval = Integer.MAX_VALUE;
    for (ZigBeeEndpointEntity endpoint : this.entity.getEndpoints()) {
      minInterval = Math.min(minInterval, endpoint.getService().getChannel().getMinPoolingInterval());
    }
    return minInterval;
  }

  public void aliveTimeoutReached() {
    entity.setStatus(Status.OFFLINE, "zigbee.error.alive_timeout_reached");
  }

  public void dispose() {
    log.debug("{}: Handler dispose.", nodeIeeeAddress);

    stopPolling();

    if (nodeIeeeAddress != null) {
      if (this.coordinatorService != null) {
        this.coordinatorService.removeNetworkNodeListener(this);
        this.coordinatorService.removeAnnounceListener(this);
      }
    }

    for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
      endpoint.getService().getChannel().disposeConverter();
      endpoint.setStatus(Status.OFFLINE, "Dispose");
    }

    entity.setStatus(Status.OFFLINE);
    zigBeeIsAliveTracker.removeHandler(this);
  }

  private void stopPolling() {
    synchronized (pollingSync) {
      if (pollingJob != null) {
        pollingJob.cancel();
        pollingJob = null;
        log.debug("{}: Polling stopped", nodeIeeeAddress);
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
      log.debug("{}: Polling initialised at {}ms", nodeIeeeAddress, pollingPeriodMs);
    }
  }

  public void pullChannels() {
    try {
      log.info("{}: Polling started", nodeIeeeAddress);

      for (ZigBeeEndpointEntity endpoint : entity.getEndpoints()) {
        if (endpoint.getStatus() == Status.ONLINE) {
          log.debug("{}: Polling {}", nodeIeeeAddress, endpoint);
          ZigBeeBaseChannelConverter converter = endpoint.getService().getChannel();
          if (converter == null) {
            log.debug("{}: Polling aborted as no converter found for {}", nodeIeeeAddress, endpoint);
          } else {
            converter.fireHandleRefresh();
          }
        }
      }

      log.info("{}: Polling done", nodeIeeeAddress);
    } catch (Exception e) {
      log.warn("{}: Polling aborted due to exception ", nodeIeeeAddress, e);
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
    if (!node.getIeeeAddress().equals(nodeIeeeAddress) || !entity.getStatus().isOnline()) {
      return;
    }
    log.debug("{}: Node has been updated. Fire initialize it.", nodeIeeeAddress);
    if (isInitializeFinished()) {
      initialiseZigBeeNode();
    }
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
      throw new IllegalStateException("ACTION.ALREADY_STARTED");
    }
    this.nodeDiscoveryThreadContext = entityContext.bgp().builder("discover-node-" + node.getIeeeAddress())
        .execute(() -> entity.updateFromNode(node, entityContext));
  }

  private boolean isInitializeFinished() {
    return entity.getNodeInitializationStatus() == Status.DONE;
  }

  @Override
  public void entityUpdated(ZigBeeDeviceEntity entity) {
    this.entity = entity;
  }

  @Override
  public void destroy() {
    this.dispose();
  }

  @Override
  public void testService() {

  }
}
