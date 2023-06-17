package org.homio.addon.z2m.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.service.properties.Z2MPropertyAction;
import org.homio.addon.z2m.service.properties.Z2MPropertyLastSeen;
import org.homio.addon.z2m.service.properties.Z2MPropertyLastUpdate;
import org.homio.addon.z2m.service.properties.dynamic.Z2MGeneralProperty;
import org.homio.addon.z2m.service.properties.dynamic.Z2MPropertyUnknown;
import org.homio.addon.z2m.util.Z2MDeviceModel;
import org.homio.addon.z2m.util.Z2MDeviceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.Z2MDevicePropertiesModel;
import org.homio.addon.z2m.util.Z2MPropertyConfigService;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.UI.Color;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.json.JSONObject;

@Getter
@Log4j2
public class Z2MDeviceService {

    private final Z2MLocalCoordinatorService coordinatorService;
    private final Map<String, Z2MProperty> properties = new ConcurrentHashMap<>();
    private final Z2MDeviceEntity deviceEntity;
    private final EntityContext entityContext;
    private final Z2MPropertyConfigService configService;
    private String availability;
    private Z2MDeviceModel device;
    private boolean initialized = false;

    public Z2MDeviceService(Z2MLocalCoordinatorService coordinatorService, Z2MDeviceModel device) {
        this.coordinatorService = coordinatorService;
        this.configService = coordinatorService.getConfigService();
        this.entityContext = coordinatorService.getEntityContext();
        this.deviceEntity = new Z2MDeviceEntity(this, device.getIeeeAddress());
        changeDeviceModel(device);

        deviceUpdated(device);
        addMissingProperties(coordinatorService, device);

        entityContext.ui().updateItem(deviceEntity);
        entityContext.ui().sendSuccessMessage(Lang.getServerMessage("ENTITY_CREATED", format("${%s}", this.device.getName())));
        setEntityOnline();
    }

    public void dispose() {
        log.warn("[{}]: Dispose zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
        coordinatorService.removeMQTTListener(device, "");
        coordinatorService.removeMQTTListener(device, "/availability");
        entityContext.ui().updateItem(deviceEntity);
        downLinkQualityToZero();
        initialized = false;
    }

    public void deviceUpdated(Z2MDeviceModel device) {
        changeDeviceModel(device);
        createOrUpdateDeviceGroup();
        removeRedundantExposes(device);
        for (Options expose : device.getDefinition().getExposes()) {
            // types like switch has no property but has 'type'/'endpoint'/'features'
            if (expose.getProperty() == null) {
                addExposeByFeatures(expose);
            } else {
                addPropertyOptional(expose.getProperty(), key -> buildExposeProperty(expose));
            }
        }
        addPropertyOptional(Z2MPropertyLastUpdate.UPDATED, key -> new Z2MPropertyLastUpdate(this));
        addPropertyOptional(Z2MPropertyLastSeen.LAST_SEEN, key -> new Z2MPropertyLastSeen(this));
    }

    public void setEntityOnline() {
        if (!initialized) {
            log.warn("[{}]: Initialize zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
            coordinatorService.addMQTTListener(device, "", payload -> mqttUpdate(new JSONObject(payload.toString())));
            coordinatorService.addMQTTListener(device, "/availability", payload -> {
                availability = payload == null ? null : payload.toString();
                entityContext.event().fireEvent(format("zigbee-%s", device.getIeeeAddress()), deviceEntity.getStatus());
                entityContext.ui().updateItem(this.deviceEntity);
                if ("offline".equals(availability)) {
                    downLinkQualityToZero();
                }
            });
            entityContext.event().fireEvent(format("zigbee-%s", device.getIeeeAddress()), deviceEntity.getStatus());
            initialized = true;
        } else {
            log.debug("[{}]: Zigbee device: {} was initialized before", coordinatorService.getEntityID(), deviceEntity.getTitle());
        }
    }

    private void changeDeviceModel(Z2MDeviceModel device) {
        this.device = device;
        Status status = Status.UNKNOWN;
        if (device.isDisabled()) {
            status = Status.DISABLED;
        } else if (device.isInterviewing()) {
            status = Status.INITIALIZE;
        } else if (!device.isInterviewCompleted() || !device.isSupported() ||
            (StringUtils.isEmpty(device.getType()) || "UNKNOWN".equalsIgnoreCase(device.getType()))) {
            status = Status.NOT_READY;
        }
        deviceEntity.setEntityStatus(status);
    }

    @Override
    public String toString() {
        return device.toString();
    }

    public JsonNode getConfiguration() {
        return coordinatorService
            .getConfiguration()
            .getDevices()
            .getOrDefault(device.getIeeeAddress(), OBJECT_MAPPER.createObjectNode());
    }

    public void updateConfiguration(String key, Object value) {
        this.coordinatorService.updateDeviceConfiguration(this, key, value);
    }

    public void publish(String topic, JSONObject payload) {
        this.coordinatorService.publish(defaultIfEmpty(device.getFriendlyName(), device.getIeeeAddress()) + "/" + topic, payload);
    }

    public String getDeviceFullName() {
        String name;
        if (this.getConfiguration().has("name")) {
            name = this.getConfiguration().get("name").asText();
        } else if (device.getModelId() != null || device.getDefinition().getModel() != null) {
            name = format("${zigbee.device.%s~%s}", defaultIfEmpty(device.getModelId(), device.getDefinition().getModel()),
                device.getDefinition().getDescription());
        } else {
            name = device.getDefinition().getDescription();
        }
        return format("%s(%s) [${%s}]", name, this.device.getIeeeAddress(), defaultIfEmpty(this.deviceEntity.getPlace(), "place_not_set"));
    }

    public Z2MProperty addDynamicProperty(String key, Supplier<Z2MProperty> supplier) {
        return addPropertyOptional(key, s -> {
            Z2MProperty property = supplier.get();
            // store missing property in file to next reload
            coordinatorService.addMissingProperty(deviceEntity.getIeeeAddress(), property);
            return property;
        });
    }

    private void mqttUpdate(JSONObject payload) {
        boolean updated = false;
        List<String> sb = new ArrayList<>();
        for (String key : payload.keySet()) {
            try {
                boolean feed = false;
                for (Z2MProperty property : properties.values()) {
                    if (property.feedPayload(key, payload)) {
                        feed = true;
                        updated = true;
                        sb.add(format("%s - %s", property.getDescription(), property.getValue().toString()));
                    }
                }
                if (!feed && !key.equals("illuminance_lux")) {
                    log.info("[{}]: Create dynamic created property '{}'. Device: {}", coordinatorService.getEntityID(), key, device.getIeeeAddress());
                    Z2MProperty missedZ2MProperty = buildExposeProperty(Options.dynamicExpose(key, getFormatFromPayloadValue(payload, key)));
                    missedZ2MProperty.mqttUpdate(payload);

                    addPropertyOptional(key, s -> missedZ2MProperty);
                    entityContext.ui().updateItem(deviceEntity);
                }
            } catch (Exception ex) {
                log.error("Unable to handle Z2MProperty: {}. Payload: {}. Msg: {}",
                    key, payload, CommonUtils.getErrorMessage(ex));
            }
        }
        if (updated) {
            properties.get(Z2MPropertyLastUpdate.UPDATED).mqttUpdate(null);
        }
        if (deviceEntity.isLogEvents() && !sb.isEmpty()) {
            entityContext.ui().sendInfoMessage(device.getGroupDescription(), String.join("\n", sb));
        }
    }

    private void removeRedundantExposes(Z2MDeviceModel device) {
        // remove illuminance_lux if illuminance is present
        if (device.getDefinition().getExposes().stream().anyMatch(e -> "illuminance_lux".equals(e.getName()))
            && device.getDefinition().getExposes().stream().anyMatch(e -> "illuminance".equals(e.getName()))) {
            device.getDefinition().getExposes().removeIf(e -> "illuminance_lux".equals(e.getName()));
        }
    }

    private void addMissingProperties(Z2MLocalCoordinatorService coordinatorService, Z2MDeviceModel device) {
        for (Pair<String, String> missingProperty : coordinatorService.getMissingProperties(device.getIeeeAddress())) {
            if ("action_event".equals(missingProperty.getValue())) {
                addPropertyOptional(missingProperty.getKey(), key -> Z2MPropertyAction.createActionEvent(key, this, entityContext));
            }
        }
    }

    private Z2MProperty addPropertyOptional(String key, Function<String, Z2MProperty> propertyProducer) {
        if (!properties.containsKey(key)) {
            properties.put(key, propertyProducer.apply(key));
            entityContext.event().fireEvent(format("zigbee-%s-%s", device.getIeeeAddress(), key), Status.ONLINE);
        }
        return properties.get(key);
    }

    private String getFormatFromPayloadValue(JSONObject payload, String key) {
        try {
            payload.getInt(key);
            return Z2MDeviceModel.NUMBER_TYPE;
        } catch (Exception ignore) {
        }
        try {
            payload.getBoolean(key);
            return Z2MDeviceModel.BINARY_TYPE;
        } catch (Exception ignore) {
        }
        return Z2MDeviceModel.UNKNOWN_TYPE;
    }

    // usually expose.getName() is enough but in case of color - name - 'color_xy' but property is
    // 'color'
    private <T> T getValueFromMap(Map<String, T> map, Options expose) {
        return map.getOrDefault(expose.getName(), map.get(expose.getProperty()));
    }

    private void addExposeByFeatures(Options expose) {
        if (expose.getFeatures() != null) {
            for (Options feature : expose.getFeatures()) {
                addPropertyOptional(feature.getProperty(), key -> buildExposeProperty(feature));
            }
        } else {
            log.error("[{}]: Device expose {} has no features", coordinatorService.getEntityID(), expose);
        }
    }

    private Z2MProperty buildExposeProperty(Options expose) {
        Class<? extends Z2MProperty> z2mCluster = getValueFromMap(configService.getConverters(), expose);
        Z2MProperty z2MProperty;
        if (z2mCluster == null) {
            Z2MDevicePropertiesModel z2MDevicePropertiesModel = getValueFromMap(configService.getDeviceProperties(), expose);
            if (z2MDevicePropertiesModel != null) {
                z2MProperty = new Z2MGeneralProperty(z2MDevicePropertiesModel.getIconColor(), z2MDevicePropertiesModel.getIcon());
                z2MProperty.setUnit(z2MDevicePropertiesModel.getUnit());
            } else {
                z2MProperty = new Z2MPropertyUnknown();
            }
        } else {
            z2MProperty = CommonUtils.newInstance(z2mCluster);
        }
        z2MProperty.init(this, expose);
        return z2MProperty;
    }

    private void createOrUpdateDeviceGroup() {
        Icon icon = new Icon(
            configService.getDeviceIcon(this.device.getModelId(), "fas fa-server"),
            configService.getDeviceIconColor(this.device.getModelId(), Color.random())
        );
        entityContext.var().createGroup("z2m", this.deviceEntity.getEntityID(), getDeviceFullName(), true,
            icon, this.device.getGroupDescription());
    }

    private void downLinkQualityToZero() {
        Optional.ofNullable(properties.get(Z2MGeneralProperty.SIGNAL)).ifPresent(z2MProperty -> {
            if (!z2MProperty.getValue().stringValue().equals("0")) {
                z2MProperty.setValue(new DecimalType(0));
            }
        });
    }
}
