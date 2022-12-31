package org.touchhome.bundle.zigbee.model.z2m;

import static java.lang.String.format;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.BINARY_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.COMPOSITE_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.ENUM_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.NUMBER_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.SWITCH_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.DisableCacheEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.optionProvider.SelectPlaceOptionLoader;
import org.touchhome.bundle.api.ui.UI.Color;
import org.touchhome.bundle.api.ui.action.UIActionHandler;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.UIFieldSlider;
import org.touchhome.bundle.api.ui.field.UIFieldTitleRef;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputEntity;
import org.touchhome.bundle.api.ui.field.action.v1.item.UIColorPickerItemBuilder.ColorType;
import org.touchhome.bundle.api.ui.field.action.v1.item.UIInfoItemBuilder.InfoType;
import org.touchhome.bundle.api.ui.field.action.v1.item.UISelectBoxItemBuilder;
import org.touchhome.bundle.api.ui.field.action.v1.layout.UILayoutBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorBgRef;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.condition.UIFieldShowOnCondition;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceBaseEntity;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;
import org.touchhome.bundle.zigbee.service.z2m.properties.Z2MPropertyColor;
import org.touchhome.bundle.zigbee.service.z2m.properties.Z2MPropertyLastUpdate;
import org.touchhome.bundle.zigbee.setting.ZigBeeEntityCompactModeSetting;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.zigbee.util.ZigBeeUtil;

@Log4j2
@Getter
@Setter
@NoArgsConstructor
@DisableCacheEntity
public class Z2MDeviceEntity extends ZigBeeDeviceBaseEntity<Z2MDeviceEntity>
    implements HasJsonData, HasStatusAndMsg<Z2MDeviceEntity>, HasDynamicContextMenuActions {

    public static final String PREFIX = "z2m_";
    @JsonIgnore private transient Z2MDeviceService deviceService;

    public Z2MDeviceEntity(Z2MDeviceService deviceService) {
        super();
        this.deviceService = deviceService;

        setEntityID(PREFIX + deviceService.getDevice().getIeeeAddress());
        setIeeeAddress(deviceService.getDevice().getIeeeAddress());
    }

    private static Float toFloat(Integer value) {
        return value == null ? null : value.floatValue();
    }

    @Override
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getIeeeAddress() {
        return super.getIeeeAddress();
    }

    @Override
    @UIField(order = 10, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getName() {
        return deviceService.getConfiguration().path("name").asText(deviceService.getDevice().getName());
    }

    public Z2MDeviceEntity setName(String value) {
        deviceService.updateConfiguration("name", value);
        return this;
    }

    @UIField(order = 10)
    @UIFieldColorStatusMatch
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public Status getStatus() {
        String availability = deviceService.getAvailability();
        Status status = Status.UNKNOWN;
        if (availability != null) {
            switch (availability) {
                case "offline":
                    status = Status.OFFLINE;
                    break;
                case "online":
                    status = Status.ONLINE;
                    break;
            }
        }
        if (deviceService.getCoordinatorService().getEntity().getStatus() != Status.ONLINE) {
            status = deviceService.getCoordinatorService().getEntity().getStatus();
        }
        return status;
    }

    @UIField(order = 1, hideOnEmpty = true, fullWidth = true, color = "#89AA50", inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    @UIFieldColorBgRef(value = "statusColor", animate = true)
    public String getDescription() {
        return deviceService.getDevice().getDefinition().getDescription();
    }

    @SuppressWarnings("unused")
    public void setDescription(String value) {
        if (!Objects.equals(getDescription(), value)) {
            deviceService.updateConfiguration("description", value);
        }
    }

    @UIField(order = 1, fullWidth = true, color = "#89AA50", type = UIFieldType.HTML, style = "height: 32px;")
    @UIFieldShowOnCondition("return context.get('compactMode')")
    @UIFieldColorBgRef(value = "statusColor", animate = true)
    public String getCompactDescription() {
        return format("<div class=\"inline-2row_d\">"
                + "<div>%s <span style=\"color:%s\">${%s}</span><span style=\"float:right\" class=\"color-primary\">%s</span>"
                + "</div><div>${%s:%s}</div></div>",
            getIeeeAddress(), getStatus().getColor(), getStatus(), getModel(), getName(), getDescription());
    }

    @UIField(order = 2, type = UIFieldType.SelectBox, color = "#7FE486", inlineEdit = true)
    @UIFieldSelection(SelectPlaceOptionLoader.class)
    @UIFieldSelectValueOnEmpty(label = "SELECT_PLACE")
    @UIFieldShowOnCondition("return !context.get('compactMode')")
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

    @UIField(order = 5)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public int getNetworkAddress() {
        return deviceService.getDevice().getNetworkAddress();
    }

    @UIField(order = 10)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getModel() {
        return deviceService.getDevice().getDefinition().getModel();
    }

    @UIField(order = 15)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getModelIdentifier() {
        return deviceService.getDevice().getModelId();
    }

    @UIField(order = 20, hideOnEmpty = true)
    public String getCurrentPowerSource() {
        return isCompactMode() ? null : deviceService.getDevice().getPowerSource();
    }

    @UIField(order = 25)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getLogicalType() {
        return deviceService.getDevice().getType();
    }

    @UIField(order = 30)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getManufacturer() {
        return deviceService.getDevice().getDefinition().getVendor();
    }

    @UIField(order = 35)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public boolean getInterviewCompleted() {
        return deviceService.getDevice().isInterviewCompleted();
    }

    public boolean isCompactMode() {
        return getEntityContext().setting().getValue(ZigBeeEntityCompactModeSetting.class);
    }

    @UIField(order = 40, inlineEdit = true)
    @UIFieldSlider(min = 0, max = 10, step = 0.5)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public Float getDebounce() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        if (deviceOptions.has("debounce")) {
            return (float) deviceOptions.get("debounce").asDouble();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void setDebounce(Float value) {
        if (!Objects.equals(getDebounce(), value)) {
            deviceService.updateConfiguration("debounce", value);
        }
    }

    @UIField(order = 45, inlineEdit = true, type = UIFieldType.Chips)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public Set<String> getDebounceIgnore() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        if (deviceOptions.has("debounce_ignore")) {
            JsonNode debounceIgnoreList = deviceOptions.get("debounce_ignore");
            Set<String> set = new HashSet<>();
            for (JsonNode jsonNode : debounceIgnoreList) {
                if (StringUtils.isNotEmpty(jsonNode.asText())) {
                    set.add(jsonNode.asText());
                }
            }
            return set;
        }
        return Collections.emptySet();
    }

    public void setDebounceIgnore(List<String> list) {
        deviceService.updateConfiguration("debounce_ignore", list.isEmpty() ? null : list);
    }

    @UIField(order = 50, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public boolean isRetainDeviceMessages() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        return deviceOptions.path("retain").asBoolean(false);
    }

    @SuppressWarnings("unused")
    public void setRetainDeviceMessages(boolean value) {
        deviceService.updateConfiguration("debounce_ignore", value ? true : null);
    }

    @UIField(order = 50, inlineEdit = true)
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public boolean isLogEvents() {
        return deviceService.getConfiguration().path("log").asBoolean(false);
    }

    @SuppressWarnings("unused")
    public void setLogEvents(boolean value) {
        deviceService.updateConfiguration("log", value ? true : null);
    }

    @UIField(order = 60 /*, inlineEdit = true*/) // TODO: fix it later
    @UIFieldShowOnCondition("return !context.get('compactMode')")
    public String getImageIdentifier() {
        JsonNode deviceOptions = deviceService.getConfiguration();
        return deviceOptions.path("image").asText(getModelIdentifier());
    }

    @SuppressWarnings("unused")
    public void setImageIdentifier(String value) {
        deviceService.updateConfiguration(
            "image", Objects.equals(value, getModelIdentifier()) ? null : value);
    }

    @UIField(order = 9999)
    @UIFieldInlineEntities(bg = "#27FF0005")
    @SuppressWarnings("unused")
    public List<Z2MPropertyEntity> getEndpointClusters() {
        return deviceService.getProperties().values().stream()
                            .filter(Z2MProperty::isVisible)
                            .sorted(Comparator.comparing(o -> o.getExpose().getProperty()))
                            .map(Z2MPropertyEntity::new)
                            .collect(Collectors.toList());
    }

    public String getStatusColor() {
        return getStatus().isOnline() ? "" : "#FF000030";
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
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
    public void assembleActions(UIInputBuilder uiInputBuilder) {
        Z2MDeviceDefinition definition = deviceService.getDevice().getDefinition();
        if (definition != null) {
            for (Options option : definition.getOptions()) {
                JsonNode deviceConfigurationOptions = deviceService.getConfiguration();
                String flexName = format("${zigbee.setting.%s:%s}", option.getName(), option.getName());
                switch (option.getType()) {
                    case BINARY_TYPE:
                        buildBinaryTypeAction(uiInputBuilder, option, deviceConfigurationOptions, flexName);
                        break;
                    case NUMBER_TYPE:
                        buildNumberTypeAction(uiInputBuilder, option, deviceConfigurationOptions, flexName);
                        break;
                }
            }
        }
    }

    private void buildNumberTypeAction(UIInputBuilder uiInputBuilder, Options option, JsonNode deviceConfigurationOptions, String flexName) {
        JsonNode deviceOptions =
            ZigBeeUtil.getDeviceOptions(deviceService.getDevice().getModelId());
        Integer minValue = option.getValueMin() == null ? (deviceOptions.has("min") ? deviceOptions.get("min").asInt() : null) : option.getValueMin();
        Integer maxValue = option.getValueMax() == null ? (deviceOptions.has("max") ? deviceOptions.get("max").asInt() : null) : option.getValueMax();

        Integer nValue = deviceConfigurationOptions == null ? null : deviceConfigurationOptions.path(option.getName()).asInt(0);
        uiInputBuilder.addFlex(option.getName() + "_flex", flex -> {
                          flex.addNumberInput(option.getName(), toFloat(nValue), toFloat(minValue), toFloat(maxValue),
                              (entityContext, params) -> {
                                  this.deviceService.getCoordinatorService().updateDeviceConfiguration(deviceService, option.getName(),
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

        uiInputBuilder.addFlex(option.getName() + "_flex", flex -> {
                          flex.addCheckbox(option.getName(), bValue, (entityContext, params) -> {
                              this.deviceService.getCoordinatorService().updateDeviceConfiguration(deviceService, option.getName(), params.getBoolean("value"));
                              return null;
                          });
                          flex.addInfo(option.getDescription()).setOuterClass("context-description");
                      })
                      .columnFlexDirection()
                      .setBorderColor(Color.random())
                      .setBorderArea(flexName);
    }

    @Override
    @UIFieldIgnore
    public Date getCreationTime() {
        return super.getCreationTime();
    }

    @Override
    @UIFieldIgnore
    public Date getUpdateTime() {
        return super.getUpdateTime();
    }

    @Override
    public ActionResponseModel handleAction(EntityContext entityContext, String actionID, JSONObject params) throws Exception {
        for (Z2MPropertyEntity endpointCluster : getEndpointClusters()) {
            if (actionID.startsWith(endpointCluster.getEntityID())) {
                UIActionHandler actionHandler = endpointCluster.buildAction().findActionHandler(actionID);
                if (actionHandler != null) {
                    return actionHandler.handleAction(entityContext, params);
                }
            }
        }
        return HasDynamicContextMenuActions.super.handleAction(entityContext, actionID, params);
    }

    @Getter
    @NoArgsConstructor
    public static class Z2MPropertyEntity {

        private String entityID;

        @UIField(order = 2, type = UIFieldType.HTML)
        private String title;

        @JsonIgnore private Z2MProperty property;
        private String valueTitle;

        public Z2MPropertyEntity(Z2MProperty property) {
            this.entityID = property.getEntityID();
            this.title = format("<div class=\"inline-2row_d\"><div style=\"color:%s;\"><i class=\"mr-1 %s\"></i>%s</div><span>%s</div></div>",
                property.getColor(), property.getIcon(), property.getName(), property.getDescription());
            this.property = property;
            this.valueTitle = property.getValue().toString();
            switch (property.getExpose().getType()) {
                case ENUM_TYPE:
                    this.valueTitle = "Values: " + String.join(", ", getProperty().getExpose().getValues());
                    break;
            }
        }

        @UIField(order = 4, style = "margin-left: auto; margin-right: 8px;")
        @UIFieldInlineEntityWidth(30)
        @UIFieldTitleRef("valueTitle")
        public UIInputEntity getValue() {
            return buildAction().buildAll().iterator().next();
        }

        private @NotNull UIInputBuilder buildAction() {
            UIInputBuilder uiInputBuilder =
                property.getDeviceService().getEntityContext().ui().inputBuilder();
            if (property.isWritable()) {
                switch (property.getExpose().getType()) {
                    case ENUM_TYPE:
                        // corner case for smoke sensor, access=2 send selftest with empty string,
                        return buildWritableEnumTypeAction(uiInputBuilder);
                    case NUMBER_TYPE:
                        if (buildWritableNumberTypeAction(uiInputBuilder)) {
                            return uiInputBuilder;
                        }
                        break;
                    case SWITCH_TYPE:
                    case BINARY_TYPE:
                        uiInputBuilder.addCheckbox(entityID, property.getValue().boolValue(), (entityContext, params) -> {
                            property.fireAction(params.getBoolean("value"));
                            return null;
                        });
                        return uiInputBuilder;
                    case COMPOSITE_TYPE:
                        if (property instanceof Z2MPropertyColor) {
                            uiInputBuilder.addColorPicker(entityID, ((Z2MPropertyColor) property).getStateColor(), (entityContext, params) -> {
                                property.fireAction(params.getString("value"));
                                return null;
                            }).setColorType(ColorType.ColorSlider);
                            return uiInputBuilder;
                        }
                    default:
                        log.error("[{}]: Z2M write handler not implemented for device: {}, property: {}",
                            property.getDeviceService().getCoordinatorService().getEntityID(),
                            property.getDeviceService().getDeviceEntity().getEntityID(),
                            property.getExpose().getProperty());
                }
            }
            if (property.getUnit() != null) {
                uiInputBuilder.addInfo(format("%s <small class=\"text-muted\">%s</small>",
                    property.getValue().stringValue(), property.getUnit()), InfoType.HTML);
            }
            if (Z2MPropertyLastUpdate.KEY.equals(property.getExpose().getProperty())) {
                uiInputBuilder.addDuration(property.getValue().longValue(), null);
            } else {
                uiInputBuilder.addInfo(property.getValue().toString(), InfoType.HTML);
            }
            return uiInputBuilder;
        }

        /**
         * Build action for 'numeric' type.
         */
        private boolean buildWritableNumberTypeAction(UIInputBuilder uiInputBuilder) {
            if (property.getExpose().getValueMin() != null && property.getExpose().getValueMax() != null) {
                // create only slider if expose has only valueMin and valueMax
                if (property.getExpose().getPresets() == null || property.getExpose().getPresets().isEmpty()) {
                    addUISlider(uiInputBuilder);
                    return true;
                }

                // build flex(selectBox,slider)
                uiInputBuilder.addFlex(entityID + "_compose", flex -> {
                    UISelectBoxItemBuilder presets = flex.addSelectBox(entityID + "_presets", (entityContext, params) -> {
                        property.fireAction(params.getInt("value"));
                        return null;
                    });
                    presets.addOptions(property.getExpose().getPresets().stream().map(p ->
                               OptionModel.of(String.valueOf(p.getValue()), p.getName()).setDescription(p.getDescription())).collect(Collectors.toList()))
                           .setAsButton("fas fa-kitchen-set", null, null);
                    // set selected presets if any presets equal to current value
                    if (property.getExpose().getPresets().stream().anyMatch(p -> String.valueOf(p.getValue()).equals(property.getValue().toString()))) {
                        presets.setSelected(property.getValue().toString());
                    }

                    addUISlider(flex);
                });

                return true;
            }
            return false;
        }

        private UIInputBuilder buildWritableEnumTypeAction(UIInputBuilder uiInputBuilder) {
            if (!property.getExpose().isReadable() && property.getExpose().getValues().size() == 1) {
                uiInputBuilder.addButton(entityID, "fas fa-play", "#eb0000", (entityContext, params) -> {
                    property.fireAction(property.getExpose().getValues().get(0));
                    return null;
                });
            } else {
                uiInputBuilder.addSelectBox(entityID, (entityContext, params) -> {
                                  property.fireAction(params.getString("value"));
                                  return null;
                              })
                              .addOptions(OptionModel.list(property.getExpose().getValues()))
                              .setPlaceholder("-----------")
                              .setSelected(property.getValue().toString());
            }
            return uiInputBuilder;
        }

        private void addUISlider(UILayoutBuilder builder) {
            builder.addSlider(entityID,
                property.getValue().floatValue(0),
                property.getExpose().getValueMin().floatValue(),
                property.getExpose().getValueMax().floatValue(),
                (entityContext, params) -> {
                    property.fireAction(params.getInt("value"));
                    return null;
                });
        }
    }
}
