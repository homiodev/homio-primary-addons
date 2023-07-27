package org.homio.addon.z2m.model;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.homio.addon.z2m.service.Z2MEndpoint.PROPERTY_FIRMWARE_UPDATE;
import static org.homio.api.model.DeviceDefinitionModel.getDeviceIcon;
import static org.homio.api.model.DeviceDefinitionModel.getDeviceIconColor;
import static org.homio.api.ui.UI.Color.ERROR_DIALOG;
import static org.homio.api.ui.field.UIFieldType.HTML;
import static org.homio.api.ui.field.UIFieldType.SelectBox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.properties.Z2MEndpointFirmwareUpdate;
import org.homio.addon.z2m.setting.ZigBeeEntityCompactModeSetting;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.Z2MPropertyConfigService;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.EntityContext;
import org.homio.api.entity.BaseEntity;
import org.homio.api.entity.DeviceBaseEntity;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.DeviceDefinitionModel;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.model.Status.EntityStatus;
import org.homio.api.model.endpoint.DeviceEndpoint;
import org.homio.api.model.endpoint.DeviceEndpointUI;
import org.homio.api.optionProvider.SelectPlaceOptionLoader;
import org.homio.api.ui.UI;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.action.UIActionHandler;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldIgnore;
import org.homio.api.ui.field.UIFieldInlineEditConfirm;
import org.homio.api.ui.field.UIFieldSlider;
import org.homio.api.ui.field.action.HasDynamicContextMenuActions;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.field.action.v1.layout.UIFlexLayoutBuilder;
import org.homio.api.ui.field.color.UIFieldColorBgRef;
import org.homio.api.ui.field.color.UIFieldColorStatusMatch;
import org.homio.api.ui.field.condition.UIFieldShowOnCondition;
import org.homio.api.ui.field.model.HrefModel;
import org.homio.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.homio.api.ui.field.selection.UIFieldSelection;
import org.homio.api.widget.template.WidgetDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Log4j2
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public final class Z2MDeviceEntity extends ZigBeeDeviceBaseEntity<Z2MDeviceEntity>
    implements HasDynamicContextMenuActions {

    @JsonIgnore private transient Z2MDeviceService deviceService;
    @JsonIgnore private transient Z2MPropertyConfigService configService;

    public Z2MDeviceEntity(Z2MDeviceService deviceService, String ieeeAddress) {
        super();
        this.deviceService = deviceService;
        this.configService = deviceService.getConfigService();

        setEntityID(getEntityPrefix() + ieeeAddress);
        setIeeeAddress(ieeeAddress);
    }

    @Override
    public @NotNull String getDeviceFullName() {
        return deviceService.getDeviceFullName();
    }

    @Override
    @JsonIgnore
    @UIFieldIgnore
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    public Z2MDeviceEntity setName(String value) {
        deviceService.sendRequest(
            "device/rename", new JSONObject().put("from", getName()).put("to", value).toString());
        return this;
    }

    @UIField(order = 10, disableEdit = true, hideInEdit = true, hideOnEmpty = true)
    @UIFieldColorStatusMatch
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup(value = "STATUS", order = 3, borderColor = "#7ACC2D")
    public @NotNull Status getStatus() {
        String availability = deviceService.getAvailability();
        Status status = Status.UNKNOWN;
        if (availability != null) {
            switch (availability) {
                case "offline" -> status = Status.OFFLINE;
                case "online" -> status = Status.ONLINE;
            }
        }
        if (deviceService.getCoordinatorEntity().getStatus() != Status.ONLINE) {
            status = deviceService.getCoordinatorEntity().getStatus();
        }
        return status;
    }

    @Override
    public EntityStatus getEntityStatus() {
        Status status = getStatus();
        ApplianceModel applianceModel = deviceService.getApplianceModel();
        if (applianceModel.isDisabled()) {
            status = Status.DISABLED;
        } else if (applianceModel.isInterviewing()) {
            status = Status.INITIALIZE;
        } else if (deviceService.getApplianceModel().isInterviewFailed()) {
            status = Status.ERROR;
        } else if (!applianceModel.isInterviewCompleted() || !applianceModel.isSupported() ||
            (StringUtils.isEmpty(applianceModel.getType()) || "UNKNOWN".equalsIgnoreCase(applianceModel.getType()))) {
            status = Status.NOT_READY;
        } else {
            DeviceEndpoint property = getDeviceEndpoint(PROPERTY_FIRMWARE_UPDATE);
            if (property instanceof Z2MEndpointFirmwareUpdate firmwareUpdate && firmwareUpdate.isUpdating()) {
                status = Status.UPDATING;
            }
        }
        return new EntityStatus(status);
    }

    @Override
    public @NotNull Icon getEntityIcon() {
        return new Icon(
            getDeviceIcon(deviceService.findDevices(), "fas fa-server"),
            getDeviceIconColor(deviceService.findDevices(), UI.Color.random())
        );
    }

    @Override
    public @NotNull Map<String, DeviceEndpoint> getDeviceEndpoints() {
        return deviceService.getEndpoints()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @UIField(order = 1, hideOnEmpty = true, fullWidth = true, color = "#89AA50", type = HTML)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldColorBgRef(value = "statusColor", animate = true)
    @UIFieldGroup(value = "NAME", order = 1, borderColor = "#CDD649")
    public String getDescription() {
        return defaultIfEmpty(
            getFirstLevelDescription(),
            deviceService.getApplianceModel().getDefinition().getDescription()
        );
    }

    @Override
    @UIField(order = 2, hideOnEmpty = true, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("NAME")
    public @NotNull String getName() {
        return defaultIfEmpty(deviceService.getApplianceModel().getName(), "UNKNOWN");
    }

    @UIField(order = 3, label = "model")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("NAME")
    public HrefModel getHrefModel() {
        String model = getModel();
        return new HrefModel("https://www.zigbee2mqtt.io/devices/%s.html".formatted(model), model);
    }

    @JsonIgnore
    public @NotNull String getModel() {
        return StringUtils.defaultString(deviceService.getModel(), "unknown-model");
    }

    @UIField(order = 1, fullWidth = true, color = "#89AA50", type = HTML, style = "height: 32px;")
    @UIFieldShowOnCondition("return context.get('compactMode')")
    @UIFieldColorBgRef(value = "statusColor", animate = true)
    @UIFieldGroup(value = "NAME", order = 1, borderColor = "#CDD649")
    public String getCompactDescription() {
        String description = getFirstLevelDescription();
        if (description == null) {
            description = "ZIGBEE.DESCRIPTION.%s~%s".formatted(getModel(), deviceService.getApplianceModel().getDefinition().getDescription());
        }
        return """
            <div class="inline-2row_d"><div>%s <span style="color:%s">${%s}</span>
            <span style="float:right" class="color-primary">%s</span></div><div>${%s}</div></div>""".formatted(
            getIeeeAddressLabel(), getStatus().getColor(), getStatus(), trimToEmpty(getModel()), description);
    }

    @UIField(order = 3, disableEdit = true, label = "ieeeAddress")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup(value = "GENERAL", order = 5)
    public String getIeeeAddressLabel() {
        return trimToEmpty(getIeeeAddress()).toUpperCase();
    }

    @UIField(order = 5)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("GENERAL")
    public String getNetworkAddress() {
        return "0x" + Integer.toHexString(deviceService.getApplianceModel().getNetworkAddress()).toUpperCase();
    }

    @UIField(order = 15)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("GENERAL")
    public String getModelIdentifier() {
        return deviceService.getApplianceModel().getModelId();
    }

    @UIField(order = 20, hideOnEmpty = true)
    @UIFieldGroup("GENERAL")
    public String getCurrentPowerSource() {
        return isCompactMode() ? null : deviceService.getApplianceModel().getPowerSource();
    }

    @UIField(order = 25)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("GENERAL")
    public String getLogicalType() {
        return deviceService.getApplianceModel().getType();
    }

    @UIField(order = 35)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("GENERAL")
    public boolean getInterviewCompleted() {
        return deviceService.getApplianceModel().isInterviewCompleted();
    }

    @UIField(order = 1, hideOnEmpty = true)
    @UIFieldGroup(value = "HARDWARE", order = 10, borderColor = Color.RED)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getFirmwareVersion() {
        return deviceService.getApplianceModel().getFirmwareVersion();
    }

    @UIField(order = 2, hideOnEmpty = true)
    @UIFieldGroup("HARDWARE")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getFirmwareBuildDate() {
        return deviceService.getApplianceModel().getFirmwareBuildDate();
    }

    @UIField(order = 3)
    @UIFieldGroup("HARDWARE")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public HrefModel getManufacturer() {
        String vendor = deviceService.getApplianceModel().getDefinition().getVendor();
        return new HrefModel("https://www.zigbee2mqtt.io/supported-devices/#v=%s".formatted(vendor), vendor);
    }

    public boolean isCompactMode() {
        return getEntityContext().setting().getValue(ZigBeeEntityCompactModeSetting.class);
    }

    @UIField(order = 1, type = SelectBox, color = "#7FE486", inlineEdit = true)
    @UIFieldSelection(SelectPlaceOptionLoader.class)
    @UIFieldSelectValueOnEmpty(label = "SELECT_PLACE")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup(value = "SETTINGS", order = 30, borderColor = "#22AB84")
    public String getPlace() {
        return deviceService.getConfiguration().path("place").asText();
    }

    @Override
    public DeviceBaseEntity<Z2MDeviceEntity> setPlace(String value) {
        if (!Objects.equals(getPlace(), value)) {
            deviceService.updateConfiguration("place", value);
        }
        return this;
    }

    @UIField(order = 4, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("SETTINGS")
    @UIFieldInlineEditConfirm(value = "W.CONFIRM.Z2M_RETAIN", dialogColor = ERROR_DIALOG)
    public boolean isRetainDeviceMessages() {
        return deviceService.getConfiguration().path("retain").asBoolean(false);
    }

    public void setRetainDeviceMessages(boolean value) {
        deviceService.sendRequest(
            "device/options", new JSONObject().put("id", getIeeeAddress()).put("options", new JSONObject().put("retain", value)).toString());
    }

    @UIField(order = 5, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("SETTINGS")
    @UIFieldInlineEditConfirm(value = "W.CONFIRM.ZIGBEE_DISABLE", dialogColor = ERROR_DIALOG, showCondition = "return context.get('disabled') == true")
    public boolean isDisabled() {
        return deviceService.getConfiguration().path("disabled").asBoolean(deviceService.getApplianceModel().isDisabled());
    }

    public void setDisabled(boolean value) {
        deviceService.sendRequest(
            "device/options", new JSONObject().put("id", getIeeeAddress()).put("options", new JSONObject().put("disabled", value)).toString());
    }

    @UIField(order = 6, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("SETTINGS")
    public boolean isLogEvents() {
        return deviceService.getConfiguration().path("log_event").asBoolean(false);
    }

    public void setLogEvents(boolean value) {
        deviceService.updateConfiguration("log_event", value);
    }

    @UIField(order = 1, inlineEdit = true)
    @UIFieldSlider(min = 0, max = 10, step = 0.5)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldGroup("ADVANCED")
    @UIFieldInlineEditConfirm(value = "W.CONFIRM.Z2M_DEBOUNCE", dialogColor = ERROR_DIALOG)
    public Float getDebounce() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        if (deviceOptions.has("debounce")) {
            return (float) deviceOptions.get("debounce").asDouble();
        }
        return null;
    }

    public void setDebounce(Float value) {
        if (!Objects.equals(getDebounce(), value)) {
            deviceService.sendRequest(
                "device/options", new JSONObject().put("id", getIeeeAddress()).put("options", new JSONObject().put("debounce", value)).toString());
        }
    }

    @Override
    protected String getImageIdentifierImpl() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        return deviceOptions.path("image").asText(getModel()).replaceAll("[/ ]", "_");
    }

    @Override
    public @Nullable String getFallbackImageIdentifier() {
        return "https://www.zigbee2mqtt.io/images/devices/%s.jpg".formatted(getModel());
    }

    public void setImageIdentifier(String value) {
        deviceService.updateConfiguration("image", trimToNull(Objects.equals(value, getModelIdentifier()) ? null : value));
    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "z2m";
    }

    @Override
    public String getDefaultName() {
        return "Z2M";
    }

    @Override
    public boolean isDisableEdit() {
        return true;
    }

    @Override
    public boolean isDisableDelete() {
        return true;
    }

    @Override
    public Boolean isOutdated() {
        DeviceEndpoint property = getDeviceEndpoint(PROPERTY_FIRMWARE_UPDATE);
        if (property instanceof Z2MEndpointFirmwareUpdate firmwareUpdate) {
            return firmwareUpdate.isOutdated();
        }
        return false;
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {
        List<DeviceDefinitionModel> deviceDefinitionModels = deviceService.findDevices();
        List<WidgetDefinition> widgetDefinitions = DeviceDefinitionModel.getDeviceWidgets(deviceDefinitionModels);
        getEntityContext().widget().createTemplateWidgetActions(uiInputBuilder, this, widgetDefinitions);

        uiInputBuilder.addOpenDialogSelectableButton("CUSTOM_DESCRIPTION", new Icon("fas fa-comment"), null, (entityContext, params) -> {
            String description = params.getString("field.description");

            if (!Objects.equals(description, getCustomDescription())) {
                deviceService.updateConfiguration("customDescription", description);
                entityContext.ui().updateItem(this);
            }
            return null;
        }).editDialog(dialogBuilder -> {
            dialogBuilder.setTitle("CONTEXT.ACTION.CUSTOM_DESCRIPTION", new Icon("fas fa-comment"));
            dialogBuilder.addFlex("main", flex ->
                flex.addTextInput("field.description", getCustomDescription(), false));
        });

        Z2MDeviceDefinition definition = deviceService.getApplianceModel().getDefinition();
        if (definition != null) {
            for (Options option : definition.getOptions()) {
                JsonNode deviceConfigurationOptions = deviceService.getConfiguration();
                String flexName = "${z2m.setting.%s~%s}".formatted(option.getName(), ZigBeeUtil.splitNameToReadableFormat(option.getName()));
                switch (option.getType()) {
                    case ApplianceModel.BINARY_TYPE -> buildBinaryTypeAction(uiInputBuilder, option, deviceConfigurationOptions, flexName);
                    case ApplianceModel.NUMBER_TYPE -> buildNumberTypeAction(uiInputBuilder, option, deviceConfigurationOptions, flexName);
                }
            }
        }
    }

    public @NotNull Date getUpdateTime() {
        return DeviceEndpoint.getLastUpdated(deviceService.getEndpoints().values());
    }

    @Override
    public ActionResponseModel handleAction(EntityContext entityContext, String actionID, JSONObject params) throws Exception {
        for (DeviceEndpointUI endpoint : getEndpoints()) {
            if (actionID.startsWith(endpoint.getEntityID())) {
                UIActionHandler actionHandler = endpoint.getEndpoint().createUIInputBuilder().findActionHandler(actionID);
                if (actionHandler != null) {
                    return actionHandler.handleAction(entityContext, params);
                }
            }
        }
        return super.handleAction(entityContext, actionID, params);
    }

    @Override
    public int compareTo(@NotNull BaseEntity o) {
        if (o instanceof Z2MDeviceEntity other) {
            return ((getStatus().isOnline() ? 0 : 1) + getName()).compareTo((other.getStatus().isOnline() ? 0 : 1) + o.getName());
        }
        return super.compareTo(o);
    }

    private static Float toFloat(Integer value) {
        return value == null ? null : value.floatValue();
    }

    private void buildNumberTypeAction(UIInputBuilder uiInputBuilder, Options option, JsonNode deviceConfigurationOptions, String flexName) {
        JsonNode deviceOptions = DeviceDefinitionModel.getDeviceOptions(deviceService.findDevices());
        Integer minValue = option.getValueMin() == null ? (deviceOptions.has("min") ? deviceOptions.get("min").asInt() : null) : option.getValueMin();
        Integer maxValue = option.getValueMax() == null ? (deviceOptions.has("max") ? deviceOptions.get("max").asInt() : null) : option.getValueMax();

        Integer nValue = deviceConfigurationOptions == null ? null : deviceConfigurationOptions.path(option.getName()).asInt(0);
        uiInputBuilder.addFlex(option.getName() + "_flex", flex -> {
                          flex.addNumberInput(option.getName(), toFloat(nValue), toFloat(minValue), toFloat(maxValue),
                              (entityContext, params) -> {
                                  this.deviceService.updateDeviceConfiguration(deviceService, option.getName(),
                                      params.has("value") ? params.getInt("value") : null);
                                  return null;
                              });
                          flex.addInfo(option.getDescription()).setOuterClass("context-description");
                      })
                      .columnFlexDirection()
                      .setBorderColor(Color.random())
                      .setBorderArea(flexName);
    }

    private void buildBinaryTypeAction(UIInputBuilder uiInputBuilder, Options option, JsonNode deviceConfigurationOptions, String flexName) {
        boolean defaultValue = StringUtils.defaultString(option.getDescription(), "").contains("(default true)");
        boolean bValue = deviceConfigurationOptions == null ? defaultValue : deviceConfigurationOptions.path(option.getName()).asBoolean(defaultValue);

        uiInputBuilder.addFlex(option.getName() + "_flex", flex -> buildCheckbox(option, bValue, flex))
                      .columnFlexDirection()
                      .setBorderColor(Color.random())
                      .setBorderArea(flexName);
    }

    private void buildCheckbox(Options option, boolean bValue, UIFlexLayoutBuilder flex) {
        flex.addCheckbox(option.getName(), bValue, (entityContext, params) -> {
            this.deviceService.updateDeviceConfiguration(deviceService, option.getName(), params.getBoolean("value"));
            return null;
        });
        flex.addInfo(option.getDescription()).setOuterClass("context-description");
    }

    private String getCustomDescription() {
        return deviceService.getConfiguration().path("customDescription").asText();
    }

    private String getFirstLevelDescription() {
        ApplianceModel applianceModel = deviceService.getApplianceModel();
        if (applianceModel.isInterviewing()) {
            return "ZIGBEE.INTERVIEWING";
        }
        if (applianceModel.isInterviewFailed()) {
            return "ZIGBEE.INTERVIEW_FAILED";
        }
        return defaultIfEmpty(
            getCustomDescription(),
            applianceModel.getDefinition().getDescription()
        );
    }
}
