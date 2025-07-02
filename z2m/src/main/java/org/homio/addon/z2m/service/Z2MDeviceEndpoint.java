package org.homio.addon.z2m.service;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.addon.z2m.service.Z2MDeviceService.CONFIG_DEVICE_SERVICE;
import static org.homio.addon.z2m.util.ApplianceModel.*;
import static org.homio.api.util.CommonUtils.splitNameToReadableFormat;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options.Presets;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.Context;
import org.homio.api.ContextVar.VariableMetaBuilder;
import org.homio.api.ContextVar.VariableType;
import org.homio.api.model.Icon;
import org.homio.api.model.OptionModel;
import org.homio.api.model.endpoint.BaseDeviceEndpoint;
import org.homio.api.state.*;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Log4j2
@Getter
public abstract class Z2MDeviceEndpoint extends BaseDeviceEndpoint<Z2MDeviceEntity> {

    @Setter
    private Function<JsonNode, State> dataReader;
    private Options expose;
    private Z2MDeviceService deviceService;

    public Z2MDeviceEndpoint(@NotNull Icon icon, @NotNull Context context) {
        super(icon, "z2m", context);
    }

    public void init(@NotNull Z2MDeviceService deviceService, @NotNull Options expose) {
        this.deviceService = deviceService;
        this.expose = expose;
        this.dataReader = this.dataReader == null ? buildDataReader() : this.dataReader;
        setAlternateEndpoints(expose.getName(), expose.getEndpoint());
        if (expose.getValueMin() != null) {
            setMin(Float.valueOf(expose.getValueMin()));
        }
        if (expose.getValueMax() != null) {
            setMax(Float.valueOf(expose.getValueMax()));
        }
        setUnit(expose.getUnit());
        setReadable(expose.isReadable());
        setWritable(expose.isWritable());
        init(
                CONFIG_DEVICE_SERVICE,
                expose.getProperty(),
                deviceService.getDeviceEntity(),
                expose.getName(),
                calcEndpointType()
        );
        getOrCreateVariable();
    }

    @Override
    public @NotNull Set<String> getHiddenEndpoints() {
        return deviceService.getCoordinatorEntity().getHiddenEndpoints();
    }

    public void mqttUpdate(JsonNode payload) {
        this.setValue(dataReader.apply(payload), true);
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
        return "${zbd.%s~%s}".formatted(expose.getName(), defaultIfEmpty(getExpose().getDescription(), getEndpointEntityID()));
    }

    public void fireAction(boolean value) {
        Object valueToFire = value ? getExpose().getValueOn() : getExpose().getValueOff();
        JSONObject params = new JSONObject().put(getEndpointEntityID(), valueToFire);
        deviceService.publish("set", params);
    }

    public void fireAction(int value) {
        if (expose.getValueMin() != null && value < expose.getValueMin()) {
            value = expose.getValueMin();
        } else if (expose.getValueMax() != null && value > expose.getValueMax()) {
            value = expose.getValueMax();
        }
        deviceService.publish("set", new JSONObject().put(getEndpointEntityID(), value));
    }

    public void fireAction(String value) {
        deviceService.publish("set", new JSONObject().put(getEndpointEntityID(), value));
    }

    public boolean feedPayload(String key, JsonNode payload) {
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
        deviceService.publish("get", new JSONObject().put(getEndpointEntityID(), ""));
    }

    @Override
    public @Nullable UIInputBuilder createActionBuilder() {
        return ZigBeeUtil.createActionBuilder(this);
    }

    protected String getJsonKey() {
        return expose.getName();
    }

    protected Function<JsonNode, State> buildDataReader() {
        switch (expose.getType()) {
            case SWITCH_TYPE, BINARY_TYPE -> {
                if (expose.getValueOn() != null) {
                    if (expose.getValueOn() instanceof String) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.get(getJsonKey()).asText()));
                    } else if (expose.getValueOn() instanceof Boolean) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.get(getJsonKey()).asBoolean()));
                    } else {
                        log.error(
                                "[{}]: Unknown type: {} for endpoint: {}",
                                deviceService.getCoordinatorEntity().getEntityID(),
                                expose.getValueOn(),
                                deviceService.getIeeeAddress());
                    }
                }
                return payload -> OnOffType.of(payload.get(getJsonKey()).asBoolean());
            }
            case NUMBER_TYPE -> {
                return payload -> new DecimalType(payload.get(getJsonKey()).asDouble()).setUnit(getUnit());
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
    public @NotNull List<OptionModel> getSelectValues() {
        return OptionModel.list(expose.getValues());
    }

    @Override
    protected String getVariableDescription() {
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
            builder.setWritable(isWritable()).setDescription(getVariableDescription()).setColor(getIcon().getColor());
            List<String> attributes = new ArrayList<>();
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
    public @NotNull VariableType getVariableType() {
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
                if ("color".equals(getEndpointEntityID())) {
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
    public String getVariableGroupID() {
        return "z2m-" + getDeviceID();
    }

    private @NotNull EndpointType calcEndpointType() {
        return switch (expose.getType()) {
            case NUMBER_TYPE -> EndpointType.number;
            case BINARY_TYPE, SWITCH_TYPE -> EndpointType.bool;
            case ENUM_TYPE -> EndpointType.select;
            default -> EndpointType.string;
        };
    }

    @Override
    protected @NotNull List<OptionModel> getVariableEnumValues() {
        return OptionModel.list(expose.getValues());
    }
}
