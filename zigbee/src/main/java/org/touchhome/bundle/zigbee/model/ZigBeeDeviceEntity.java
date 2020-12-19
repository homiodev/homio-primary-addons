package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.*;
import org.touchhome.bundle.api.ui.field.action.UIContextMenuAction;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.method.UIFieldCreateWorkspaceVariableOnEmpty;
import org.touchhome.bundle.zigbee.*;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterEndpoint;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoint;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;
import org.touchhome.bundle.zigbee.setting.ZigBeeCoordinatorHandlerSetting;
import org.touchhome.bundle.zigbee.setting.ZigBeeDiscoveryDurationSetting;
import org.touchhome.bundle.zigbee.setting.ZigBeeStatusSetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, bg = "#de9ed7", order = 5)
public final class ZigBeeDeviceEntity extends DeviceBaseEntity<ZigBeeDeviceEntity> {

    public static final String PREFIX = "zb_";
    @Transient
    @UIField(readOnly = true, order = 100)
    @UIFieldCodeEditor(editorType = UIFieldCodeEditor.CodeEditorType.json, autoFormat = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZigBeeNodeDescription zigBeeNodeDescription;

    @Transient
    @JsonIgnore
    private ZigBeeDevice zigBeeDevice;

    @Transient
    @UIField(order = 40, type = UIFieldType.Selection, readOnly = true, color = "#7FBBCC")
    @UIFieldExpand
    @UIFieldCreateWorkspaceVariableOnEmpty
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Map<OptionModel, String>> availableLinks;

    // The minimum time period in seconds between device state updates
    @UIField(onlyEdit = true, order = 100)
    @UIFieldNumber(min = 1, max = 86400)
    public Integer getReportingTimeMin() {
        return getJsonData("reportingTimeMin", 1);
    }

    // The maximum time period in seconds between device state updates
    @UIField(onlyEdit = true, order = 101)
    @UIFieldNumber(min = 1, max = 86400)
    public Integer getReportingTimeMax() {
        return getJsonData("reportingTimeMax", 900);
    }

    // The time period in seconds between subsequent polls
    @UIField(onlyEdit = true, order = 103)
    @UIFieldNumber(min = 15, max = 86400)
    public Integer getPoolingPeriod() {
        return getJsonData("poolingPeriod", 900);
    }

    @JsonIgnore
    public int getNetworkAddress() {
        return getJsonData("networkAddress", 0);
    }

    public ZigBeeDeviceEntity setNetworkAddress(Integer networkAddress) {
        setJsonData("networkAddress", networkAddress);
        return this;
    }

    @UIField(order = 50, type = UIFieldType.TextSelectBoxDynamic)
    @UIFieldSelection(SelectModelIdentifierDynamicLoader.class)
    @UIFieldSelectValueOnEmpty(label = "zigbee.action.selectModelIdentifier", color = "#A7D21E")
    public String getModelIdentifier() {
        return getJsonData("modelIdentifier");
    }

    ZigBeeDeviceEntity setModelIdentifier(String modelIdentifier) {
        setJsonData("modelIdentifier", modelIdentifier);
        tryEvaluateImageIdentifier();

        if (this.getTitle().equals(this.getIeeeAddress())) {
            Optional<ZigBeeRequireEndpoint> zigbeeRequireEndpoint = ZigBeeRequireEndpoints.get().getZigBeeRequireEndpoint(modelIdentifier);
            if (zigbeeRequireEndpoint.isPresent()) {
                String describeName = Lang.findPathText(zigbeeRequireEndpoint.get().getName());
                if (describeName != null) {
                    setName(describeName + "(" + getIeeeAddress() + ")");
                }
            }
        }
        return this;
    }

    public String getImageIdentifier() {
        return getJsonData("imageIdentifier");
    }

    public void setImageIdentifier(String imageIdentifier) {
        setJsonData("imageIdentifier", imageIdentifier);
    }

    @UIContextMenuAction("ACTION.INITIALIZE_ZIGBEE_NODE")
    public ActionResponseModel initializeZigBeeNode() {
        zigBeeDevice.initialiseZigBeeNode();
        return ActionResponseModel.showSuccess("ACTION.RESPONSE.NODE_INITIALIZATION_STARTED");
    }

    @UIContextMenuAction("ACTION.SHOW_LAST_VALUES")
    public ActionResponseModel showLastValues(ZigBeeDeviceEntity zigBeeDeviceEntity, ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
        return ActionResponseModel.showJson(zigBeeDeviceUpdateValueListener.getDeviceStates(zigBeeDeviceEntity.getIeeeAddress()));
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
    public ActionResponseModel permitJoin(EntityContext entityContext) {
        if (!entityContext.setting().getValue(ZigBeeStatusSetting.class).isOnline()) {
            throw new IllegalStateException("DEVICE_OFFLINE");
        }
        if (zigBeeDevice == null) {
            throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
        }
        ZigBeeCoordinatorHandler zigBeeCoordinatorHandler = entityContext.setting().getValue(ZigBeeCoordinatorHandlerSetting.class);
        boolean join = zigBeeCoordinatorHandler.permitJoin(zigBeeDevice.getNodeIeeeAddress(), entityContext.setting().getValue(ZigBeeDiscoveryDurationSetting.class));
        return join ? ActionResponseModel.showSuccess("ACTION.RESPONSE.STARTED") : ActionResponseModel.showError("ACTION.RESPONSE.ERROR");
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
            setJsonData("modelIdentifier", property == null ? null : property.getModelId());
        }
    }

    private void tryEvaluateImageIdentifier() {
        String modelIdentifier = getModelIdentifier();
        if (this.getImageIdentifier() == null && modelIdentifier != null) {
            this.setImageIdentifier(ZigBeeRequireEndpoints.get().getImage(modelIdentifier));
        }
    }

    @Override
    public void afterFetch(EntityContext entityContext) {
        super.afterFetch(entityContext);

        ZigBeeBundleEntryPoint zigbeeBundleContext = entityContext.getBean(ZigBeeBundleEntryPoint.class);
        ZigBeeDevice device = zigbeeBundleContext.getCoordinatorHandler().getZigBeeDevices().get(getIeeeAddress());
        setZigBeeDevice(device);

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
        List<Map<OptionModel, String>> links = new ArrayList<>();
        ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener = entityContext.getBean(ZigBeeDeviceUpdateValueListener.class);

        for (Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter> availableLinkEntry : gatherAvailableLinks()) {
            ZigBeeConverterEndpoint converterEndpoint = availableLinkEntry.getKey();
            ZigBeeDeviceStateUUID uuid = converterEndpoint.toUUID();

            Map<OptionModel, String> map = new HashMap<>();
            DeviceChannelLinkType deviceChannelLinkType = availableLinkEntry.getKey().getZigBeeConverter().linkType();

            ZigBeeDeviceUpdateValueListener.LinkDescription linkDescription = zigBeeDeviceUpdateValueListener.getLinkListeners().get(uuid);

            if (linkDescription != null) {
                BaseEntity variableEntity = entityContext.getEntity(deviceChannelLinkType.getEntityPrefix() + linkDescription.getVarId());
                if (variableEntity != null) {
                    map.put(OptionModel.key(linkDescription.getDescription()), variableEntity.getTitle());
                }
            } else {
                String name = defaultIfEmpty(availableLinkEntry.getValue().getDescription(), converterEndpoint.getClusterDescription());
                map.put(OptionModel.of(uuid.asKey(), name), "");
            }
            links.add(map);
        }
        entity.setAvailableLinks(links);
    }

    public final List<Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter>> gatherAvailableLinks() {
        return zigBeeDevice == null ? Collections.emptyList() : zigBeeDevice.getZigBeeConverterEndpoints().entrySet()
                .stream().filter(c -> c.getKey().getZigBeeConverter().linkType() != DeviceChannelLinkType.None)
                .collect(Collectors.toList());
    }
}
