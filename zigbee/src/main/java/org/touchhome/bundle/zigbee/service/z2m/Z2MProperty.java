package org.touchhome.bundle.zigbee.service.z2m;

import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.BINARY_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.COMPOSITE_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.ENUM_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.NUMBER_TYPE;
import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.SWITCH_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.measure.Unit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.JsonType;
import org.touchhome.bundle.api.state.ObjectType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.api.util.Units;
import org.touchhome.bundle.zigbee.model.z2m.Z2MDeviceEntity.Z2MPropertyEntity;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options.Presets;

@Log4j2
@Getter
public abstract class Z2MProperty {

    private final @NotNull String color;
    private final @NotNull String icon;
    protected Function<JSONObject, State> dataReader;
    protected Options expose;
    @Setter private @Nullable Unit<?> unit;
    private Z2MDeviceService deviceService;
    @Setter private long updated;
    private String entityID;
    private String variableId;
    @Getter private EntityContext entityContext;

    @Setter private State value = new StringType("N/A");
    private Object dbValue;

    public Z2MProperty(@NotNull String color, @NotNull String icon) {
        this.color = color;
        this.icon = icon;
    }

    public void init(Z2MDeviceService deviceService, Options expose) {
        this.deviceService = deviceService;
        this.entityContext = this.deviceService.getEntityContext();
        this.expose = expose;
        this.entityID = deviceService.getDevice().getIeeeAddress() + "_" + expose.getProperty();

        if (this.unit == null && expose.getUnit() != null) {
            this.unit = Units.findUnit(expose.getUnit());
            if (this.unit == null) {
                log.warn("[{}]: Unable to find java Unit for string representation: {}",
                    deviceService.getCoordinatorService().getEntityID(), expose.getUnit());
            }
        }

        if (this.dataReader == null) {
            this.dataReader = buildDataReader();
        }

        getOrCreateVariable();
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
                if (unit != null) {
                    return payload -> new QuantityType<>(payload.getNumber(getJsonKey()), unit);
                } else {
                    return payload -> new DecimalType(payload.getNumber(getJsonKey()));
                }
            case COMPOSITE_TYPE:
                return payload -> new JsonType(payload.get(getJsonKey()).toString());
            case ENUM_TYPE:
                return payload -> new StringType(payload.getString(getJsonKey()));
            default:
                return payload -> new StringType(payload.get(getJsonKey()).toString());
        }
    }

    public void mqttUpdate(JSONObject payload) {
        this.updated = System.currentTimeMillis();
        value = dataReader.apply(payload);

        updateUI();
        pushVariable();

        ObjectType entityUpdated = new ObjectType(this);
        // entityContext.event().fireEvent(deviceHandler.getDevice().getIeeeAddress(),
        // entityUpdated);
        entityContext.event().fireEvent(entityID, entityUpdated);
    }

    protected void updateUI() {
        entityContext.ui().updateInnerSetItem(deviceService.getDeviceEntity(), "endpointClusters", entityID, "value", new Z2MPropertyEntity(this).getValue());
        entityContext.ui().updateInnerSetItem(deviceService.getDeviceEntity(), "endpointClusters", entityID, "updated", updated);
    }

    protected void pushVariable() {
        getOrCreateVariable();
        entityContext.var().set(variableId, value, dbValue -> this.dbValue = dbValue);
    }

    private void getOrCreateVariable() {
        if (variableId == null) {
            VariableType variableType = getVariableType();
            if (variableType == VariableType.Enum) {
                variableId = entityContext.var().createEnumVariable(deviceService.getDeviceEntity().getEntityID(),
                    entityID, getName(), getVariableDescription(), !isWritable(), color, expose.getValues());
            } else {
                variableId = entityContext.var().createVariable(deviceService.getDeviceEntity().getEntityID(),
                    entityID, getName(), variableType, getVariableDescription(), !isWritable(), color, expose.getUnit());
            }

            if (isWritable()) {
                entityContext.var().setLinkListener(variableId, varValue -> {
                    // fire updates only if variable updates externally
                    if (!Objects.equals(dbValue, varValue)) {
                        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), varValue));
                    }
                });
            }
        }
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
            case ENUM_TYPE:
                return VariableType.Enum;
            case NUMBER_TYPE:
                return VariableType.Float;
            case BINARY_TYPE:
            case SWITCH_TYPE:
                return VariableType.Bool;
            default:
                switch (expose.getProperty()) {
                    case "color":
                        return VariableType.Color;
                }
                // check if we are able to find out type from current value
                if (value instanceof DecimalType || value instanceof QuantityType) {
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

    public String getName() {
        String name = format("${zigbee.endpoint.name.%s:%s}", expose.getName(), expose.getName());
        if (isNotEmpty(expose.getEndpoint())) {
            return format("%s [%s]", name, expose.getEndpoint());
        }
        return name;
    }

    public String getDescription() {
        return format("${zigbee.endpoint.description.%s:%s}", expose.getName(), defaultIfEmpty(getExpose().getDescription(), expose.getProperty()));
    }

    public void fireAction(boolean value) {
        getDeviceService().publish("set", new JSONObject().put(expose.getProperty(), value ? getExpose().getValueOn() : getExpose().getValueOff()));
    }

    public void fireAction(int value) {
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
}
