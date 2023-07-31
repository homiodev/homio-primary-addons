package org.homio.addon.z2m.service;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.addon.z2m.service.Z2MDeviceService.CONFIG_DEVICE_SERVICE;
import static org.homio.addon.z2m.util.ApplianceModel.BINARY_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.ENUM_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.NUMBER_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.SWITCH_TYPE;
import static org.homio.api.util.CommonUtils.splitNameToReadableFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options.Presets;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.EntityContextVar.VariableMetaBuilder;
import org.homio.api.EntityContextVar.VariableType;
import org.homio.api.model.Icon;
import org.homio.api.model.endpoint.BaseDeviceEndpoint;
import org.homio.api.state.DecimalType;
import org.homio.api.state.JsonType;
import org.homio.api.state.OnOffType;
import org.homio.api.state.State;
import org.homio.api.state.StringType;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Log4j2
@Getter
public abstract class Z2MDeviceEndpoint extends BaseDeviceEndpoint<Z2MDeviceEntity> {

    public static final String ENDPOINT_FIRMWARE_UPDATE = "update";

    @Setter
    private Function<JSONObject, State> dataReader;
    private Options expose;
    private Z2MDeviceService deviceService;

    public Z2MDeviceEndpoint(@NotNull Icon icon) {
        super(icon);
    }

    public void init(@NotNull Z2MDeviceService deviceService, @NotNull Options expose, boolean createVariable) {
        this.deviceService = deviceService;
        this.expose = expose;
        this.dataReader = this.dataReader == null ? buildDataReader() : this.dataReader;
        init(
            StringUtils.defaultString(expose.getProperty(), expose.getEndpoint()),
            deviceService.getDeviceEntity(),
            deviceService.getEntityContext(),
            StringUtils.defaultIfEmpty(getUnit(), expose.getUnit()),
            expose.isReadable(),
            expose.isWritable(),
            expose.getName(),
            CONFIG_DEVICE_SERVICE.getEndpointOrder(expose.getName()),
            calcEndpointType());

        if (createVariable) {
            getOrCreateVariable();
        }
    }

    public void mqttUpdate(JSONObject payload) {
        this.setUpdated(System.currentTimeMillis());
        this.setValue(dataReader.apply(payload));
        for (Consumer<State> changeListener : getChangeListeners().values()) {
            changeListener.accept(getValue());
        }

        updateUI();
        // push value to variable. Variable engine will fire event!
        pushVariable();
    }

    @Override
    public @NotNull String getName(boolean shortFormat) {
        String l1Name = expose.getName();
        if (expose.getProperty() != null && !expose.getProperty().equals(expose.getName())) {
            l1Name = expose.getProperty();
        }
        String name = splitNameToReadableFormat(l1Name);
        name = shortFormat ? name : "${zbe.%s~%s}".formatted(l1Name, name);

        if (isNotEmpty(expose.getEndpoint())) {
            return "%s [%s]".formatted(name, expose.getEndpoint());
        }
        return name;
    }

    @Override
    public String getDescription() {
        return "${zbd.%s~%s}".formatted(expose.getName(), defaultIfEmpty(getExpose().getDescription(), expose.getProperty()));
    }

    public void fireAction(boolean value) {
        Object valueToFire = value ? getExpose().getValueOn() : getExpose().getValueOff();
        JSONObject params = new JSONObject().put(expose.getProperty(), valueToFire);
        deviceService.publish("set", params);
    }

    public void fireAction(int value) {
        if (expose.getValueMin() != null && value < expose.getValueMin()) {
            value = expose.getValueMin();
        } else if (expose.getValueMax() != null && value > expose.getValueMax()) {
            value = expose.getValueMax();
        }
        deviceService.publish("set", new JSONObject().put(expose.getProperty(), value));
    }

    public void fireAction(String value) {
        deviceService.publish("set", new JSONObject().put(expose.getProperty(), value));
    }

    @Override
    public boolean isVisible() {
        if (CONFIG_DEVICE_SERVICE.isHideEndpoint(expose.getProperty())) {
            return false;
        }
        return !deviceService.getCoordinatorEntity().getHiddenEndpoints().contains(expose.getProperty());
    }

    public boolean feedPayload(String key, JSONObject payload) {
        if (key.equals(expose.getProperty()) || key.equals(expose.getName())) {
            mqttUpdate(payload);
            return true;
        }
        return false;
    }

    public abstract @Nullable String getEndpointDefinition();

    @Override
    public void writeValue(@NotNull State state) {
        switch (expose.getType()) {
            case NUMBER_TYPE -> fireAction(state.intValue());
            case BINARY_TYPE, SWITCH_TYPE -> fireAction(state.boolValue());
            default -> fireAction(state.stringValue());
        }
    }

    @Override
    public void readValue() {
        deviceService.publish("get", new JSONObject().put(expose.getProperty(), ""));
    }

    @Override
    public @NotNull UIInputBuilder createUIInputBuilder() {
        return ZigBeeUtil.createUIInputBuilder(this);
    }

    protected String getJsonKey() {
        return expose.getName();
    }

    protected Function<JSONObject, State> buildDataReader() {
        switch (expose.getType()) {
            case SWITCH_TYPE, BINARY_TYPE -> {
                if (expose.getValueOn() != null) {
                    if (expose.getValueOn() instanceof String) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.getString(getJsonKey())));
                    } else if (expose.getValueOn() instanceof Boolean) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.getBoolean(getJsonKey())));
                    } else {
                        log.error(
                                "[{}]: Unknown type: {} for endpoint: {}",
                                deviceService.getCoordinatorEntity().getEntityID(),
                                expose.getValueOn(),
                                deviceService.getIeeeAddress());
                    }
                }
                return payload -> OnOffType.of(payload.getBoolean(getJsonKey()));
            }
            case NUMBER_TYPE -> {
                return payload -> new DecimalType(payload.getNumber(getJsonKey())).setUnit(getUnit());
            }
            case ApplianceModel.COMPOSITE_TYPE -> {
                return payload -> new JsonType(payload.get(getJsonKey()).toString());
            }
            default -> {
                return payload -> new StringType(payload.get(getJsonKey()).toString());
            }
        }
    }

    @Override
    public @NotNull List<String> getSelectValues() {
        return expose.getValues();
    }

    private String getVariableDescription() {
        List<String> description = new ArrayList<>();
        if (isNotEmpty(getExpose().getDescription())) {
            description.add(getExpose().getDescription());
        } else if (isNotEmpty(expose.getUnit())) {
            description.add(expose.getUnit());
        }
        if (expose.getValueMin() != null && expose.getValueMax() != null) {
            description.add("(range:%d...%d)".formatted(expose.getValueMin(), expose.getValueMax()));
        }
        if (expose.getValueOn() != null && expose.getValueOff() != null) {
            description.add("(on:%s;off:%s)".formatted(expose.getValueOn(), expose.getValueOff()));
        }
        if (expose.getPresets() != null && !expose.getPresets().isEmpty()) {
            description.add("(presets:%s)".formatted(expose.getPresets().stream().map(Presets::getName).collect(Collectors.joining("|"))));
        }
        if (description.isEmpty()) {
            description.add(getDescription());
        }
        return String.join(" ", description);
    }

    protected Consumer<VariableMetaBuilder> getVariableMetaBuilder() {
        return builder -> {
            builder.setDescription(getVariableDescription()).setReadOnly(!isWritable()).setColor(getIcon().getColor());
            List<String> attributes = new ArrayList<>();
            if (expose.getValueMin() != null) {
                attributes.add("min:" + expose.getValueMin());
            }
            if (expose.getValueMax() != null) {
                attributes.add("max:" + expose.getValueMax());
            }
            if (expose.getValueStep() != null) {
                attributes.add("step:" + expose.getValueStep());
            }
            if (expose.getValueToggle() != null) {
                attributes.add("toggle:" + expose.getValueToggle());
            }
            if (expose.getValueOn() != null) {
                attributes.add("on:" + expose.getValueOn());
            }
            if (expose.getValueOff() != null) {
                attributes.add("off:" + expose.getValueOff());
            }
            builder.setAttributes(attributes);
        };
    }

    @Override
    protected @NotNull VariableType getVariableType() {
        switch (expose.getType()) {
            case ApplianceModel.ENUM_TYPE -> {
                return VariableType.Enum;
            }
            case NUMBER_TYPE -> {
                return VariableType.Float;
            }
            case BINARY_TYPE, SWITCH_TYPE -> {
                return VariableType.Bool;
            }
            default -> {
                if ("color".equals(expose.getProperty())) {
                    return VariableType.Color;
                }
                // check if we are able to find out type from current value
                if (getValue() instanceof DecimalType) {
                    return VariableType.Float;
                }
                String valueStr = getValue().stringValue();
                try {
                    Float.parseFloat(valueStr);
                    return VariableType.Float;
                } catch (Exception ignore) {
                }
                if (getValue() instanceof OnOffType) {
                    return VariableType.Bool;
                }
                if (valueStr.equals("1")
                    || valueStr.equals("0")
                    || valueStr.equals("true")
                    || valueStr.equals("false")
                    || valueStr.equalsIgnoreCase("ON")
                    || valueStr.equalsIgnoreCase("OFF")) {
                    return VariableType.Bool;
                }
                return VariableType.Any;
            }
        }
    }

    @Override
    protected @NotNull List<String> getVariableEnumValues() {
        return expose.getValues();
    }

    private @NotNull EndpointType calcEndpointType() {
        return switch (expose.getType()) {
            case NUMBER_TYPE -> EndpointType.number;
            case BINARY_TYPE, SWITCH_TYPE -> EndpointType.bool;
            case ENUM_TYPE -> EndpointType.select;
            default -> EndpointType.string;
        };
    }
}
