package org.homio.addon.z2m.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.addon.z2m.util.Z2MDeviceModel.BINARY_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.NUMBER_TYPE;
import static org.homio.addon.z2m.util.Z2MDeviceModel.SWITCH_TYPE;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity.Z2MPropertyEntity;
import org.homio.addon.z2m.util.Z2MDeviceModel;
import org.homio.addon.z2m.util.Z2MDeviceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.Z2MDeviceModel.Z2MDeviceDefinition.Options.Presets;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextVar.VariableMetaBuilder;
import org.homio.api.EntityContextVar.VariableType;
import org.homio.api.entity.zigbee.ZigBeeProperty;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.homio.api.state.JsonType;
import org.homio.api.state.OnOffType;
import org.homio.api.state.State;
import org.homio.api.state.StringType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Log4j2
@Getter
@RequiredArgsConstructor
public abstract class Z2MProperty implements ZigBeeProperty {

    private final @NotNull Icon icon;
    private final Map<String, Consumer<State>> changeListeners = new ConcurrentHashMap<>();
    protected Function<JSONObject, State> dataReader;
    protected Options expose;
    @Setter private @Nullable String unit;
    private Z2MDeviceService deviceService;
    @Setter private long updated;
    private String entityID;
    private String variableID;
    @Getter private EntityContext entityContext;
    @Setter private State value = new StringType("N/A");
    private Object dbValue;

    @Override
    public Duration getTimeSinceLastEvent() {
        return Duration.ofMillis(System.currentTimeMillis() - updated);
    }

    @Override
    public void addChangeListener(String id, Consumer<State> changeListener) {
        changeListeners.put(id, changeListener);
    }

    @Override
    public void removeChangeListener(String id) {
        changeListeners.remove(id);
    }

    public void init(@NotNull Z2MDeviceService deviceService, @NotNull Options expose) {
        this.deviceService = deviceService;
        this.entityContext = this.deviceService.getEntityContext();
        this.expose = expose;
        this.entityID = deviceService.getDevice().getIeeeAddress() + "_" + expose.getProperty();
        this.unit = StringUtils.defaultIfEmpty(this.unit, expose.getUnit());
        this.dataReader = this.dataReader == null ? buildDataReader() : this.dataReader;

        getOrCreateVariable();
    }

    public void mqttUpdate(JSONObject payload) {
        this.updated = System.currentTimeMillis();
        value = dataReader.apply(payload);
        for (Consumer<State> changeListener : changeListeners.values()) {
            changeListener.accept(value);
        }

        updateUI();
        // push value to variable. Variable engine will fire event!
        pushVariable();
    }

    public @NotNull String getName(boolean shortFormat) {
        String name = ZigBeeUtil.splitNameToReadableFormat(expose.getName());
        name = shortFormat ? name : format("${zbe.%s~%s}", expose.getName(), name);
        if (isNotEmpty(expose.getEndpoint())) {
            return format("%s [%s]", name, expose.getEndpoint());
        }
        return name;
    }

    public String getDescription() {
        return format("${zbd.%s~%s}", expose.getName(), defaultIfEmpty(getExpose().getDescription(), expose.getProperty()));
    }

    public void fireAction(boolean value) {
        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), value ? getExpose().getValueOn() : getExpose().getValueOff()));
    }

    public void fireAction(int value) {
        if (expose.getValueMin() != null && value < expose.getValueMin()) {
            value = expose.getValueMin();
        } else if (expose.getValueMax() != null && value > expose.getValueMax()) {
            value = expose.getValueMax();
        }
        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), value));
    }

    public void fireAction(String value) {
        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), value));
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isWritable() {
        return expose.isWritable();
    }

    @Override
    public boolean isReadable() {
        return expose.isReadable();
    }

    public boolean feedPayload(String key, JSONObject payload) {
        if (key.equals(expose.getProperty()) || key.equals(expose.getName())) {
            mqttUpdate(payload);
            return true;
        }
        return false;
    }

    public int getInteger(int defaultValue) {
        try {
            return value.intValue();
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public abstract String getPropertyDefinition();

    @Override
    public @NotNull String getKey() {
        return Objects.requireNonNull(expose.getProperty());
    }

    @Override
    public @NotNull String getIeeeAddress() {
        return deviceService.getDevice().getIeeeAddress();
    }

    @Override
    public State getLastValue() {
        return value;
    }

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
        getDeviceService().publish("get", new JSONObject().put(expose.getProperty(), ""));
    }

    @Override
    public PropertyType getPropertyType() {
        return switch (expose.getType()) {
            case NUMBER_TYPE -> PropertyType.number;
            case BINARY_TYPE, SWITCH_TYPE -> PropertyType.bool;
            default -> PropertyType.string;
        };
    }

    protected String getJsonKey() {
        return expose.getName();
    }

    protected Function<JSONObject, State> buildDataReader() {
        switch (expose.getType()) {
            case SWITCH_TYPE:
            case BINARY_TYPE:
                if (expose.getValueOn() != null) {
                    if (expose.getValueOn() instanceof String) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.getString(getJsonKey())));
                    } else if (expose.getValueOn() instanceof Boolean) {
                        return payload -> OnOffType.of(expose.getValueOn().equals(payload.getBoolean(getJsonKey())));
                    } else {
                        log.error(
                            "[{}]: Unknown property type: {} for property: {}",
                            deviceService.getCoordinatorService().getEntityID(),
                            expose.getValueOn(),
                            deviceService.getDevice().getIeeeAddress());
                    }
                }
                return payload -> OnOffType.of(payload.getBoolean(getJsonKey()));
            case NUMBER_TYPE:
                return payload -> new DecimalType(payload.getNumber(getJsonKey())).setUnit(unit);
            case Z2MDeviceModel.COMPOSITE_TYPE:
                return payload -> new JsonType(payload.get(getJsonKey()).toString());
            case Z2MDeviceModel.ENUM_TYPE:
                return payload -> new StringType(payload.getString(getJsonKey()));
            default:
                return payload -> new StringType(payload.get(getJsonKey()).toString());
        }
    }

    protected void updateUI() {
        entityContext.ui().updateInnerSetItem(deviceService.getDeviceEntity(), "endpointClusters",
            entityID, "value", new Z2MPropertyEntity(this, deviceService).getValue());
        entityContext.ui().updateInnerSetItem(deviceService.getDeviceEntity(), "endpointClusters",
            entityID, "updated", updated);
    }

    protected void pushVariable() {
        getOrCreateVariable();
        entityContext.var().set(variableID, value, dbValue -> this.dbValue = dbValue);
    }

    private void getOrCreateVariable() {
        if (variableID == null) {
            VariableType variableType = getVariableType();
            if (variableType == VariableType.Enum) {
                variableID = entityContext.var().createEnumVariable(deviceService.getDeviceEntity().getEntityID(),
                    entityID, getName(false), expose.getValues(), getVariableMetaBuilder());
            } else {
                variableID = entityContext.var().createVariable(deviceService.getDeviceEntity().getEntityID(),
                    entityID, getName(false), variableType, getVariableMetaBuilder());
            }
            entityContext.var().setVariableIcon(variableID, icon);

            if (isWritable()) {
                entityContext.var().setLinkListener(variableID, varValue -> {
                    if (!deviceService.getCoordinatorService().getEntity().getStatus().isOnline()) {
                        throw new RuntimeException("Unable to handle action. Zigbee coordinator is offline");
                    }
                    // fire updates only if variable updates externally
                    if (!Objects.equals(dbValue, varValue)) {
                        writeValue(State.of(varValue));
                    }
                });
            }
        }
    }

    @NotNull
    private Consumer<VariableMetaBuilder> getVariableMetaBuilder() {
        return builder -> {
            builder.setDescription(getVariableDescription()).setReadOnly(!isWritable()).setColor(icon.getColor());
            List<String> attributes = new ArrayList<>();
            if (expose.getValueMin() != null) {attributes.add("min:" + expose.getValueMin());}
            if (expose.getValueMax() != null) {attributes.add("max:" + expose.getValueMax());}
            if (expose.getValueStep() != null) {attributes.add("step:" + expose.getValueStep());}
            if (expose.getValueToggle() != null) {attributes.add("toggle:" + expose.getValueToggle());}
            if (expose.getValueOn() != null) {attributes.add("on:" + expose.getValueOn());}
            if (expose.getValueOff() != null) {attributes.add("off:" + expose.getValueOff());}
            builder.setAttributes(attributes);
        };
    }

    private String getVariableDescription() {
        List<String> descr = new ArrayList<>();
        if (isNotEmpty(getExpose().getDescription())) {
            descr.add(getExpose().getDescription());
        } else if (isNotEmpty(expose.getUnit())) {
            descr.add(expose.getUnit());
        }
        if (expose.getValueMin() != null && expose.getValueMax() != null) {
            descr.add(format("(range:%s...%s)", expose.getValueMin(), expose.getValueMax()));
        }
        if (expose.getValueOn() != null && expose.getValueOff() != null) {
            descr.add(format("(on:%s;off:%s)", expose.getValueOn(), expose.getValueOff()));
        }
        if (expose.getPresets() != null && !expose.getPresets().isEmpty()) {
            descr.add(format("(presets:%s)", expose.getPresets().stream().map(Presets::getName).collect(Collectors.joining("|"))));
        }
        if (descr.isEmpty()) {
            descr.add(getDescription());
        }
        return String.join(" ", descr);
    }

    private VariableType getVariableType() {
        switch (expose.getType()) {
            case Z2MDeviceModel.ENUM_TYPE:
                return VariableType.Enum;
            case NUMBER_TYPE:
                return VariableType.Float;
            case BINARY_TYPE:
            case SWITCH_TYPE:
                return VariableType.Bool;
            default:
                if ("color".equals(expose.getProperty())) {
                    return VariableType.Color;
                }
                // check if we are able to find out type from current value
                if (value instanceof DecimalType) {
                    return VariableType.Float;
                }
                String valueStr = value.stringValue();
                try {
                    Float.parseFloat(valueStr);
                    return VariableType.Float;
                } catch (Exception ignore) {
                }
                if (value instanceof OnOffType) {
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
