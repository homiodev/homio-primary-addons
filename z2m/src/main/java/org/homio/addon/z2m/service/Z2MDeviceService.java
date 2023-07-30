package org.homio.addon.z2m.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.endpoints.Z2MEndpointAction;
import org.homio.addon.z2m.service.endpoints.Z2MEndpointLastSeen;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceStatusEndpoint;
import org.homio.addon.z2m.service.endpoints.inline.Z2MEndpointGeneral;
import org.homio.addon.z2m.service.endpoints.inline.Z2MEndpointUnknown;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextService.MQTTEntityService;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.model.device.ConfigDeviceDefinition;
import org.homio.api.model.device.ConfigDeviceDefinitionService;
import org.homio.api.model.device.ConfigDeviceEndpoint;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.UI;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.api.model.endpoint.DeviceEndpoint.ENDPOINT_DEVICE_STATUS;
import static org.homio.api.model.endpoint.DeviceEndpoint.ENDPOINT_LAST_SEEN;
import static org.homio.api.util.CommonUtils.OBJECT_MAPPER;

@Log4j2
public class Z2MDeviceService {

    public static final ConfigDeviceDefinitionService CONFIG_DEVICE_SERVICE =
            new ConfigDeviceDefinitionService("zigbee-devices.json");

    private final Z2MLocalCoordinatorService coordinatorService;
    @Getter
    private final Map<String, Z2MEndpoint> endpoints = new ConcurrentHashMap<>();
    @Getter
    private final Z2MDeviceEntity deviceEntity;
    @Getter
    private final EntityContext entityContext;
    @Getter
    private String availability;
    @Getter
    private ApplianceModel applianceModel;
    private boolean initialized = false;
    private String currentMQTTTopic;
    private List<ConfigDeviceDefinition> models;

    public Z2MDeviceService(Z2MLocalCoordinatorService coordinatorService, ApplianceModel applianceModel) {
        this.coordinatorService = coordinatorService;
        this.entityContext = coordinatorService.getEntityContext();

        this.deviceEntity = new Z2MDeviceEntity(this, applianceModel.getIeeeAddress());

        deviceUpdated(applianceModel);
        addMissingEndpoints();

        entityContext.ui().updateItem(deviceEntity);
        entityContext.ui().sendSuccessMessage(Lang.getServerMessage("ENTITY_CREATED", "${%s}".formatted(this.applianceModel.getName())));
        setEntityOnline();
    }

    public String getIeeeAddress() {
        return deviceEntity.getIeeeAddress();
    }

    public void dispose() {
        log.warn("[{}]: Dispose zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
        removeMqttListeners();
        entityContext.ui().updateItem(deviceEntity);
        downLinkQualityToZero();
        initialized = false;
    }

    public synchronized void deviceUpdated(ApplianceModel applianceModel) {
        // user changed friendly name via z2m frontend or change file manually - update listeners
        if (initialized && !applianceModel.getMQTTTopic().equals(currentMQTTTopic)) {
            removeMqttListeners();
            addMqttListeners();
        }

        this.applianceModel = applianceModel;
        createOrUpdateDeviceGroup();
        removeRedundantExposes();
        for (Options expose : applianceModel.getDefinition().getExposes()) {
            // types like switch has no endpoint but has 'type'/'endpoint'/'features'
            if (expose.getProperty() == null) {
                addExposeByFeatures(expose);
            } else {
                addEndpointOptional(expose.getProperty(), key -> buildExposeEndpoint(expose));
            }
        }
        addEndpointOptional(ENDPOINT_LAST_SEEN, key -> {
            Z2MEndpointLastSeen lastSeenEndpoint = new Z2MEndpointLastSeen();
            lastSeenEndpoint.init(this, Options.dynamicEndpoint(ENDPOINT_LAST_SEEN, ApplianceModel.NUMBER_TYPE), true);
            return lastSeenEndpoint;
        });
        // add device status
        addEndpointOptional(ENDPOINT_DEVICE_STATUS, key -> new Z2MDeviceStatusEndpoint(this));
    }

    public void setEntityOnline() {
        if (!initialized) {
            log.warn("[{}]: Initialize zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
            addMqttListeners();
            entityContext.event().fireEvent("zigbee-%s".formatted(applianceModel.getIeeeAddress()), deviceEntity.getStatus());
            initialized = true;
        } else {
            log.debug("[{}]: Zigbee device: {} was initialized before", coordinatorService.getEntityID(), deviceEntity.getTitle());
        }
    }

    public ConfigDeviceEndpoint getConfigDeviceEndpoint(String action) {
        return CONFIG_DEVICE_SERVICE.getDeviceEndpoints().get(action);
    }

    public Set<String> getExposes() {
        return endpoints.keySet();
    }

    public void sendRequest(String path, String payload) {
        coordinatorService.sendRequest(path, payload);
    }

    public @NotNull Z2MLocalCoordinatorEntity getCoordinatorEntity() {
        return coordinatorService.getEntity();
    }

    public void updateDeviceConfiguration(Z2MDeviceService deviceService, String endpointName, Object value) {
        coordinatorService.updateDeviceConfiguration(deviceService, endpointName, value);
    }

    public String getModel() {
        return applianceModel.getDefinition().getModel();
    }

    private void addMqttListeners() {
        currentMQTTTopic = applianceModel.getMQTTTopic();
        String topic = getMqttFQDNTopic();

        coordinatorService.getMqttEntityService().addListener(topic, applianceModel.getIeeeAddress(), value -> {
            String payload = value == null ? "" : value.toString();
            if (!payload.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(payload);
                    mqttUpdate(jsonObject);
                } catch (Exception ex) {
                    log.error("[{}]: Unable to parse json for entity: '{}' from: '{}'", coordinatorService.getEntityID(),
                        deviceEntity.getTitle(), payload);
                }
            }
        });

        coordinatorService.getMqttEntityService().addListener(topic + "/availability", applianceModel.getIeeeAddress(), payload -> {
            availability = payload == null ? null : payload.toString();
            entityContext.event().fireEvent("zigbee-%s".formatted(applianceModel.getIeeeAddress()), deviceEntity.getStatus());
            entityContext.ui().updateItem(deviceEntity);
            if ("offline".equals(availability)) {
                downLinkQualityToZero();
            }
        });
    }

    private String getMqttFQDNTopic() {
        return "%s/%s".formatted(coordinatorService.getEntity().getBasicTopic(), currentMQTTTopic);
    }

    private void removeMqttListeners() {
        MQTTEntityService mqttEntityService = coordinatorService.getMqttEntityService();
        if (mqttEntityService != null) { // may be NPE during dispose if device not configured yet
            String topic = getMqttFQDNTopic();
            mqttEntityService.removeListener(topic, applianceModel.getIeeeAddress());
            mqttEntityService.removeListener(topic + "/availability", applianceModel.getIeeeAddress());
        }
    }

    @Override
    public String toString() {
        return applianceModel.toString();
    }

    public JsonNode getConfiguration() {
        return coordinatorService
            .getConfiguration()
            .getDevices()
            .getOrDefault(applianceModel.getIeeeAddress(), OBJECT_MAPPER.createObjectNode());
    }

    public void updateConfiguration(String key, Object value) {
        coordinatorService.updateDeviceConfiguration(this, key, value);
    }

    public void publish(@NotNull String topic, @NotNull JSONObject payload) {
        if (topic.startsWith("bridge/'")) {
            coordinatorService.publish(topic, payload);
        } else {
            coordinatorService.publish(currentMQTTTopic + "/" + topic, payload);
        }
    }

    public String getDeviceFullName() {
        String name;
        if (getConfiguration().has("name")) {
            name = getConfiguration().get("name").asText();
        } else if (getModel() != null) {
            name = "${zigbee.device.%s~%s}".formatted(getModel(), applianceModel.getDefinition().getDescription());
        } else {
            name = applianceModel.getDefinition().getDescription();
        }
        return "%s(%s) [${%s}]".formatted(name, applianceModel.getIeeeAddress(), defaultIfEmpty(deviceEntity.getPlace(), "place_not_set"));
    }

    public Z2MEndpoint addDynamicEndpoint(String key, Supplier<Z2MEndpoint> supplier) {
        return addEndpointOptional(key, s -> {
            Z2MEndpoint endpoint = supplier.get();
            // store missing endpoint in file to next reload
            coordinatorService.addMissingEndpoints(deviceEntity.getIeeeAddress(), endpoint);
            return endpoint;
        });
    }

    private void mqttUpdate(JSONObject payload) {
        boolean updated = false;
        List<String> sb = new ArrayList<>();
        for (String key : payload.keySet()) {
            try {
                boolean feed = false;
                for (Z2MEndpoint endpoint : endpoints.values()) {
                    if (endpoint.feedPayload(key, payload)) {
                        feed = true;
                        updated = true;
                        sb.add("%s - %s".formatted(endpoint.getDescription(), endpoint.getValue().toString()));
                    }
                }
                if (!feed) {
                    Set<String> ignoreExposes = getListOfRedundantEndpoints();
                    if (!ignoreExposes.contains(key)) {
                        log.info("[{}]: Create dynamic created endpoint '{}'. Device: {}", coordinatorService.getEntityID(), key,
                                applianceModel.getIeeeAddress());
                        Z2MEndpoint missedZ2MEndpoint = buildExposeEndpoint(Options.dynamicEndpoint(key, getFormatFromPayloadValue(payload, key)));
                        missedZ2MEndpoint.mqttUpdate(payload);

                        addEndpointOptional(key, s -> missedZ2MEndpoint);
                        entityContext.ui().updateItem(deviceEntity);
                    }
                }
            } catch (Exception ex) {
                log.error("Unable to handle Z2MEndpoint: {}. Payload: {}. Msg: {}",
                        key, payload, CommonUtils.getErrorMessage(ex));
            }
        }
        if (updated && !payload.keySet().contains(ENDPOINT_LAST_SEEN)) {
            // fire set value as current milliseconds if device has no last_seen endpoint for some reason
            endpoints.get(ENDPOINT_LAST_SEEN).mqttUpdate(null);
        }
        if (deviceEntity.isLogEvents() && !sb.isEmpty()) {
            entityContext.ui().sendInfoMessage(applianceModel.getGroupDescription(), String.join("\n", sb));
        }
    }

    /**
     * Remove exposes from z2m device in such cases:
     * <p>
     * 1) expose exists inside file 'zigbee-devices.json' array 'ignoreEndpoints'
     * <p>
     * 2) expose exists inside 'alias' array inside another expose in file 'zigbee-devices.json'
     */
    private void removeRedundantExposes() {
        Set<String> ignoreExposes = getListOfRedundantEndpoints();

        applianceModel.getDefinition().getExposes().removeIf(e -> {
            if (CONFIG_DEVICE_SERVICE.isIgnoreEndpoint(e.getName()) || ignoreExposes.contains(e.getName())) {
                log.info("[{}]: ({}): Skip endpoint: {}",
                        coordinatorService.getEntityID(),
                        applianceModel.getIeeeAddress(), e.getName());
                return true;
            }
            return false;
        });
    }

    @NotNull
    private Set<String> getListOfRedundantEndpoints() {
        return Stream.concat(
                        endpoints.keySet().stream(),
                        applianceModel.getDefinition().getExposes().stream().map(Options::getName))
                .map(this::getConfigDeviceEndpoint)
                .filter(p -> p != null && p.getAlias() != null)
                .flatMap(p -> p.getAlias().stream())
                .collect(Collectors.toSet());
    }

    private void addMissingEndpoints() {
        for (Pair<String, String> missingEndpoints : coordinatorService.getMissingEndpoints(applianceModel.getIeeeAddress())) {
            if ("action_event".equals(missingEndpoints.getValue())) {
                addEndpointOptional(missingEndpoints.getKey(), key -> Z2MEndpointAction.createActionEvent(key, this, entityContext));
            }
        }
    }

    private Z2MEndpoint addEndpointOptional(String key, Function<String, Z2MEndpoint> endpointProducer) {
        if (!endpoints.containsKey(key)) {
            endpoints.put(key, endpointProducer.apply(key));
            entityContext.event().fireEvent("zigbee-%s-%s".formatted(applianceModel.getIeeeAddress(), key), Status.ONLINE);
        }
        return endpoints.get(key);
    }

    private String getFormatFromPayloadValue(JSONObject payload, String key) {
        try {
            payload.getInt(key);
            return ApplianceModel.NUMBER_TYPE;
        } catch (Exception ignore) {
        }
        try {
            payload.getBoolean(key);
            return ApplianceModel.BINARY_TYPE;
        } catch (Exception ignore) {
        }
        return ApplianceModel.UNKNOWN_TYPE;
    }

    // usually expose.getName() is enough but in case of color - name - 'color_xy' but endpoint is
    // 'color'
    private <T> T getValueFromMap(Map<String, T> map, Options expose) {
        return map.getOrDefault(expose.getName(), map.get(expose.getProperty()));
    }

    private void addExposeByFeatures(Options expose) {
        if (expose.getFeatures() != null) {
            for (Options feature : expose.getFeatures()) {
                addEndpointOptional(feature.getProperty(), key -> buildExposeEndpoint(feature));
            }
        } else {
            log.error("[{}]: Device expose {} has no features", coordinatorService.getEntityID(), expose);
        }
    }

    private Z2MEndpoint buildExposeEndpoint(Options expose) {
        Class<? extends Z2MEndpoint> z2mCluster = getValueFromMap(Z2MLocalCoordinatorService.getAllEndpoints(), expose);
        Z2MEndpoint z2MEndpoint;
        if (z2mCluster == null) {
            ConfigDeviceEndpoint configDeviceEndpoint = getValueFromMap(CONFIG_DEVICE_SERVICE.getDeviceEndpoints(), expose);
            if (configDeviceEndpoint != null) {
                z2MEndpoint = new Z2MEndpointGeneral(configDeviceEndpoint.getIcon(), configDeviceEndpoint.getIconColor());
                z2MEndpoint.setUnit(configDeviceEndpoint.getUnit());
            } else {
                z2MEndpoint = new Z2MEndpointUnknown();
            }
        } else {
            z2MEndpoint = CommonUtils.newInstance(z2mCluster);
        }
        boolean createVariable = CONFIG_DEVICE_SERVICE.isEndpointHasVariable(expose.getProperty());
        z2MEndpoint.init(this, expose, createVariable);
        return z2MEndpoint;
    }

    private void createOrUpdateDeviceGroup() {
        entityContext.var().createGroup("z2m", "ZigBee2MQTT", true, new Icon("fab fa-laravel", "#ED3A3A"));

        Icon icon = new Icon(
                CONFIG_DEVICE_SERVICE.getDeviceIcon(findDevices(), "fas fa-server"),
                CONFIG_DEVICE_SERVICE.getDeviceIconColor(findDevices(), UI.Color.random())
        );
        entityContext.var().createGroup("z2m", deviceEntity.getEntityID(), getDeviceFullName(), true,
                icon, applianceModel.getGroupDescription());
    }

    public @NotNull List<ConfigDeviceDefinition> findDevices() {
            if (models == null) {
            models = CONFIG_DEVICE_SERVICE.findDeviceDefinitionModels(getModel(), getExposes());
        }
        return models;
    }

    private void downLinkQualityToZero() {
        Optional.ofNullable(endpoints.get(Z2MEndpointGeneral.ENDPOINT_SIGNAL)).ifPresent(endpoint -> {
            if (!endpoint.getValue().stringValue().equals("0")) {
                endpoint.setValue(new DecimalType(0));
            }
        });
    }
}
