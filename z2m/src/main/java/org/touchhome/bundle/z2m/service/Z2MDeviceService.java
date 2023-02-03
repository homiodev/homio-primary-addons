package org.touchhome.bundle.z2m.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.BINARY_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.NUMBER_TYPE;
import static org.touchhome.bundle.z2m.util.Z2MDeviceDTO.UNKNOWN_TYPE;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.ui.UI;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.service.properties.Z2MPropertyAction;
import org.touchhome.bundle.z2m.service.properties.Z2MPropertyLastUpdate;
import org.touchhome.bundle.z2m.service.properties.dynamic.Z2MGeneralProperty;
import org.touchhome.bundle.z2m.service.properties.dynamic.Z2MPropertyUnknown;
import org.touchhome.bundle.z2m.util.Z2MDeviceDTO;
import org.touchhome.bundle.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.z2m.util.Z2MPropertyDTO;
import org.touchhome.bundle.z2m.util.ZigBeeUtil;
import org.touchhome.common.util.CommonUtils;
import org.touchhome.common.util.Lang;

@Getter
@Log4j2
public class Z2MDeviceService {

    private final Z2MLocalCoordinatorService coordinatorService;
    private final Map<String, Z2MProperty> properties = new ConcurrentHashMap<>();
    private final Z2MDeviceEntity deviceEntity;
    private final EntityContext entityContext;
    private String availability;
    private Z2MDeviceDTO device;

    public Z2MDeviceService(Z2MLocalCoordinatorService coordinatorService, Z2MDeviceDTO device) {
        this.coordinatorService = coordinatorService;
        this.entityContext = coordinatorService.getEntityContext();
        this.device = device;
        this.deviceEntity = new Z2MDeviceEntity(this);

        this.deviceUpdated(device);
        addMissingProperties(coordinatorService, device);

        entityContext.ui().updateItem(deviceEntity);
        entityContext.ui().sendSuccessMessage(Lang.getServerMessage("ENTITY_CREATED", "NAME", format("${%s}", this.device.getName())));
        entityContext.event().addEventBehaviourListener(getDeviceTopic(this.device), payload -> mqttUpdate(new JSONObject(payload.toString())));

        entityContext.event().addEventBehaviourListener(
            format("%s/availability", getDeviceTopic(this.device)),
            payload -> {
                availability = payload == null ? null : payload.toString();
                entityContext.event().fireEvent(format("zigbee-%s", device.getIeeeAddress()), deviceEntity.getStatus());
                entityContext.ui().updateItem(this.deviceEntity);
            });
        entityContext.event().fireEvent(format("zigbee-%s", device.getIeeeAddress()), deviceEntity.getStatus());
    }

    public String getDeviceTopic(Z2MDeviceDTO device) {
        return format("%s-%s/%s",
            coordinatorService.getMqttEntity().getEntityID(),
            coordinatorService.getEntity().getBasicTopic(),
            device.getIeeeAddress());
    }

    public void dispose() {
        entityContext.event().removeEvents(getDeviceTopic(device));
        entityContext.ui().updateItem(deviceEntity);
    }

    public void deviceUpdated(Z2MDeviceDTO device) {
        this.device = device;
        createOrUpdateDeviceGroup();
        removeRedundantExposes(device);
        for (Options expose : device.getDefinition().getExposes()) {
            // types like switch has no property but has 'type'/'endpoint'/'features'
            if (expose.getProperty() == null) {
                addExposeByFeatures(expose);
            } else {
                addProperty(expose.getProperty(), key -> buildExposeProperty(expose));
            }
        }
        addProperty(Z2MPropertyLastUpdate.KEY, key -> new Z2MPropertyLastUpdate(this));
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
            name = format("${zigbee.device.%s:%s}", defaultIfEmpty(device.getModelId(), device.getDefinition().getModel()),
                device.getDefinition().getDescription());
        } else {
            name = device.getDefinition().getDescription();
        }
        return format("%s(%s) [${%s}]", name, this.device.getIeeeAddress(), defaultIfEmpty(this.deviceEntity.getPlace(), "PLACE_NOT_SET"));
    }

    public Z2MProperty addDynamicProperty(String key, Supplier<Z2MProperty> supplier) {
        return addProperty(key, s -> {
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

                    addProperty(key, s -> missedZ2MProperty);
                    entityContext.ui().updateItem(deviceEntity);
                }
            } catch (Exception ex) {
                log.error("Unable to handle Z2MProperty: {}. Payload: {}. Msg: {}", key, payload, CommonUtils.getErrorMessage(ex));
            }
        }
        if (updated) {
            properties.get(Z2MPropertyLastUpdate.KEY).mqttUpdate(null);
        }
        if (deviceEntity.isLogEvents() && !sb.isEmpty()) {
            entityContext.ui().sendInfoMessage(device.getGroupDescription(), String.join("\n", sb));
        }
    }

    private void removeRedundantExposes(Z2MDeviceDTO device) {
        // remove illuminance_lux if illuminance is present
        if (device.getDefinition().getExposes().stream().anyMatch(e -> "illuminance_lux".equals(e.getName()))
            && device.getDefinition().getExposes().stream().anyMatch(e -> "illuminance".equals(e.getName()))) {
            device.getDefinition().getExposes().removeIf(e -> "illuminance_lux".equals(e.getName()));
        }
    }

    private void addMissingProperties(Z2MLocalCoordinatorService coordinatorService, Z2MDeviceDTO device) {
        for (Pair<String, String> missingProperty : coordinatorService.getMissingProperties(device.getIeeeAddress())) {
            switch (missingProperty.getSecond()) {
                case "action_event":
                    addProperty(missingProperty.getFirst(), key -> Z2MPropertyAction.createActionEvent(key, this, entityContext));
                    break;
            }
        }
    }

    private Z2MProperty addProperty(String key, Function<String, Z2MProperty> propertyProducer) {
        if (!properties.containsKey(key)) {
            properties.put(key, propertyProducer.apply(key));
            entityContext.event().fireEvent(format("zigbee-%s-%s", device.getIeeeAddress(), key), Status.ONLINE);
        }
        return properties.get(key);
    }

    private String getFormatFromPayloadValue(JSONObject payload, String key) {
        try {
            payload.getInt(key);
            return NUMBER_TYPE;
        } catch (Exception ignore) {
        }
        try {
            payload.getBoolean(key);
            return BINARY_TYPE;
        } catch (Exception ignore) {
        }
        return UNKNOWN_TYPE;
    }

    // usually expose.getName() is enough but in case of color - name - 'color_xy' but property is
    // 'color'
    private <T> T getValueFromMap(Map<String, T> map, Options expose) {
        return map.getOrDefault(expose.getName(), map.get(expose.getProperty()));
    }

    private void addExposeByFeatures(Options expose) {
        if (expose.getFeatures() != null) {
            for (Options feature : expose.getFeatures()) {
                addProperty(feature.getProperty(), key -> buildExposeProperty(feature));
            }
        } else {
            log.error("[{}]: Device expose {} has no features", coordinatorService.getEntityID(), expose);
        }
    }

    private Z2MProperty buildExposeProperty(Options expose) {
        Class<? extends Z2MProperty> z2mCluster = getValueFromMap(coordinatorService.getZ2mConverters(), expose);
        Z2MProperty z2MProperty;
        if (z2mCluster == null) {
            Z2MPropertyDTO z2MPropertyDTO = getValueFromMap(ZigBeeUtil.DEVICE_PROPERTIES, expose);
            if (z2MPropertyDTO != null) {
                z2MProperty = new Z2MGeneralProperty(z2MPropertyDTO.getIconColor(), z2MPropertyDTO.getIcon());
                z2MProperty.setUnit(z2MPropertyDTO.getUnit());
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
        entityContext.var().createGroup("z2m", this.deviceEntity.getEntityID(), getDeviceFullName(), true,
            ZigBeeUtil.getDeviceIcon(this.device.getModelId(), "fas fa-server"), ZigBeeUtil.getDeviceIconColor(this.device.getModelId(), UI.Color.random()),
            this.device.getGroupDescription());
    }
}
