package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.zdo.field.NodeDescriptor.LogicalType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.converter.JSONObjectConverter;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.validation.MaxItems;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService;
import org.touchhome.bundle.api.service.EntityService.ServiceIdentifier;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldCodeEditor;
import org.touchhome.bundle.api.ui.field.UIFieldInlineEntity;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.zigbee.SelectModelIdentifierDynamicLoader;
import org.touchhome.bundle.zigbee.ZigBeeCoordinatorHandler;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.ZigBeeNodeDescription;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.service.ZigbeeEndpointService;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;
import org.touchhome.common.exception.NotFoundException;
import org.touchhome.common.util.Lang;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, bg = "#de9ed7",
    order = 5, overridePath = "zigbee")
public final class ZigBeeDeviceEntity extends BaseEntity<ZigBeeDeviceEntity> implements
    HasJsonData, HasStatusAndMsg<ZigBeeDeviceEntity> {

  public static final String PREFIX = "zb_";

  @UIField(readOnly = true, order = 5)
  private String ieeeAddress;

  @UIField(order = 11, readOnly = true, hideOnEmpty = true)
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

  @Lob
  @Column(length = 10_000) // 10kb
  @Convert(converter = JSONObjectConverter.class)
  private JSONObject jsonData = new JSONObject();

  @ManyToOne
  private ZigbeeCoordinatorEntity coordinatorEntity;

  @UIField(order = 50)
  @UIFieldSelection(value = SelectModelIdentifierDynamicLoader.class, allowInputRawText = true)
  @UIFieldSelectValueOnEmpty(label = "zigbee.action.selectModelIdentifier", color = "#A7D21E")
  private String model;

  private String image;

  @Transient
  @UIField(readOnly = true, order = 100)
  @UIFieldCodeEditor(editorType = UIFieldCodeEditor.CodeEditorType.json, autoFormat = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private ZigBeeNodeDescription zigBeeNodeDescription;

  @Transient
  @JsonIgnore
  private ZigBeeDevice zigBeeDevice;

  public ZigBeeDeviceEntity setModel(String modelIdentifier) {
    this.model = modelIdentifier;
    tryEvaluateImageIdentifier();
    updateNameAndDescription(ZigBeeRequireEndpoints.getDeviceDefinitionsByModel(modelIdentifier).stream().findAny());
    return this;
  }

  private void updateNameAndDescription(Optional<DeviceDefinition> zigbeeRequireEndpoint) {
    if (zigbeeRequireEndpoint.isPresent()) {
      String label = zigbeeRequireEndpoint.get().getLabel(Lang.CURRENT_LANG);
      if (label != null) {
        setName(label);
      }
      String description = zigbeeRequireEndpoint.get().getDescription(Lang.CURRENT_LANG);
      if (description != null) {
        setDescription(description);
      }
    }
  }

  @UIContextMenuAction("ACTION.INITIALIZE_ZIGBEE_NODE")
  public ActionResponseModel initializeZigBeeNode() {
    zigBeeDevice.initialiseZigBeeNode();
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.NODE_INITIALIZATION_STARTED");
  }

  @UIContextMenuAction("ACTION.REDISCOVERY")
  public ActionResponseModel rediscoveryNode() {
    if (zigBeeDevice == null) {
      throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
    }
    zigBeeDevice.discoveryNodeDescription(this.getModel());
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.REDISCOVERY_STARTED");
  }

  @UIContextMenuAction("ACTION.ZIGBEE_PULL_CHANNELS")
  public ActionResponseModel pullChannels() {
    if (zigBeeDevice == null) {
      throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
    }
    new Thread(zigBeeDevice.createPoolingThread()).start();
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.ZIGBEE_PULL_CHANNELS_STARTED");
  }

  @UIContextMenuAction("ACTION.PERMIT_JOIN")
  public ActionResponseModel permitJoin() {
    if (coordinatorEntity.getStatus() != Status.ONLINE) {
      throw new IllegalStateException("DEVICE_OFFLINE");
    }
    if (zigBeeDevice == null) {
      throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
    }
    ZigBeeCoordinatorHandler zigBeeCoordinatorHandler = coordinatorEntity.getService();
    boolean join = zigBeeCoordinatorHandler.permitJoin(zigBeeDevice.getNodeIeeeAddress(),
        coordinatorEntity.getDiscoveryDuration());
    return join ? ActionResponseModel.showSuccess("ACTION.RESPONSE.STARTED") :
        ActionResponseModel.showError("ACTION.RESPONSE.ERROR");
  }

  @Override
  public String toString() {
    return "ZigBee [modelIdentifier='" + getModel() + "]. IeeeAddress-" + getIeeeAddress() + ". Name";
  }

  void setZigBeeNodeDescription(ZigBeeNodeDescription zigBeeNodeDescription) {
    this.zigBeeNodeDescription = zigBeeNodeDescription;
    setStatus(zigBeeNodeDescription.getDeviceStatus());
    tryEvaluateModelDescription(zigBeeNodeDescription);
    tryEvaluateImageIdentifier();
  }

  // TODO:?????????????????
  private void tryEvaluateModelDescription(ZigBeeNodeDescription zigBeeNodeDescription) {
   /* if (zigBeeNodeDescription != null && zigBeeNodeDescription.getChannels() != null && this.getModel() == null) {
      DeviceDefinition property = ZigBeeRequireEndpoints.findByNode(zigBeeNodeDescription);
      this.model = property == null ? null : property.getModelId();
    }*/
  }

  private void tryEvaluateImageIdentifier() {
    String modelIdentifier = getModel();
    if (this.getImage() == null && modelIdentifier != null) {
      this.setImage(ZigBeeRequireEndpoints.getDeviceDefinitionImage(modelIdentifier));
    }
  }

  @Override
  public void afterDelete(EntityContext entityContext) {
    ZigBeeCoordinatorHandler service = coordinatorEntity.getService();
    service.removeNode(new IeeeAddress(getIeeeAddress()));
    service.leave(new IeeeAddress(getIeeeAddress()), true);
  }

  @Override
  public void afterFetch(EntityContext entityContext) {
    super.afterFetch(entityContext);

    ZigBeeDevice device = coordinatorEntity.getService().getZigBeeDevices().get(getIeeeAddress());
    this.zigBeeDevice = device;

    if (device != null) {
      ZigBeeNodeDescription zigBeeNodeDescription = device.getZigBeeNodeDescription();
      if (getModel() == null && zigBeeNodeDescription.getModelIdentifier() != null) {
        entityContext.save(setModel(zigBeeNodeDescription.getModelIdentifier()));
      } else if (getModel() != null && zigBeeNodeDescription.getModelIdentifier() == null) {
        zigBeeNodeDescription.setModelIdentifier(getModel());
      }

      setZigBeeNodeDescription(zigBeeNodeDescription);
    }
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
    ZigBeeCoordinatorHandler coordinatorHandler = zigBeeDevice.getDiscoveryService().getCoordinatorHandler();
    int localEndpointId = coordinatorHandler.getLocalEndpointId(ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION);
    IeeeAddress localIeeeAddress = coordinatorHandler.getLocalIeeeAddress();

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

      ZigbeeEndpointService endpointService = new ZigbeeEndpointService(entityContext, cluster, zigBeeDevice,
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

  public List<ZigBeeEndpointEntity> getEndpoints(int clusterId) {
    return endpoints.stream().filter(e -> e.getClusterId() == clusterId).collect(Collectors.toList());
  }
}
