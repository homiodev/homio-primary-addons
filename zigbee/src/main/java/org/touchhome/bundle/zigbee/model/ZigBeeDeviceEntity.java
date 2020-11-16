package org.touchhome.bundle.zigbee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.manager.En;
import org.touchhome.bundle.api.measure.State;
import org.touchhome.bundle.api.model.DeviceBaseEntity;
import org.touchhome.bundle.api.ui.UISidebarMenu;
import org.touchhome.bundle.api.ui.field.*;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.ui.method.UIFieldCreateWorkspaceVariableOnEmpty;
import org.touchhome.bundle.api.ui.method.UIMethodAction;
import org.touchhome.bundle.zigbee.*;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigbeeRequireEndpoint;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigbeeRequireEndpoints;
import org.touchhome.bundle.zigbee.setting.ZigbeeCoordinatorHandlerSetting;
import org.touchhome.bundle.zigbee.setting.ZigbeeDiscoveryDurationSetting;
import org.touchhome.bundle.zigbee.setting.ZigbeeStatusSetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarMenu(icon = "fas fa-bezier-curve", parent = UISidebarMenu.TopSidebarMenu.HARDWARE, bg = "#de9ed7", order = 5)
public class ZigBeeDeviceEntity extends DeviceBaseEntity<ZigBeeDeviceEntity> {

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
    private List<Map<Option, String>> availableLinks;

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
            Optional<ZigbeeRequireEndpoint> zigbeeRequireEndpoint = ZigbeeRequireEndpoints.get().getZigbeeRequireEndpoint(modelIdentifier);
            if (zigbeeRequireEndpoint.isPresent()) {
                String describeName = En.findPathText(zigbeeRequireEndpoint.get().getName());
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

    @UIMethodAction("ACTION.INITIALIZE_ZIGBEE_NODE")
    public String initializeZigbeeNode() {
        zigBeeDevice.initialiseZigBeeNode();
        return "ACTION.RESPONSE.NODE_INITIALIZATION_STARTED";
    }

    @UIMethodAction(value = "ACTION.SHOW_LAST_VALUES", responseAction = UIMethodAction.ResponseAction.ShowJson)
    public Map<ZigBeeDeviceStateUUID, State> showLastValues(ZigBeeDeviceEntity zigBeeDeviceEntity, ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
        return zigBeeDeviceUpdateValueListener.getDeviceStates(zigBeeDeviceEntity.getIeeeAddress());
    }

    @UIMethodAction("ACTION.REDISCOVERY")
    public String rediscoveryNode() {
        if (zigBeeDevice == null) {
            throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
        }
        zigBeeDevice.discoveryNodeDescription(this.getModelIdentifier());
        return "ACTION.RESPONSE.REDISCOVERY_STARTED";
    }

    @UIMethodAction("ACTION.ZIGBEE_PULL_CHANNELS")
    public String pullChannels() {
        if (zigBeeDevice == null) {
            throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
        }
        new Thread(zigBeeDevice.getPoolingThread()).start();
        return "ACTION.RESPONSE.ZIGBEE_PULL_CHANNELS_STARTED";
    }

    @UIMethodAction("ACTION.PERMIT_JOIN")
    public String permitJoin(EntityContext entityContext) {
        if (!entityContext.setting().getValue(ZigbeeStatusSetting.class).isOnline()) {
            throw new IllegalStateException("DEVICE_OFFLINE");
        }
        if (zigBeeDevice == null) {
            throw new IllegalStateException("Unable to find zigbee node with ieeeAddress: " + getIeeeAddress());
        }
        ZigBeeCoordinatorHandler zigBeeCoordinatorHandler = entityContext.setting().getValue(ZigbeeCoordinatorHandlerSetting.class);
        boolean join = zigBeeCoordinatorHandler.permitJoin(zigBeeDevice.getNodeIeeeAddress(), entityContext.setting().getValue(ZigbeeDiscoveryDurationSetting.class));
        return join ? "ACTION.RESPONSE.STARTED" : "ACTION.RESPONSE.ERROR";
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
            ZigbeeRequireEndpoint property = ZigbeeRequireEndpoints.get().findByNode(zigBeeNodeDescription);
            setJsonData("modelIdentifier", property == null ? null : property.getModelId());
        }
    }

    private void tryEvaluateImageIdentifier() {
        String modelIdentifier = getModelIdentifier();
        if (this.getImageIdentifier() == null && modelIdentifier != null) {
            this.setImageIdentifier(ZigbeeRequireEndpoints.get().getImage(modelIdentifier));
        }
    }
}
