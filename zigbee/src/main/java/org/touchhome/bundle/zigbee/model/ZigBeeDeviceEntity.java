package org.touchhome.bundle.zigbee.model;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zsmartsystems.zigbee.IeeeAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.converter.JSONObjectConverter;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldCodeEditor;
import org.touchhome.bundle.api.ui.field.UIFieldExpand;
import org.touchhome.bundle.api.ui.field.UIFieldNumber;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.method.UIFieldCreateWorkspaceVariableOnEmpty;
import org.touchhome.bundle.zigbee.SelectModelIdentifierDynamicLoader;
import org.touchhome.bundle.zigbee.ZigBeeCoordinatorHandler;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.ZigBeeDeviceStateUUID;
import org.touchhome.bundle.zigbee.ZigBeeNodeDescription;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterEndpoint;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoint;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;
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

  @UIField(readOnly = true, order = 5, hideOnEmpty = true)
  private String ieeeAddress;

  // The minimum time period in seconds between device state updates
  @UIField(onlyEdit = true, order = 100)
  @UIFieldNumber(min = 1, max = 86400)
  private int reportingTimeMin = 1;

  // The maximum time period in seconds between device state updates
  @UIField(onlyEdit = true, order = 101)
  @UIFieldNumber(min = 1, max = 86400)
  private int reportingTimeMax = 900;

  @UIField(onlyEdit = true, order = 102)
  @UIFieldNumber(min = 1, max = 86400)
  private int reportingChange = 10;

  // The time period in seconds between subsequent polls
  @UIField(onlyEdit = true, order = 103)
  @UIFieldNumber(min = 15, max = 86400)
  private int poolingPeriod = 900;

  @JsonIgnore
  private int networkAddress = 0;

  @Lob
  @Column(length = 10000) // 10kb
  @Convert(converter = JSONObjectConverter.class)
  private JSONObject jsonData = new JSONObject();

  @Transient
  @UIField(readOnly = true, order = 100)
  @UIFieldCodeEditor(editorType = UIFieldCodeEditor.CodeEditorType.json, autoFormat = true)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private ZigBeeNodeDescription zigBeeNodeDescription;

  @ManyToOne
  private ZigbeeCoordinatorEntity coordinatorEntity;

  @Transient
  @JsonIgnore
  private ZigBeeDevice zigBeeDevice;

  @Transient
  @UIField(order = 1000, type = UIFieldType.SelectBox, readOnly = true, color = "#7FBBCC")
  @UIFieldExpand
  @UIFieldCreateWorkspaceVariableOnEmpty
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<AvailableLink> availableLinks;

  @UIField(order = 50)
  @UIFieldSelection(value = SelectModelIdentifierDynamicLoader.class, allowInputRawText = true)
  @UIFieldSelectValueOnEmpty(label = "zigbee.action.selectModelIdentifier", color = "#A7D21E")
  private String modelIdentifier;

  private String imageIdentifier;


  public ZigBeeDeviceEntity setModelIdentifier(String modelIdentifier) {
    this.modelIdentifier = modelIdentifier;
    tryEvaluateImageIdentifier();

    if (this.getTitle().equals(this.getIeeeAddress())) {
      Optional<ZigBeeRequireEndpoint> zigbeeRequireEndpoint =
          ZigBeeRequireEndpoints.get().getZigBeeRequireEndpoint(modelIdentifier);
      if (zigbeeRequireEndpoint.isPresent()) {
        String describeName = Lang.findPathText(zigbeeRequireEndpoint.get().getName());
        if (describeName != null) {
          setName(describeName + "(" + getIeeeAddress() + ")");
        }
      }
    }
    return this;
  }

  @UIContextMenuAction("ACTION.INITIALIZE_ZIGBEE_NODE")
  public ActionResponseModel initializeZigBeeNode() {
    zigBeeDevice.initialiseZigBeeNode();
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.NODE_INITIALIZATION_STARTED");
  }

  @UIContextMenuAction("ACTION.SHOW_LAST_VALUES")
  public ActionResponseModel showLastValues(ZigBeeDeviceEntity zigBeeDeviceEntity,
      ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
    return ActionResponseModel.showJson("Zigbee device values",
        zigBeeDeviceUpdateValueListener.getDeviceStates(zigBeeDeviceEntity.getIeeeAddress()));
  }

  @UIContextMenuAction("ACTION.REDISCOVERY")
  public ActionResponseModel rediscoveryNode() {
    if (zigBeeDevice == null) {
      throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
    }
    zigBeeDevice.discoveryNodeDescription(this.getModelIdentifier());
    return ActionResponseModel.showSuccess("ACTION.RESPONSE.REDISCOVERY_STARTED");
  }

  @UIContextMenuAction("ACTION.ZIGBEE_PULL_CHANNELS")
  public ActionResponseModel pullChannels() {
    if (zigBeeDevice == null) {
      throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
    }
    new Thread(zigBeeDevice.getPoolingThread()).start();
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
    ZigBeeCoordinatorHandler zigBeeCoordinatorHandler = coordinatorEntity.getZigBeeCoordinatorHandler();
    boolean join = zigBeeCoordinatorHandler.permitJoin(zigBeeDevice.getNodeIeeeAddress(),
        coordinatorEntity.getDiscoveryDuration());
    return join ? ActionResponseModel.showSuccess("ACTION.RESPONSE.STARTED") :
        ActionResponseModel.showError("ACTION.RESPONSE.ERROR");
  }

  @Override
  public String toString() {
    return "ZigBee [modelIdentifier='" + getModelIdentifier() + "]. IeeeAddress-" + getIeeeAddress() + ". Name";
  }

  void setZigBeeNodeDescription(ZigBeeNodeDescription zigBeeNodeDescription) {
    this.zigBeeNodeDescription = zigBeeNodeDescription;
    setStatus(zigBeeNodeDescription.getDeviceStatus());
    tryEvaluateModelDescription(zigBeeNodeDescription);
    tryEvaluateImageIdentifier();
  }

  private void tryEvaluateModelDescription(ZigBeeNodeDescription zigBeeNodeDescription) {
    if (zigBeeNodeDescription != null && zigBeeNodeDescription.getChannels() != null && this.getModelIdentifier() == null) {
      ZigBeeRequireEndpoint property = ZigBeeRequireEndpoints.get().findByNode(zigBeeNodeDescription);
      this.modelIdentifier = property == null ? null : property.getModelId();
    }
  }

  private void tryEvaluateImageIdentifier() {
    String modelIdentifier = getModelIdentifier();
    if (this.getImageIdentifier() == null && modelIdentifier != null) {
      this.setImageIdentifier(ZigBeeRequireEndpoints.get().getImage(modelIdentifier));
    }
  }

  @Override
  public void afterDelete(EntityContext entityContext) {
    coordinatorEntity.getZigBeeCoordinatorHandler().removeNode(new IeeeAddress(getIeeeAddress()));
    coordinatorEntity.getZigBeeCoordinatorHandler().leave(new IeeeAddress(getIeeeAddress()), true);
  }

  @Override
  public void afterFetch(EntityContext entityContext) {
    super.afterFetch(entityContext);

    ZigBeeDevice device = coordinatorEntity.getZigBeeCoordinatorHandler().getZigBeeDevices().get(getIeeeAddress());
    this.zigBeeDevice = device;

    if (device != null) {
      ZigBeeNodeDescription zigBeeNodeDescription = device.getZigBeeNodeDescription();
      if (getModelIdentifier() == null && zigBeeNodeDescription.getModelIdentifier() != null) {
        entityContext.save(setModelIdentifier(zigBeeNodeDescription.getModelIdentifier()));
      } else if (getModelIdentifier() != null && zigBeeNodeDescription.getModelIdentifier() == null) {
        zigBeeNodeDescription.setModelIdentifier(getModelIdentifier());
      }

      setZigBeeNodeDescription(zigBeeNodeDescription);
    }

    gatherAvailableLinks(entityContext, this);
  }

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  private void gatherAvailableLinks(EntityContext entityContext, ZigBeeDeviceEntity entity) {
    List<AvailableLink> links = new ArrayList<>();
    ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener =
        entityContext.getBean(ZigBeeDeviceUpdateValueListener.class);

    for (Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter> availableLinkEntry : gatherAvailableLinks()) {
      ZigBeeConverterEndpoint converterEndpoint = availableLinkEntry.getKey();
      ZigBeeDeviceStateUUID uuid = converterEndpoint.toUUID();

      DeviceChannelLinkType deviceChannelLinkType = availableLinkEntry.getKey().getZigBeeConverter().linkType();

      ZigBeeDeviceUpdateValueListener.LinkDescription linkDescription =
          zigBeeDeviceUpdateValueListener.getLinkListeners().get(uuid);

      AvailableLink link = null;
      if (linkDescription != null) {
        String title = entityContext.var().getTitle(linkDescription.getVarId(), null);
        if (title != null) {
          link = new AvailableLink(OptionModel.key(linkDescription.getDescription()), title);
        }
      } else {
        String name =
            defaultIfEmpty(availableLinkEntry.getValue().getDescription(), converterEndpoint.getClusterDescription());
        link = new AvailableLink(OptionModel.of(uuid.asKey(), name), "");
      }
      links.add(link);
    }
    entity.setAvailableLinks(links);
  }

  public List<Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter>> gatherAvailableLinks() {
    return zigBeeDevice == null ? Collections.emptyList() : zigBeeDevice.getZigBeeConverterEndpoints().entrySet()
        .stream().filter(c -> c.getKey().getZigBeeConverter().linkType() != DeviceChannelLinkType.None)
        .collect(Collectors.toList());
  }

  @Getter
  @AllArgsConstructor
  public final class AvailableLink {

    private OptionModel key;
    private String value;
  }
}
