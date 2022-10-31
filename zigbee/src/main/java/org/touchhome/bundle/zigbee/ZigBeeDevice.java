package org.touchhome.bundle.zigbee;

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
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEndpoint;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition.EndpointDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;

@Log4j2
public class ZigBeeDevice implements ZigBeeNetworkNodeListener, ZigBeeAnnounceListener {

  private final Object pollingSync = new Object();
  @Getter
  private final IeeeAddress nodeIeeeAddress;
  @Getter
  private final ZigBeeNodeDescription zigBeeNodeDescription;
  @Getter
  private final ZigBeeDiscoveryService discoveryService;
  private final ZigBeeNode node;
  private ThreadContext<Void> pollingJob = null;
  @Getter
  @Setter
  private int pollingPeriod = 86400;
  @Getter
  private ZigBeeDeviceEntity entity;

  private Thread nodeDiscoveryThread;
  private final EntityContext entityContext;

  ZigBeeDevice(ZigBeeDiscoveryService discoveryService, IeeeAddress nodeIeeeAddress, ZigBeeNode node, EntityContext entityContext) {
    log.info("{}: Creating zigBee device", nodeIeeeAddress);
    this.node = node;
    this.entityContext = entityContext;
    this.discoveryService = discoveryService;

    this.zigBeeNodeDescription = new ZigBeeNodeDescription(nodeIeeeAddress);
    this.nodeIeeeAddress = nodeIeeeAddress;

    this.discoveryService.getCoordinatorHandler().addNetworkNodeListener(this);
    this.discoveryService.getCoordinatorHandler().addAnnounceListener(this);

    tryInitializeDevice(discoveryService.getCoordinator().getStatus());

    // register listener for reset timer if any updates from any endpoint
    this.discoveryService.getDeviceUpdateListener().addIeeeAddressListener(this.nodeIeeeAddress.toString(), state ->
    {
      discoveryService.getZigBeeIsAliveTracker().resetTimer(this);
      updateStatus(Status.ONLINE, "");
    });
  }

  private void tryInitializeDevice(Status coordinatorStatus) {
    if (coordinatorStatus != Status.ONLINE) {
      log.trace("{}: Coordinator is unknown or not online.", nodeIeeeAddress);
      zigBeeNodeDescription.setNodeInitialized(false);
      updateStatus(Status.OFFLINE, "Coordinator unknown status");
      stopPolling();
    } else if (!zigBeeNodeDescription.isNodeInitialized() && isInitializeFinished()) {
      log.debug("{}: Coordinator is ONLINE. Starting device initialisation.", nodeIeeeAddress);
      this.discoveryService.getCoordinatorHandler().rediscoverNode(nodeIeeeAddress);
      initialiseZigBeeNode();
    }
  }

  public void initialiseZigBeeNode() {
    if (!isInitializeFinished()) {
      throw new IllegalStateException("Node <" + nodeIeeeAddress + "> initialization already started");
    }
    this.zigBeeNodeDescription.setNodeInitializationStatus(ZigBeeNodeDescription.NodeInitializationStatus.WaitForStart);
    entityContext.bgp().builder("zigbee-node-init-" + this.nodeIeeeAddress)
        .delay(Duration.ofMillis(10)).execute(this::doNodeInitialisation);
  }

  private synchronized void doNodeInitialisation() {
    this.zigBeeNodeDescription.setNodeInitializationStatus(ZigBeeNodeDescription.NodeInitializationStatus.Started);
    try {
      log.info("{}: Initialize zigBee device", nodeIeeeAddress);
      ZigBeeNode node = this.discoveryService.getCoordinatorHandler().getNode(nodeIeeeAddress);
      if (node == null) {
        log.debug("{}: Node not found", nodeIeeeAddress);
        updateStatus(Status.OFFLINE, "zigbee.error.OFFLINE_NODE_NOT_FOUND");
        return;
      }

      // Check if discovery is complete, and we know all the services the node supports
      if (!node.isDiscovered()) {
        log.debug("{}: Node has not finished discovery", nodeIeeeAddress);
        updateStatus(Status.OFFLINE, "zigbee.error.OFFLINE_DISCOVERY_INCOMPLETE");
        return;
      }

      log.debug("{}: Start initialising ZigBee channels", nodeIeeeAddress);

      for (ZigBeeDeviceEndpoint endpoint : entity.getEndpoints()) {
        endpoint.setStatus(Status.OFFLINE, "Uninitialised");
      }

      // update node description in thread or not
      this.updateNodeDescription(node);

      // for remove old one if exists
      this.discoveryService.getEntityContext().event()
          .removeEntityUpdateListener(this.entity.getEntityID(), "zigbee-change-listener");

      createDynamicEndpoints();

      if (zigBeeNodeDescription.getModelIdentifier() != null) {
        createMissingRequireEndpointClusters();
      }

      // Create missing endpoints in zsmartsystem.node
      for (ZigBeeDeviceEndpoint zigBeeDeviceEndpoint : entity.getEndpoints()) {
        ZigBeeEndpoint endpoint = node.getEndpoint(zigBeeDeviceEndpoint.getEndpointId());
        if (endpoint == null) {
          int profileId = ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION.getKey();
          log.debug("{}: Creating statically defined device endpoint {} with profile {}", nodeIeeeAddress,
              zigBeeDeviceEndpoint.getEndpointId(), ZigBeeProfileType.getByValue(profileId));
          endpoint = new ZigBeeEndpoint(node, zigBeeDeviceEndpoint.getEndpointId());
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
        updateStatus(Status.OFFLINE, "zigbee.error.NO_CLUSTER_FOUND");
        return;
      }

      // If this is an RFD then we reduce polling to the max to avoid wasting battery
      if (node.isReducedFunctionDevice()) {
        pollingPeriod = 1800;
      }

      int expectedUpdatePeriod = getExpectedUpdatePeriod();
      expectedUpdatePeriod = (expectedUpdatePeriod * 2) + 30;
      log.debug("{}: Setting ONLINE/OFFLINE timeout interval to: {}", nodeIeeeAddress, expectedUpdatePeriod);
      this.discoveryService.getZigBeeIsAliveTracker().addHandler(this, expectedUpdatePeriod);

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
      zigBeeNodeDescription.setNodeInitialized(true);
      // TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  zigBeeNodeDescription.setChannels(this.zigBeeConverterEndpoints);

      updateStatus(Status.ONLINE, null);

      startPolling();

      log.debug("{}: Done initialising ZigBee device", nodeIeeeAddress);

      // Save the network state
      this.discoveryService.getCoordinatorHandler().serializeNetwork(node.getIeeeAddress());
    } finally {
      this.zigBeeNodeDescription.setNodeInitializationStatus(ZigBeeNodeDescription.NodeInitializationStatus.Finished);
    }
  }

  private void createDynamicEndpoints() {
    // Dynamically create the zigBeeConverterEndpoints from the device
    // Process all the endpoints for this device and add all zigBeeConverterEndpoints as derived from the supported clusters
    for (ZigBeeEndpoint endpoint : this.discoveryService.getCoordinatorHandler().getNodeEndpoints(nodeIeeeAddress)) {
      log.debug("{}: Checking endpoint zigBeeConverterEndpoints", nodeIeeeAddress);
      Collection<ZigBeeBaseChannelConverter> cluster = discoveryService.getZigBeeChannelConverterFactory().getZigBeeConverterEndpoints(endpoint);
      entity.createEndpoints(entityContext, endpoint.getIeeeAddress().toString(), endpoint.getEndpointId(), cluster);
    }
    log.debug("{}: Dynamically created {} zigBeeConverterEndpoints", nodeIeeeAddress, entity.getEndpoints().size());
  }

  private void updateNodeDescription(ZigBeeNode node) {
    this.entity = this.discoveryService.getEntityContext().getEntity(ZigBeeDeviceEntity.PREFIX + node.getIeeeAddress());
    if (entity == null || entity.getModel() == null) {
      startDiscoveryNodeDescription(node, null, true);
    } else {
      startDiscoveryNodeDescription(node, entity.getModel(), false);
    }
  }

  private void createMissingRequireEndpointClusters() {
    for (EndpointDefinition endpointDefinition : ZigBeeRequireEndpoints.getEndpointDefinitions(zigBeeNodeDescription.getModelIdentifier())) {
      ZigBeeDeviceEndpoint foundEndpoint = entity.findEndpoint(null, endpointDefinition.getInputCluster(),
          endpointDefinition.getEndpoint(), endpointDefinition.getTypeId());
      if (foundEndpoint == null) {
        log.info("Add zigbee node <{}> missed require endpoint: <{}>", nodeIeeeAddress, endpointDefinition);
        Collection<ZigBeeBaseChannelConverter> endpoints = discoveryService.getZigBeeChannelConverterFactory()
            .createConverterEndpoint(endpointDefinition);
        entity.createEndpoints(entityContext, nodeIeeeAddress.toString(), endpointDefinition.getEndpoint(), endpoints);
      }
    }
  }

  private boolean initializeZigBeeChannelConverters() {
    try {
      // TODO: do same as: node.getIeeeAddress() ??????
      ZigBeeCoordinatorHandler coordinatorHandler = this.discoveryService.getCoordinatorHandler();
      ZigBeeNode node = coordinatorHandler.getNode(nodeIeeeAddress);

      for (ZigBeeDeviceEndpoint endpoint : entity.getEndpoints()) {
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
      updateStatus(Status.OFFLINE, "zigbee.error.HANDLER_INITIALIZING_ERROR");
      return false;
    }
    log.debug("{}: Channel initialisation complete", nodeIeeeAddress);
    return true;
  }

  private void updateStatus(Status deviceStatus, String deviceStatusMessage) {
    this.entity.setStatus(deviceStatus, deviceStatusMessage);
    if (this.zigBeeNodeDescription.getDeviceStatus() != deviceStatus) {
      this.zigBeeNodeDescription.setDeviceStatus(deviceStatus);
      this.zigBeeNodeDescription.setDeviceStatusMessage(deviceStatusMessage);
      this.discoveryService.getEntityContext().ui().sendInfoMessage(
          "ZigBee device status", this.nodeIeeeAddress.toString() + " - " + deviceStatus);
    }
  }

  private int getExpectedUpdatePeriod() {
    int minInterval = Integer.MAX_VALUE;
    for (ZigBeeDeviceEndpoint endpoint : this.entity.getEndpoints()) {
      minInterval = Math.min(minInterval, endpoint.getService().getChannel().getMinPoolingInterval());
    }
    return minInterval;
  }

  void aliveTimeoutReached() {
    updateStatus(Status.OFFLINE, "zigbee.error.ALIVE_TIMEOUT_REACHED");
  }

  void dispose() {
    log.debug("{}: Handler dispose.", nodeIeeeAddress);

    stopPolling();

    if (nodeIeeeAddress != null) {
      if (this.discoveryService.getCoordinatorHandler() != null) {
        this.discoveryService.getCoordinatorHandler().removeNetworkNodeListener(this);
        this.discoveryService.getCoordinatorHandler().removeAnnounceListener(this);
      }
    }

    for (ZigBeeDeviceEndpoint endpoint : entity.getEndpoints()) {
      endpoint.getService().getChannel().disposeConverter();
      endpoint.setStatus(Status.OFFLINE, "Dispose");
    }

    this.discoveryService.getZigBeeIsAliveTracker().removeHandler(this);

    zigBeeNodeDescription.setNodeInitialized(false);
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
          .execute(() -> {
            createPoolingThread().run();
          });
      log.debug("{}: Polling initialised at {}ms", nodeIeeeAddress, pollingPeriodMs);
    }
  }

  public Runnable createPoolingThread() {
    return () -> {
      try {
        log.info("{}: Polling started", nodeIeeeAddress);

        for (ZigBeeDeviceEndpoint endpoint : entity.getEndpoints()) {
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
    };
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
    if (!node.getIeeeAddress().equals(nodeIeeeAddress) || zigBeeNodeDescription.isNodeInitialized()) {
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
    updateStatus(Status.OFFLINE, "zigbee.error.REMOVED_BY_DONGLE");
  }

  @Override
  public String toString() {
    return "ZigBeeDevice{" +
        "nodeIeeeAddress=" + nodeIeeeAddress +
        ", zigBeeNodeDescription=" + zigBeeNodeDescription +
        '}';
  }

  public void updateValue(ZigBeeDeviceEndpoint zigBeeDeviceEndpoint, State state, boolean pooling) {
    this.discoveryService.getDeviceUpdateListener()
        .updateValue(this, zigBeeDeviceEndpoint.getEndpointUUID(), state, pooling);
  }

  public void discoveryNodeDescription(String savedModelIdentifier) {
    ZigBeeNode node = this.discoveryService.getCoordinatorHandler().getNode(nodeIeeeAddress);
    if (node == null) {
      throw new IllegalStateException("Unable to find node: <" + nodeIeeeAddress + ">");
    }
    startDiscoveryNodeDescription(node, savedModelIdentifier, false);
  }

  @SneakyThrows
  private void startDiscoveryNodeDescription(ZigBeeNode node, String savedModelIdentifier, boolean waitResponse) {
    if (nodeDiscoveryThread != null && nodeDiscoveryThread.isAlive()) {
      throw new IllegalStateException("ACTION.ALREADY_STARTED");
    }
    nodeDiscoveryThread = new Thread(() -> {
      this.zigBeeNodeDescription.updateFromNode(node);
      if (this.zigBeeNodeDescription.getModelIdentifier() == null) {
        this.zigBeeNodeDescription.setModelIdentifier(savedModelIdentifier);
      }
    });
    nodeDiscoveryThread.start();
    if (waitResponse) {
      nodeDiscoveryThread.join();
    }
  }

  public long getChannelCount(int clusterId) {
    return entity.getEndpoints(clusterId).size();
  }

  private boolean isInitializeFinished() {
    return zigBeeNodeDescription.getNodeInitializationStatus() == null || zigBeeNodeDescription.getNodeInitializationStatus().finished();
  }
}
