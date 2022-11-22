package org.touchhome.bundle.zigbee.model;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_APPLICATIONVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_DATECODE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_HWVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_MANUFACTURERNAME;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_MODELIDENTIFIER;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_STACKVERSION;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster.ATTR_ZCLVERSION;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeNode;
import com.zsmartsystems.zigbee.app.discovery.ZigBeeNodeServiceDiscoverer;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclBasicCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOtaUpgradeCluster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextSetting;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityLog;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldGroup;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.condition.UIFieldDisableCreateTab;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.zigbee.SelectModelIdentifierDynamicLoader;
import org.touchhome.bundle.zigbee.model.service.ZigBeeCoordinatorService;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeDefineEndpoints;
import org.touchhome.common.exception.NotFoundException;

@Log4j2
@Getter
@Setter
@Entity
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, bg = "#de9ed7",
    order = 5, overridePath = "zigbee")
public class ZigBeeDeviceEntity extends DeviceBaseEntity<ZigBeeDeviceEntity> implements
    HasJsonData, HasNodeDescriptor, HasEntityLog,
    HasStatusAndMsg<ZigBeeDeviceEntity>, EntityService<ZigBeeDeviceService, ZigBeeDeviceEntity> {

  public static final String PREFIX = "zb_";

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
  @UIField(order = 9999)
  @UIFieldDisableCreateTab
  @UIFieldInlineEntities(bg = "#9B9B2E21")
  private Set<ZigBeeEndpointEntity> endpoints;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "parent_id")
  private ZigbeeCoordinatorEntity parent;

  @UIField(order = 11, hideInEdit = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public Status getNodeInitializationStatus() {
    return EntityContextSetting.getStatus(this, "node_init", Status.UNKNOWN);
  }

  public void setNodeInitializationStatus(Status status) {
    EntityContextSetting.setStatus(this, "node_init", "NodeInitializationStatus", status);
  }

  @UIField(hideInEdit = true, order = 1)
  @UIFieldGroup(value = "General", order = 1, borderColor = "#317175")
  @Override
  public String getIeeeAddress() {
    return super.getIeeeAddress();
  }

  @UIField(order = 2, hideOnEmpty = true)
  @UIFieldGroup("General")
  public String getDescription() {
    return getJsonData("descr", "");
  }

  public void setDescription(String description) {
    setJsonData("descr", description);
  }

  @UIField(order = 3, hideInEdit = true, type = UIFieldType.Duration)
  @UIFieldGroup("General")
  public long getLastAnswerFromEndpoints() {
    return EntityContextSetting.getMemValue(this, "lafe", 0L);
  }

  public void setLastAnswerFromEndpoints(long currentTimeMillis) {
    EntityContextSetting.setMemValue(this, "lafe", "", currentTimeMillis, true);
  }

  @UIField(hideInEdit = true, order = 2, hideOnEmpty = true)
  @UIFieldGroup("Node")
  public String getManufacturer() {
    return getJsonData("man");
  }

  public void setManufacturer(String manufacturer) {
    setJsonData("man", manufacturer);
  }

  @UIField(order = 3)
  @UIFieldSelection(value = SelectModelIdentifierDynamicLoader.class, allowInputRawText = true)
  @UIFieldSelectValueOnEmpty(label = "zigbee.action.select_model_identifier")
  @UIFieldGroup("Node")
  public String getModelIdentifier() {
    return getJsonData("m_id");
  }

  public void setModelIdentifier(String modelIdentifier) {
    setJsonData("m_id", modelIdentifier);
  }

  @UIField(order = 4, hideInEdit = true)
  @UIFieldGroup("Node")
  public String getImageIdentifier() {
    return getJsonData("i_id");
  }

  public void setImageIdentifier(String imageIdentifier) {
    setJsonData("i_id", imageIdentifier);
  }

  @UIField(hideInEdit = true, order = 1, hideOnEmpty = true)
  @UIFieldGroup(value = "Version", order = 100, borderColor = "#86AD2A")
  public Integer getHwVersion() {
    return getJsonData().optInt("hw_v");
  }

  public void setHwVersion(Integer hwVersion) {
    setJsonData("hw_v", hwVersion);
  }

  @UIField(hideInEdit = true, order = 2, hideOnEmpty = true)
  @UIFieldGroup("Version")
  public Integer getAppVersion() {
    return getJsonData().optInt("app_v");
  }

  public void setAppVersion(Integer appVersion) {
    setJsonData("app_v", appVersion);
  }

  @UIField(hideInEdit = true, order = 3, hideOnEmpty = true)
  @UIFieldGroup("Version")
  public Integer getStackVersion() {
    return getJsonData().optInt("stack_v");
  }

  public void setStackVersion(Integer stackVersion) {
    setJsonData("stack_v", stackVersion);
  }

  @UIField(hideInEdit = true, order = 4, hideOnEmpty = true)
  @UIFieldGroup("Version")
  public Integer getZclVersion() {
    return getJsonData().optInt("zcl_v");
  }

  public void setZclVersion(Integer zclVersion) {
    setJsonData("zcl_v", zclVersion);
  }

  @UIField(hideInEdit = true, order = 5, hideOnEmpty = true)
  @UIFieldGroup("Version")
  public String getDateCode() {
    return getJsonData("d_c");
  }

  public void setDateCode(String dateCode) {
    setJsonData("d_c", dateCode);
  }

  @UIField(hideInEdit = true, order = 4, hideOnEmpty = true)
  @UIFieldGroup("General")
  public String getMaxTimeoutBeforeOfflineNode() {
    return optService().map(service -> {
      Integer interval = service.getExpectedUpdateInterval();
      return interval == null ? "Not set" : TimeUnit.SECONDS.toMinutes(interval) + "min";
    }).orElse(null);
  }

  @UIField(order = 5, hideInEdit = true)
  @UIFieldGroup("General")
  public String getTimeoutBeforeOfflineNode() {
    return optService().map(service -> {
      Integer interval = service.getExpectedUpdateInterval();
      Long intervalTimer = service.getExpectedUpdateIntervalTimer();
      if (intervalTimer == null || interval == null) {
        return "Not set";
      }
      String min = String.valueOf(TimeUnit.SECONDS.toMinutes(interval - (System.currentTimeMillis() - intervalTimer) / 1000));
      return "Expired in: " + min + "min";
    }).orElse(null);
  }

  @UIField(order = 7, hideInEdit = true, hideOnEmpty = true)
  @UIFieldGroup("General")
  public String getFetchInfoStatusMessage() {
    return EntityContextSetting.getMessage(this, "fetch_info");
  }

  @UIContextMenuAction(value = "zigbee.action.re_initialize_node", icon = "fas fa-arrow-rotate-forward", iconColor = "#A939B7")
  public ActionResponseModel initializeZigBeeNode() {
    getService().initializeZigBeeNode();
    return ActionResponseModel.success();
  }

  @UIContextMenuAction(value = "zigbee.action.pull_endpoints", icon = "fas fa-exchange-alt", iconColor = "#B3981D")
  public ActionResponseModel pullChannels(EntityContext entityContext) {
    entityContext.bgp().builder("pull-channels-" + getIeeeAddress())
        .execute(() -> getService().pullChannels());
    return ActionResponseModel.success();
  }

  @UIContextMenuAction(value = "zigbee.action.permit_join", icon = "fas fa-arrows-to-eye", iconColor = "#1D8EB3")
  public ActionResponseModel permitJoin() {
    if (parent.getStatus() != Status.ONLINE) {
      throw new IllegalStateException("DEVICE_OFFLINE");
    }
    ZigBeeCoordinatorService zigBeeCoordinatorService = parent.getService();
    boolean join = zigBeeCoordinatorService.permitJoin(toIeeeAddress(), parent.getDiscoveryDuration());
    return join ? ActionResponseModel.success() :
        ActionResponseModel.showError("ACTION.RESPONSE.ERROR");
  }

  @Override
  public String getDefaultName() {
    return "Zigbee device";
  }

  @Override
  public String refreshName() {
    return null; // uses when persist this entity
  }

  @Override
  public String toString() {
    return "ZigBee device '" + getTitle() + "'. [IeeeAddress='" + getIeeeAddress() + ", ModelIdentifier=" + getModelIdentifier() + "]";
  }

  @Override
  public void afterDelete(EntityContext entityContext) {
    parent.optService().ifPresent(service -> {
      try {
        service.removeNode(toIeeeAddress());
        service.leave(toIeeeAddress(), true);
      } catch (Exception ex) {
        log.error("Something went wrong during detaching removed ZigBeeDeviceEntity from coordinator: {}", parent.getTitle());
      }
    });
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  public @NotNull ZigBeeEndpointEntity getEndpointRequired(@NotNull Integer endpointId, @NotNull Integer clusterId) {
    ZigBeeEndpointEntity endpoint = findEndpoint(clusterId, endpointId);
    if (endpoint == null) {
      throw new NotFoundException("Unable to find endpoint: EndpointId: " + endpointId + ". ClusterId: " + clusterId);
    }
    return endpoint;
  }

  public @Nullable ZigBeeEndpointEntity findEndpoint(@NotNull Integer clusterId, @NotNull Integer endpointId) {
    for (ZigBeeEndpointEntity endpoint : endpoints) {
      if (endpoint.getAddress() == endpointId && endpoint.getClusterId() == clusterId) {
        return endpoint;
      }
    }
    return null;
  }

  public List<ZigBeeEndpointEntity> filterEndpoints(int clusterId) {
    return endpoints.stream().filter(e -> e.getClusterId() == clusterId).collect(Collectors.toList());
  }

  public void updateFromNode(ZigBeeNode node, EntityContext entityContext) {
    log.info("[{}]: Starting fetch info from ZigBeeNode: <{}>", getEntityID(), node.getIeeeAddress().toString());
    setFetchInfoStatus(Status.RUNNING, null);

    boolean updated = updateFromNodeDescriptor(node);
    updated |= updateFromOtaCluster(node);
    updated |= updateFromBasicCluster(node);

      // try to find image for device
      if (isEmpty(this.getName()) || isEmpty(this.getImageIdentifier()) || isEmpty(this.getDescription())) {
        DeviceDefinition deviceDefinition = ZigBeeDefineEndpoints.findDeviceDefinition(this);
        if (deviceDefinition != null) {
          if (isEmpty(this.getImageIdentifier()) && !isEmpty(deviceDefinition.getImage())) {
            setImageIdentifier(deviceDefinition.getImage());
          updated = true;
          }
          String description = deviceDefinition.getDescription();
          if (isEmpty(this.getDescription()) && !isEmpty(description)) {
            setDescription(description);
          updated = true;
          }
          String label = deviceDefinition.getLabel();
          if (isEmpty(this.getName()) && !isEmpty(label)) {
            setName(label);
          updated = true;
          }
        }
      }

      log.info("[{}]: Finished fetch info from ZigBeeNode: <{}>", getEntityID(), node.getIeeeAddress());
      setFetchInfoStatus(Status.ONLINE, null);

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
      log.warn("[{}]: Fetch info from ZigBeeNode: <{}> not completed", getEntityID(), node.getIeeeAddress());
      return false;
    }

    // Attempt to read all properties with a single command.
    // If successful, this updates the cache with the property values.
    try {
      // Try to get the supported attributes, so we can reduce the number of attribute read requests
      basicCluster.discoverAttributes(false).get(60, TimeUnit.SECONDS);
      List<Integer> attributes = new ArrayList<>(Arrays.asList(ATTR_MANUFACTURERNAME, ATTR_MODELIDENTIFIER, ATTR_HWVERSION,
          ATTR_APPLICATIONVERSION, ATTR_STACKVERSION, ATTR_ZCLVERSION, ATTR_DATECODE));

      // filter attributes that already fetched
      attributes.removeIf(attributeId -> basicCluster.getAttribute(attributeId).isLastValueCurrent(Long.MAX_VALUE));

      if (!attributes.isEmpty()) {
        basicCluster.readAttributes(attributes).get(60, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      log.info("[{}]: There was an error when trying to read all properties with a single command. {}",
          getEntityID(), node.getIeeeAddress(), e);
    }

    Integer hwVersion = (Integer) basicCluster.getAttribute(ATTR_HWVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.getHwVersion(), hwVersion)) {
      this.setHwVersion(hwVersion);
      return true;
    }

    Integer appVersion = (Integer) basicCluster.getAttribute(ATTR_APPLICATIONVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.getAppVersion(), appVersion)) {
      this.setAppVersion(appVersion);
      return true;
    }

    Integer stackVersion = (Integer) basicCluster.getAttribute(ATTR_STACKVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.getStackVersion(), stackVersion)) {
      this.setStackVersion(stackVersion);
      return true;
    }

    Integer zclVersion = (Integer) basicCluster.getAttribute(ATTR_ZCLVERSION).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.getZclVersion(), zclVersion)) {
      this.setZclVersion(zclVersion);
      return true;
    }

    String dateCode = (String) basicCluster.getAttribute(ATTR_DATECODE).readValue(Long.MAX_VALUE);
    if (!Objects.equals(this.getDateCode(), dateCode)) {
      this.setDateCode(dateCode);
      return true;
    }

    return updated;
  }

  private boolean updateFromOtaCluster(ZigBeeNode node) {
    ZclOtaUpgradeCluster otaCluster = (ZclOtaUpgradeCluster) node.getEndpoints().stream()
        .map(ep -> ep.getOutputCluster(ZclOtaUpgradeCluster.CLUSTER_ID)).filter(Objects::nonNull).findFirst()
        .orElse(null);

    if (otaCluster != null) {
      log.debug("[{}]: ZigBee node {} property discovery using OTA cluster on endpoint {}", getEntityID(), node.getIeeeAddress(),
          otaCluster.getZigBeeAddress());

      ZclAttribute attribute = otaCluster.getAttribute(ZclOtaUpgradeCluster.ATTR_CURRENTFILEVERSION);
      Object fileVersion = attribute.readValue(Long.MAX_VALUE);
      if (fileVersion != null) {
        String firmwareVersion = String.format("0x%08X", fileVersion);
        if (!Objects.equals(getFirmwareVersion(), firmwareVersion)) {
        this.setFirmwareVersion(firmwareVersion);
          return true;
        }
      } else {
        log.debug("[{}]: Could not get OTA firmware version from device {}", getEntityID(), node.getIeeeAddress());
      }
    } else {
      log.debug("[{}]: Node doesn't support OTA cluster {}", getEntityID(), node.getIeeeAddress());
    }
    return false;
  }

  private IeeeAddress toIeeeAddress() {
    return new IeeeAddress(getIeeeAddress());
  }

  @Override
  public @NotNull Class<ZigBeeDeviceService> getEntityServiceItemClass() {
    return ZigBeeDeviceService.class;
  }

  @Override
  public ZigBeeDeviceService createService(@NotNull EntityContext entityContext) {
    ZigBeeCoordinatorService coordinatorService = parent.getOrCreateService(entityContext)
        .orElseThrow(() -> new RuntimeException("Unable to create zigbee discovery service"));
    return new ZigBeeDeviceService(coordinatorService, toIeeeAddress(), entityContext, this);
  }

  @Override
  public void logBuilder(EntityLogBuilder entityLogBuilder) {
    entityLogBuilder.addTopic("org.touchhome.bundle.zigbee", "entityID");
    entityLogBuilder.addTopic(ZigBeeNodeServiceDiscoverer.class, "ieeeAddress");
  }

  @Override
  public void getAllRelatedEntities(Set<BaseEntity> set) {
    set.addAll(getEndpoints());
  }
}
