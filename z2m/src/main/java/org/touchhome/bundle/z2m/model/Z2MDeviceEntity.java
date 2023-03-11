package org.touchhome.bundle.z2m.model;

import static java.lang.String.format;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.BINARY_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.ENUM_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.NUMBER_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.DisableCacheEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.touchhome.bundle.api.entity.zigbee.ZigBeeProperty;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.optionProvider.SelectPlaceOptionLoader;
import org.touchhome.bundle.api.ui.UI;
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
import org.touchhome.bundle.api.ui.field.action.v1.layout.UIFlexLayoutBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorBgRef;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.condition.UIFieldShowOnCondition;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.z2m.service.Z2MDeviceService;
import org.touchhome.bundle.z2m.service.Z2MProperty;
import org.touchhome.bundle.z2m.setting.ZigBeeEntityCompactModeSetting;
import org.touchhome.bundle.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition;
import org.touchhome.bundle.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.z2m.util.ZigBeeUtil;

@Log4j2
@Getter
@Setter
@NoArgsConstructor
@DisableCacheEntity
public final class Z2MDeviceEntity extends ZigBeeDeviceBaseEntity<Z2MDeviceEntity>
    implements HasJsonData, HasStatusAndMsg<Z2MDeviceEntity>, HasDynamicContextMenuActions {

    public static final String PREFIX = "z2m_";
    @JsonIgnore private transient Z2MDeviceService deviceService;

    public Z2MDeviceEntity(Z2MDeviceService deviceService) {
        super();
        this.deviceService = deviceService;

        setEntityID(PREFIX + deviceService.getDevice().getIeeeAddress());
        setIeeeAddress(deviceService.getDevice().getIeeeAddress());
    }

    @Override
    public @NotNull String getDeviceFullName() {
        return deviceService.getDeviceFullName();
    }

    @Override
    public @NotNull Map<String, ZigBeeProperty> getProperties() {
        return deviceService.getProperties().entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @Override
    public @Nullable ZigBeeProperty getProperty(@NotNull String property) {
        return deviceService.getProperties().get(property);
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

    @Override
    public String getIcon() {
        return ZigBeeUtil.getDeviceIcon(deviceService.getDevice().getModelId(), "fas fa-server");
    }

    @Override
    public String getIconColor() {
        return ZigBeeUtil.getDeviceIconColor(deviceService.getDevice().getModelId(), UI.Color.random());
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
                + "</div><div>${%s~%s}</div></div>",
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
        deviceService.updateConfiguration("image", Objects.equals(value, getModelIdentifier()) ? null : value);
    }

    @UIField(order = 9999)
    @UIFieldInlineEntities(bg = "#27FF0005")
    @SuppressWarnings("unused")
    public List<Z2MPropertyEntity> getEndpointClusters() {
        return deviceService.getProperties().values().stream()
                            .filter(Z2MProperty::isVisible)
                            .map(Z2MPropertyEntity::new)
                            .sorted()
                            .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
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
        Z2MActionsBuilder.createWidgetActions(uiInputBuilder, getEntityContext(), this);

        Z2MDeviceDefinition definition = deviceService.getDevice().getDefinition();
        if (definition != null) {
            for (Options option : definition.getOptions()) {
                JsonNode deviceConfigurationOptions = deviceService.getConfiguration();
                String flexName = format("${z2m.setting.%s~%s}", option.getName(), ZigBeeUtil.splitNameToReadableFormat(option.getName()));
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

    @Override
    @UIFieldIgnore
    public Date getCreationTime() {
        return super.getCreationTime();
    }

    @Override
    @UIFieldIgnore
    public Date getUpdateTime() {
        return new Date(deviceService.getProperties().values()
                                     .stream()
                                     .max(Comparator.comparingLong(Z2MProperty::getUpdated))
                                     .map(Z2MProperty::getUpdated).orElse(0L));
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

    @Override
    public int compareTo(@NotNull BaseEntity o) {
        if (o instanceof Z2MDeviceEntity) {
            Z2MDeviceEntity other = (Z2MDeviceEntity) o;
            return ((getStatus().isOnline() ? 0 : 1) + getName()).compareTo((other.getStatus().isOnline() ? 0 : 1) + o.getName());
        }
        return super.compareTo(o);
    }

    private static Float toFloat(Integer value) {
        return value == null ? null : value.floatValue();
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

        uiInputBuilder.addFlex(option.getName() + "_flex", flex -> buildCheckbox(option, bValue, flex))
                      .columnFlexDirection()
                      .setBorderColor(Color.random())
                      .setBorderArea(flexName);
    }

    private void buildCheckbox(Options option, boolean bValue, UIFlexLayoutBuilder flex) {
        flex.addCheckbox(option.getName(), bValue, (entityContext, params) -> {
            this.deviceService.getCoordinatorService().updateDeviceConfiguration(deviceService, option.getName(), params.getBoolean("value"));
            return null;
        });
        flex.addInfo(option.getDescription()).setOuterClass("context-description");
    }

    @Getter
    @NoArgsConstructor
    public static class Z2MPropertyEntity implements Comparable<Z2MPropertyEntity> {

        private String entityID;

        @UIField(order = 2, type = UIFieldType.HTML)
        private String title;

        @JsonIgnore private Z2MProperty property;
        private String valueTitle;

        public Z2MPropertyEntity(Z2MProperty property) {
            this.entityID = property.getEntityID();
            this.title = format("<div class=\"inline-2row_d\"><div style=\"color:%s;\"><i class=\"mr-1 %s\"></i>%s</div><span>%s</div></div>",
                property.getIconColor(), property.getIcon(), property.getName(false), property.getDescription());
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

        @Override
        public int compareTo(@NotNull Z2MDeviceEntity.Z2MPropertyEntity o) {
            return ZigBeeUtil.compareProperty(this.property.getExpose().getName(), o.getProperty().getExpose().getName());
        }

        private @NotNull UIInputBuilder buildAction() {
            return ZigBeeUtil.buildZigbeeActions(property, entityID);
        }
    }
}
