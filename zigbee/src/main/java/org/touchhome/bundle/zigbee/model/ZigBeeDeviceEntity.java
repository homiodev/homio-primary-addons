package org.touchhome.bundle.zigbee.model;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_APPLICATIONVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_DATECODE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_HWVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_MANUFACTURERNAME;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_MODELIDENTIFIER;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_STACKVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_ZCLVERSION;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOtaUpgradeCluster;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor.LogicalType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.CurrentPowerModeType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.PowerLevelType;
import com.zsmartsystems.zigbee.zdo.field.PowerDescriptor.PowerSourceType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.converter.JSONObjectConverter;
import org.touchhome.bundle.api.converter.StringSetConverter;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.validation.MaxItems;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnoreGetDefault;
import org.touchhome.bundle.api.ui.field.UIFieldInlineEntity;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.zigbee.SelectModelIdentifierDynamicLoader;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.service.ZigBeeCoordinatorService;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeDefineEndpoints;
import org.touchhome.common.exception.NotFoundException;
import org.touchhome.common.util.Lang;

@Log4j2
@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, bg = "#de9ed7",
    order = 5, overridePath = "zigbee")
public final class ZigBeeDeviceEntity extends BaseEntity<ZigBeeDeviceEntity> implements
    HasJsonData, HasStatusAndMsg<ZigBeeDeviceEntity>, EntityService<ZigBeeDeviceService, ZigBeeDeviceEntity> {

  public static final String PREFIX = "zb_";

  @UIField(readOnly = true, order = 5)
  private String ieeeAddress;

  @UIField(order = 11)
  private String description;

  @UIField(order = 12, readOnly = true)
  @Enumerated(EnumType.STRING)
  private LogicalType logicalType;

  @MaxItems(30) // max 30 variables in one group
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "zigBeeDeviceEntity")
  @UIField(order = 30)
  @UIFieldInlineEntity(bg = "#1E5E611F", addRow = "bla-bla-bla")
  private Set<ZigBeeEndpointEntity> endpoints;

  @UIField(order = 33, readOnly = true)
  private int networkAddress = 0;

  @UIField(order = 35, readOnly = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public Status getNodeInitializationStatus() {
    return TouchHomeUtils.STATUS_MAP.getOrDefault(getEntityID() + "_NS", DEFAULT_STATUS).getFirst();
  }

  public void setNodeInitializationStatus(Status status) {
    TouchHomeUtils.STATUS_MAP.put(getEntityID() + "_NS", Pair.of(status, ""));
  }

  @Lob
  @Column(length = 10_000)
  @Convert(converter = JSONObjectConverter.class)
  private JSONObject jsonData = new JSONObject();

  @ManyToOne
  private ZigbeeCoordinatorEntity coordinatorEntity;

  @UIField(order = 50)
  @UIFieldSelection(value = SelectModelIdentifierDynamicLoader.class, allowInputRawText = true)
  @UIFieldSelectValueOnEmpty(label = "zigbee.action.selectModelIdentifier", color = "#A7D21E")
  private String modelIdentifier;

  @UIField(order = 51, readOnly = true)
  private String imageIdentifier;

  @UIField(readOnly = true, order = 1001, hideOnEmpty = true)
  @Enumerated(EnumType.STRING)
  private PowerLevelType powerLevel;

  @UIField(readOnly = true, order = 1002, hideOnEmpty = true)
  @Enumerated(EnumType.STRING)
  private CurrentPowerModeType powerMode;

  @UIField(readOnly = true, order = 1003, hideOnEmpty = true)
  @Enumerated(EnumType.STRING)
  private PowerSourceType powerSource;

  @UIField(readOnly = true, type = UIFieldType.Chips, order = 1004, hideOnEmpty = true)
  @Convert(converter = StringSetConverter.class)
  private Set<String> availablePowerSources;

  @UIField(readOnly = true, order = 1005, hideOnEmpty = true)
  private int manufacturerCode;

  @UIField(readOnly = true, order = 1006, hideOnEmpty = true)
  private Date nodeLastUpdateTime;

  @UIField(readOnly = true, order = 1007, hideOnEmpty = true)
  private String firmwareVersion;
  private String manufacturer;
  private Integer hwVersion;
  private Integer appVersion;
  private Integer stackVersion;
  private Integer zclVersion;
  private String dateCode;

  @Override
  public void logChangeStatus(Status status, String message) {
    Level level = status == Status.ERROR ? Level.ERROR : Level.INFO;
    if (StringUtils.isEmpty(message)) {
      log.log(level, "Set ZigBee device status: {}", status);
    } else {
      log.log(level, "Set ZigBee device status: {}. Msg: {}", status, message);
    }
  }

  @UIField(readOnly = true, order = 1200, hideOnEmpty = true)
  @UIFieldIgnoreGetDefault
  public String getMaxTimeoutBeforeOfflineNode() {
    Integer interval = getService().getExpectedUpdateInterval();
    return interval == null ? "Not set" : TimeUnit.SECONDS.toMinutes(interval) + "min";
  }

  @UIField(readOnly = true, order = 1201)
  @UIFieldIgnoreGetDefault
  public String getTimeoutBeforeOfflineNode() {
    ZigBeeDeviceService service = getService();
    Integer interval = service.getExpectedUpdateInterval();
    Long intervalTimer = service.getExpectedUpdateIntervalTimer();
    if (intervalTimer == null || interval == null) {
      return "Not set";
    }
    String min = String.valueOf(TimeUnit.SECONDS.toMinutes(interval - (System.currentTimeMillis() - intervalTimer) / 1000));
    return "Expired in: " + min + "min";
  }

  @UIContextMenuAction("ACTION.INITIALIZE_ZIGBEE_NODE")
  public ActionResponseModel initializeZigBeeNode() {
    getService().initialiseZigBeeNode();
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.NODE_INITIALIZATION_STARTED");
  }

  @UIContextMenuAction("ACTION.REDISCOVERY")
  public ActionResponseModel rediscoveryNode() {
    getService().discoveryNodeDescription();
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.REDISCOVERY_STARTED");
  }

  @UIContextMenuAction("ACTION.ZIGBEE_PULL_CHANNELS")
  public ActionResponseModel pullChannels(EntityContext entityContext) {
    entityContext.bgp().builder("pull-channels-" + ieeeAddress)
        .execute(() -> getService().pullChannels());
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.ZIGBEE_PULL_CHANNELS_STARTED");
  }

  @UIContextMenuAction("ACTION.PERMIT_JOIN")
  public ActionResponseModel permitJoin() {
    if (coordinatorEntity.getStatus() != Status.ONLINE) {
      throw new IllegalStateException("DEVICE_OFFLINE");
    }
    ZigBeeCoordinatorService zigBeeCoordinatorService = coordinatorEntity.getService();
    boolean join = zigBeeCoordinatorService.permitJoin(toIeeeAddress(), coordinatorEntity.getDiscoveryDuration());
    return join ? ActionResponseModel.showSuccess("ACTION.RESPONSE.STARTED") :
        ActionResponseModel.showError("ACTION.RESPONSE.ERROR");
  }

  @Override
  public String toString() {
    return "ZigBee [modelIdentifier='" + getModelIdentifier() + "]. IeeeAddress-" + ieeeAddress + ". Name";
  }

  @Override
  public void afterDelete(EntityContext entityContext) {
    ZigBeeCoordinatorService service = coordinatorEntity.getService();
    service.removeNode(toIeeeAddress());
    service.leave(toIeeeAddress(), true);
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  public ZigBeeEndpointEntity getEndpointRequired(String ieeeAddress, Integer clusterId, Integer endpointId, String clusterName) {
    ZigBeeEndpointEntity endpoint = findEndpoint(ieeeAddress, clusterId, endpointId, clusterName);
    if (endpoint == null) {
      throw new NotFoundException("Unable to find endpoint: IeeeAddress: " + ieeeAddress + ". ClusterId: " + clusterId
          + ". EndpointId: " + endpointId + ". ClusterName: " + clusterName);
    }
    return endpoint;
  }

  public void createEndpoints(EntityContext entityContext, String ieeeAddress, int endpointId, Collection<ZigBeeBaseChannelConverter> clusters) {
    ZigBeeDeviceService zigBeeDeviceService = getService();
    ZigBeeCoordinatorService coordinatorService = zigBeeDeviceService.getCoordinatorService();
    int localEndpointId = coordinatorService.getLocalEndpointId(ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION);
    IeeeAddress localIeeeAddress = coordinatorService.getLocalIeeeAddress();

    for (ZigBeeBaseChannelConverter cluster : clusters) {
      ZigBeeEndpointEntity zigBeeEndpointEntity = findEndpoint(ieeeAddress,
          cluster.getAnnotation().clientCluster(), endpointId, cluster.getAnnotation().name());

      if (zigBeeEndpointEntity == null) {
        zigBeeEndpointEntity = new ZigBeeEndpointEntity()
            .setIeeeAddress(ieeeAddress)
            .setClusterId(cluster.getAnnotation().clientCluster())
            .setEndpointId(endpointId)
            .setZigBeeDeviceEntity(this);
        zigBeeEndpointEntity = entityContext.save(zigBeeEndpointEntity);
      }

      ZigbeeEndpointService endpointService = new ZigbeeEndpointService(entityContext, cluster, zigBeeDeviceService,
          zigBeeEndpointEntity, coordinatorEntity, localEndpointId, localIeeeAddress);
      cluster.setEndpointService(endpointService);
      EntityService.entityToService.put(zigBeeEndpointEntity.getEntityID(), new ServiceIdentifier(0, endpointService));
    }
  }

  public ZigBeeEndpointEntity findEndpoint(String ieeeAddress, Integer clusterId, Integer endpointId, String clusterName) {
    for (ZigBeeEndpointEntity endpoint : endpoints) {
      if (endpoint.getEndpointId() == endpointId && (ieeeAddress == null || endpoint.getIeeeAddress().equals(ieeeAddress))
          && endpoint.getClusterId() == clusterId && endpoint.getName().equals(clusterName)) {
        return endpoint;
      }
    }
    return null;
  }

  public List<ZigBeeEndpointEntity> filterEndpoints(int clusterId) {
    return endpoints.stream().filter(e -> e.getClusterId() == clusterId).collect(Collectors.toList());
  }

  @UIField(order = 120, readOnly = true, hideOnEmpty = true)
  @UIFieldColorStatusMatch
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public Status getFetchInfoStatus() {
    return TouchHomeUtils.STATUS_MAP.getOrDefault(getEntityID() + "_FI", DEFAULT_STATUS).getFirst();
  }

  public void setFetchInfoStatus(Status status, @NotNull String msg) {
    TouchHomeUtils.STATUS_MAP.put(getEntityID() + "_FI", Pair.of(status, msg));
  }

  @UIField(order = 121, readOnly = true, hideOnEmpty = true)
  public String getFetchInfoStatusMessage() {
    return TouchHomeUtils.STATUS_MAP.getOrDefault(getEntityID() + "_FI", DEFAULT_STATUS).getSecond();
  }

  public void updateFromNode(ZigBeeNode node, EntityContext entityContext) {
    log.info("Starting fetch info from ZigBeeNode: <{}>", node.getIeeeAddress().toString());
    setFetchInfoStatus(Status.RUNNING, "");

    boolean updated = updateFromNodeDescriptor(node);
    updated |= updateFromOtaCluster(node);
    updated |= updateFromBasicCluster(node);

    // try to find image for device
    if (this.imageIdentifier == null || this.getName() == null || this.getDescription() == null) {
      DeviceDefinition deviceDefinition = ZigBeeDefineEndpoints.findDeviceDefinition(this);
      if (deviceDefinition != null) {
        this.imageIdentifier = deviceDefinition.getImage();
        setName(StringUtils.defaultString(deviceDefinition.getLabel(Lang.CURRENT_LANG), "name not found"));
        this.description = StringUtils.defaultString(deviceDefinition.getDescription(Lang.CURRENT_LANG), "");
      }
    }

    log.info("Finished fetch info from ZigBeeNode: <{}>", node.getIeeeAddress().toString());
    setFetchInfoStatus(Status.OFFLINE, "");

    if (updated) {
      entityContext.save(this);
    }
  }

  private boolean updateFromBasicCluster(ZigBeeNode node) {
    boolean updated = false;
    ZclBasicCluster basicCluster = (ZclBasicCluster) node.getEndpoints().stream()
        .map(ep -> ep.getInputCluster(ZclBasicCluster.CLUSTER_ID)).filter(Objects::nonNull).findFirst()
        .orElse(null);

    if (basicCluster == null) {
      setFetchInfoStatus(Status.ERROR, "Unable to find basic cluster");
      log.warn("Fetch info from ZigBeeNode: <{}> not completed", node.getIeeeAddress().toString());
      return false;
    }

    // Attempt to read all properties with a single command.
    // If successful, this updates the cache with the property values.
    try {
      // Try to get the supported attributes, so we can reduce the number of attribute read requests
      basicCluster.discoverAttributes(false).get();
      List<Integer> attributes = Arrays.asList(ATTR_MANUFACTURERNAME, ATTR_MODELIDENTIFIER, ATTR_HWVERSION,
          ATTR_APPLICATIONVERSION, ATTR_STACKVERSION, ATTR_ZCLVERSION, ATTR_DATECODE);

      // filter attributes that already fetched
      attributes.removeIf(attributeId -> basicCluster.getAttribute(attributeId).isLastValueCurrent(Long.MAX_VALUE));

      basicCluster.readAttributes(attributes).get();
    } catch (InterruptedException | ExecutionException e) {
      log.info("{}: There was an error when trying to read all properties with a single command.",
          node.getIeeeAddress(), e);
    }

    String manufacturer = (String) basicCluster.getAttribute(ATTR_MANUFACTURERNAME).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.manufacturer, manufacturer)) {
      this.manufacturer = manufacturer;
      return true;
    }

    String modelIdentifier = (String) basicCluster.getAttribute(ATTR_MODELIDENTIFIER).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.modelIdentifier, modelIdentifier)) {
      this.modelIdentifier = modelIdentifier;
      return true;
    }

    Integer hwVersion = (Integer) basicCluster.getAttribute(ATTR_HWVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.hwVersion, hwVersion)) {
      this.hwVersion = hwVersion;
      return true;
    }

    Integer appVersion = (Integer) basicCluster.getAttribute(ATTR_APPLICATIONVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.appVersion, appVersion)) {
      this.appVersion = appVersion;
      return true;
    }

    Integer stackVersion = (Integer) basicCluster.getAttribute(ATTR_STACKVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.stackVersion, stackVersion)) {
      this.stackVersion = stackVersion;
      return true;
    }

    Integer zclVersion = (Integer) basicCluster.getAttribute(ATTR_ZCLVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.zclVersion, zclVersion)) {
      this.zclVersion = zclVersion;
      return true;
    }

    String dateCode = (String) basicCluster.getAttribute(ATTR_DATECODE).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.dateCode, dateCode)) {
      this.dateCode = dateCode;
      return true;
    }

    return updated;
  }

  private boolean updateFromOtaCluster(ZigBeeNode node) {
    ZclOtaUpgradeCluster otaCluster = (ZclOtaUpgradeCluster) node.getEndpoints().stream()
        .map(ep -> ep.getOutputCluster(ZclOtaUpgradeCluster.CLUSTER_ID)).filter(Objects::nonNull).findFirst()
        .orElse(null);

    if (otaCluster != null) {
      log.debug("{}: ZigBee node property discovery using OTA cluster on endpoint {}", node.getIeeeAddress(),
          otaCluster.getZigBeeAddress());

      ZclAttribute attribute = otaCluster.getAttribute(ZclOtaUpgradeCluster.ATTR_CURRENTFILEVERSION);
      Object fileVersion = attribute.readValue(Long.MAX_VALUE);
      if (fileVersion != null) {
        String firmwareVersion = String.format("0x%08X", fileVersion);
        if (!Objects.equals(this.firmwareVersion, firmwareVersion)) {
          this.firmwareVersion = firmwareVersion;
          return true;
        }
      } else {
        log.debug("{}: Could not get OTA firmware version from device", node.getIeeeAddress());
      }
    } else {
      log.debug("{}: Node doesn't support OTA cluster", node.getIeeeAddress());
    }
    return false;
  }

  private boolean updateFromNodeDescriptor(ZigBeeNode node) {
    boolean updated = false;

    if (!Objects.equals(this.networkAddress, node.getNetworkAddress())) {
      this.networkAddress = node.getNetworkAddress();
      updated = true;
    }
    PowerDescriptor pd = node.getPowerDescriptor();
    if (pd != null) {
      if (!Objects.equals(this.powerLevel, pd.getPowerLevel())) {
        this.powerLevel = pd.getPowerLevel();
        updated = true;
      }
      if (!Objects.equals(this.powerMode, pd.getCurrentPowerMode())) {
        this.powerMode = pd.getCurrentPowerMode();
        updated = true;
      }
      if (!Objects.equals(this.powerSource, pd.getCurrentPowerSource())) {
        this.powerSource = pd.getCurrentPowerSource();
        updated = true;
      }
      Set<String> availablePowerSources = pd.getAvailablePowerSources().stream().map(Enum::name).collect(Collectors.toSet());
      if (!Objects.equals(this.availablePowerSources, availablePowerSources)) {
        this.availablePowerSources = availablePowerSources;
        updated = true;
      }
    }
    NodeDescriptor nd = node.getNodeDescriptor();
    if (nd != null) {
      if (!Objects.equals(this.manufacturerCode, nd.getManufacturerCode())) {
        this.manufacturerCode = nd.getManufacturerCode();
        updated = true;
      }
      if (!Objects.equals(this.logicalType, nd.getLogicalType())) {
        this.logicalType = nd.getLogicalType();
        updated = true;
      }
    }
    if (!Objects.equals(this.nodeLastUpdateTime, node.getLastUpdateTime())) {
      this.nodeLastUpdateTime = node.getLastUpdateTime();
      updated = true;
    }
    return updated;
  }

  private IeeeAddress toIeeeAddress() {
    return new IeeeAddress(ieeeAddress);
  }

  @Override
  public Class<ZigBeeDeviceService> getEntityServiceItemClass() {
    return ZigBeeDeviceService.class;
  }

  @Override
  public ZigBeeDeviceService createService(EntityContext entityContext) {
    return new ZigBeeDeviceService(coordinatorEntity.getService(), toIeeeAddress(), entityContext);
  }

  @Override
  public void testService(ZigBeeDeviceService service) {

  }

  @Override
  public void destroyService(ZigBeeDeviceService service) {
    service.dispose();
  }

  @Override
  public Object[] getServiceParams() {
    return new Object[0];
  }

  // do not change Status on create service
  @Override
  public @Nullable Status getSuccessServiceStatus() {
    return null;
  }
}
