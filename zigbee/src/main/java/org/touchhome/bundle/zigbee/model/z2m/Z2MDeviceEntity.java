package org.touchhome.bundle.zigbee.model.z2m;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.DisableCacheEntity;
import org.touchhome.bundle.api.entity.HasJsonData;
import org.touchhome.bundle.api.entity.HasStatusAndMsg;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.optionProvider.SelectPlaceOptionLoader;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldIgnoreGetDefault;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.ui.field.color.UIFieldColorStatusMatch;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntities;
import org.touchhome.bundle.api.ui.field.inline.UIFieldInlineEntityWidth;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelectValueOnEmpty;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceBaseEntity;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;
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
  @JsonIgnore
  private transient Z2MDeviceService deviceService;

  public Z2MDeviceEntity(Z2MDeviceService deviceService) {
    super();
    this.deviceService = deviceService;

    setEntityID(PREFIX + deviceService.getDevice().getIeeeAddress());
    setName(deviceService.getDevice().getName());
    setIeeeAddress(deviceService.getDevice().getIeeeAddress());
  }

  @UIField(order = 10, hideInEdit = true, hideOnEmpty = true)
  @UIFieldColorStatusMatch
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public Status getStatus() {
    return deviceService.getDeviceEntity().getStatus();
  }

  @UIField(order = 1, hideOnEmpty = true, fullWidth = true, bg = "#334842C2", inlineEdit = true)
  @UIFieldIgnoreGetDefault
  public String getDescription() {
    return deviceService.getDevice().getDefinition().getDescription();
  }

  @SuppressWarnings("unused")
  public void setDescription(String value) {
    deviceService.updateConfiguration("description", value);
  }

  @UIField(order = 2, type = UIFieldType.SelectBox, color = "#538744", inlineEdit = true)
  @UIFieldIgnoreGetDefault
  @UIFieldSelection(SelectPlaceOptionLoader.class)
  @UIFieldSelectValueOnEmpty(label = "SELECT_PLACE")
  public String getPlace() {
    return deviceService.getConfiguration().get("place").asText();
  }

  @Override
  public DeviceBaseEntity<Z2MDeviceEntity> setPlace(String value) {
    deviceService.updateConfiguration("place", value);
    return this;
  }

  @UIField(order = 5)
  @UIFieldIgnoreGetDefault
  public int getNetworkAddress() {
    return deviceService.getDevice().getNetworkAddress();
  }

  @UIField(order = 10)
  @UIFieldIgnoreGetDefault
  public String getModel() {
    return deviceService.getDevice().getDefinition().getModel();
  }

  @UIField(order = 15)
  @UIFieldIgnoreGetDefault
  public String getModelIdentifier() {
    return deviceService.getDevice().getModelId();
  }

  @UIField(order = 20, hideOnEmpty = true)
  @UIFieldIgnoreGetDefault
  public String getCurrentPowerSource() {
    return deviceService.getDevice().getPowerSource();
  }

  @UIField(order = 25)
  @UIFieldIgnoreGetDefault
  public String getLogicalType() {
    return deviceService.getDevice().getType();
  }

  @UIField(order = 30)
  @UIFieldIgnoreGetDefault
  public String getManufacturer() {
    return deviceService.getDevice().getDefinition().getVendor();
  }

  @UIField(order = 35)
  @UIFieldIgnoreGetDefault
  public boolean getInterviewCompleted() {
    return deviceService.getDevice().isInterviewCompleted();
  }

  @UIField(order = 40, inlineEdit = true)
  @UIFieldIgnoreGetDefault
  public Integer getDebounce() {
    JsonNode deviceOptions = deviceService.getConfiguration();
    return deviceOptions.has("debounce") ? deviceOptions.get("debounce").asInt() : null;
  }

  @SuppressWarnings("unused")
  public void setDebounce(Integer value) {
    deviceService.updateConfiguration("debounce", value);
  }

  @UIField(order = 45, inlineEdit = true, type = UIFieldType.Chips)
  public Set<String> getDebounceIgnore() {
    JsonNode deviceOptions = deviceService.getConfiguration();
    if (deviceOptions.has("debounce_ignore")) {
      JsonNode debounceIgnoreList = deviceOptions.get("debounce_ignore");
      Set<String> set = new HashSet<>();
      for (JsonNode jsonNode : debounceIgnoreList) {
        set.add(jsonNode.asText());
      }
      return set;
    }
    return Collections.emptySet();
  }

  @SuppressWarnings("unused")
  public void setDebounceIgnore(String value) {
    String[] ignoreActions = value.split("~~~");
    JSONArray array = new JSONArray();
    for (String ignoreAction : ignoreActions) {
      array.put(ignoreAction);
    }
    deviceService.updateConfiguration("debounce_ignore", array);
  }

  @UIField(order = 50, inlineEdit = true)
  public boolean isRetainDeviceMessages() {
    JsonNode deviceOptions = deviceService.getConfiguration();
    return deviceOptions.get("retain").asBoolean(false);
  }

  @SuppressWarnings("unused")
  public void setRetainDeviceMessages(boolean value) {
    deviceService.updateConfiguration("debounce_ignore", value ? true : null);
  }

  @UIField(order = 9999)
  @UIFieldInlineEntities(bg = "#27FF000D")
  @SuppressWarnings("unused")
  public List<Z2MPropertyEntity> getEndpointClusters() {
    return deviceService.getProperties().values().stream()
                        .sorted(Comparator.comparing(Z2MProperty::getProperty))
                        .map(Z2MPropertyEntity::new)
                        .collect(Collectors.toList());
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
        switch (option.getType()) {
          case "numeric":
            JsonNode deviceOptions = ZigBeeUtil.getDeviceOptions(deviceService.getDevice().getModelId());
            int minValue = option.getValueMin() == null ? deviceOptions.get("min").asInt(Integer.MIN_VALUE) : option.getValueMin();
            int maxValue = option.getValueMax() == null ? deviceOptions.get("max").asInt(Integer.MAX_VALUE) : option.getValueMax();

            JsonNode deviceConfigurationOptions = deviceService.getConfiguration();
            int value = deviceConfigurationOptions == null ? 0 : deviceConfigurationOptions.get(option.getName()).asInt(0);

            uiInputBuilder.addNumberInput(option.getName(), value, minValue, maxValue, (entityContext, params) -> {
              this.deviceService.getCoordinatorService().updateDeviceConfiguration(deviceService, option.getName(), params.getInt("value"));
              return null;
            }).setAllowEraseValue(true);
            break;
        }
      }
    }
  }

  @Getter
  @NoArgsConstructor
  public static class Z2MPropertyEntity {

    private String entityID;

    @UIField(order = 2, type = UIFieldType.HTML)
    private String title;

    @UIField(order = 3, type = UIFieldType.Duration)
    @UIFieldInlineEntityWidth(15)
    private long updated;

    @UIField(order = 4)
    @UIFieldInlineEntityWidth(15)
    private String value;

    public Z2MPropertyEntity(Z2MProperty property) {
      this.entityID = property.getEntityID();
      this.title = format("<div class=\"inline-2row_d\"><div style=\"color:%s;\">${%s}</div><div>${%s:%s}</div></div>",
          property.getColor(), property.getName(), property.getDescription(), property.getExpose().getDescription());
      this.updated = property.getUpdated();
      this.value = property.getValue().toString();
    }
  }
}
