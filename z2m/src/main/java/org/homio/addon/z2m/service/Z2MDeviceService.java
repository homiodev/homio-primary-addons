package org.homio.addon.z2m.service;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.addon.z2m.util.ApplianceModel.BINARY_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.NUMBER_TYPE;
import static org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options.dynamicEndpoint;
import static org.homio.api.model.Status.ONLINE;
import static org.homio.api.model.endpoint.DeviceEndpoint.ENDPOINT_DEVICE_STATUS;
import static org.homio.api.model.endpoint.DeviceEndpoint.ENDPOINT_LAST_SEEN;
import static org.homio.api.util.JsonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.endpoints.Z2MDeviceEndpointAction;
import org.homio.addon.z2m.service.endpoints.Z2MDeviceEndpointLastSeen;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceEndpointGeneral;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceEndpointUnknown;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceStatusDeviceEndpoint;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.Context;
import org.homio.api.ContextService.MQTTEntityService;
import org.homio.api.model.Icon;
import org.homio.api.model.device.ConfigDeviceDefinition;
import org.homio.api.model.device.ConfigDeviceDefinitionService;
import org.homio.api.model.device.ConfigDeviceEndpoint;
import org.homio.api.state.DecimalType;
import org.homio.api.state.StringType;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Log4j2
public class Z2MDeviceService {

    public static final ConfigDeviceDefinitionService CONFIG_DEVICE_SERVICE =
            new ConfigDeviceDefinitionService("zigbee-devices.json");

    private final Z2MLocalCoordinatorService coordinatorService;
    @Getter
    private final Map<String, Z2MDeviceEndpoint> endpoints = new ConcurrentHashMap<>();
    @Getter
    private final Z2MDeviceEntity deviceEntity;
    @Getter
    @Accessors(fluent = true)
    private final Context context;
    @Getter
    private String availability;
    @Getter
    private ApplianceModel applianceModel;
    private boolean initialized = false;
    private String currentMQTTTopic;
    private List<ConfigDeviceDefinition> models;

    public Z2MDeviceService(Z2MLocalCoordinatorService coordinatorService, ApplianceModel applianceModel) {
        this.coordinatorService = coordinatorService;
        this.context = coordinatorService.context();

        this.deviceEntity = new Z2MDeviceEntity(this, applianceModel.getIeeeAddress());

        deviceUpdated(applianceModel);
        addMissingEndpoints();

        context.ui().updateItem(deviceEntity);
        context.ui().toastr().success(Lang.getServerMessage("ENTITY_CREATED", "${%s}".formatted(this.applianceModel.getName())));
        setEntityOnline();
    }

    public String getIeeeAddress() {
        return deviceEntity.getIeeeAddress();
    }

    public void dispose() {
        log.warn("[{}]: Dispose zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
        removeMqttListeners();
        context.ui().updateItem(deviceEntity);
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
            Z2MDeviceEndpointLastSeen lastSeenEndpoint = new Z2MDeviceEndpointLastSeen(context);
            lastSeenEndpoint.init(this, dynamicEndpoint(ENDPOINT_LAST_SEEN, NUMBER_TYPE));
            return lastSeenEndpoint;
        });
        // add device status
        addEndpointOptional(ENDPOINT_DEVICE_STATUS, key -> new Z2MDeviceStatusDeviceEndpoint(this));
    }

    public void setEntityOnline() {
        if (!initialized) {
            log.info("[{}]: Initialize zigbee device: {}", coordinatorService.getEntityID(), deviceEntity.getTitle());
            addMqttListeners();
            context.event().fireEvent("zigbee-%s".formatted(applianceModel.getIeeeAddress()),
                new StringType(deviceEntity.getStatus().toString()));
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

        coordinatorService.getMqttEntityService().addPayloadListener(Set.of(topic), "asd",
            "", log, Level.DEBUG, (tpc, payload) -> mqttUpdate(payload));

        coordinatorService.getMqttEntityService().addListener(topic + "/availability", applianceModel.getIeeeAddress(), payload -> {
            availability = payload;
            context.event().fireEvent("zigbee-%s".formatted(applianceModel.getIeeeAddress()),
                new StringType(deviceEntity.getStatus().toString()));
            context.ui().updateItem(deviceEntity);
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
        return "%s(%s) [${%s}]".formatted(name, applianceModel.getIeeeAddress(), defaultIfEmpty(deviceEntity.getPlace(), "W.ERROR.PLACE_NOT_SET"));
    }

    public Z2MDeviceEndpoint addDynamicEndpoint(String key, Supplier<Z2MDeviceEndpoint> supplier) {
        return addEndpointOptional(key, s -> {
            Z2MDeviceEndpoint endpoint = supplier.get();
            // store missing endpoint in file to next reload
            coordinatorService.addMissingEndpoints(deviceEntity.getIeeeAddress(), endpoint);
            return endpoint;
        });
    }

    private void mqttUpdate(JsonNode payload) {
        boolean updated = false;
        List<String> sb = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        payload.fieldNames().forEachRemaining(keys::add);

        for (String key : keys) {
            try {
                boolean feed = false;
                for (Z2MDeviceEndpoint endpoint : endpoints.values()) {
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
                        Z2MDeviceEndpoint missedEndpoint = buildExposeEndpoint(dynamicEndpoint(key, getFormatFromPayloadValue(payload, key)));
                        missedEndpoint.mqttUpdate(payload);

                        addEndpointOptional(key, s -> missedEndpoint);
                        context.ui().updateItem(deviceEntity);
                    }
                }
            } catch (Exception ex) {
                log.error("Unable to handle Z2MDeviceEndpoint: {}. Payload: {}. Msg: {}",
                    key, payload, CommonUtils.getErrorMessage(ex));
            }
        }
        if (updated && !keys.contains(ENDPOINT_LAST_SEEN)) {
            // fire set value as current milliseconds if device has no last_seen endpoint for some reason
            endpoints.get(ENDPOINT_LAST_SEEN).mqttUpdate(null);
        }
        if (deviceEntity.isLogEvents() && !sb.isEmpty()) {
            context.ui().toastr().info(applianceModel.getGroupDescription(), String.join("\n", sb));
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
                addEndpointOptional(missingEndpoints.getKey(), key -> Z2MDeviceEndpointAction.createActionEvent(key, this, context));
            }
        }
    }

    private Z2MDeviceEndpoint addEndpointOptional(String key, Function<String, Z2MDeviceEndpoint> endpointProducer) {
        if (!endpoints.containsKey(key)) {
            endpoints.put(key, endpointProducer.apply(key));
            context.event().fireEvent("endpoint-%s-%s".formatted(applianceModel.getIeeeAddress(), key),
                new StringType(ONLINE.toString()));
        }
        return endpoints.get(key);
    }

    private String getFormatFromPayloadValue(JsonNode payload, String key) {
        JsonNode jsonNode = payload.get(key);
        if (jsonNode.isBoolean()) {
            return BINARY_TYPE;
        } else if (jsonNode.isNumber()) {
            return NUMBER_TYPE;
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

    private Z2MDeviceEndpoint buildExposeEndpoint(Options expose) {
        Class<? extends Z2MDeviceEndpoint> z2mCluster = getValueFromMap(Z2MLocalCoordinatorService.allEndpoints, expose);
        Z2MDeviceEndpoint endpoint;
        if (z2mCluster == null) {
            ConfigDeviceEndpoint configDeviceEndpoint = getValueFromMap(CONFIG_DEVICE_SERVICE.getDeviceEndpoints(), expose);
            if (configDeviceEndpoint != null) {
                endpoint = new Z2MDeviceEndpointGeneral(configDeviceEndpoint.getIcon(), configDeviceEndpoint.getIconColor(), context);
            } else {
                endpoint = new Z2MDeviceEndpointUnknown(context);
            }
        } else {
            endpoint = CommonUtils.newInstance(z2mCluster, context);
        }
        endpoint.init(this, expose);
        return endpoint;
    }

    private void createOrUpdateDeviceGroup() {
        context.var().createGroup("z2m", "ZigBee2MQTT", builder ->
            builder.setLocked(true).setIcon(new Icon("fab fa-laravel", "#ED3A3A")));
        context.var().createSubGroup("z2m", requireNonNull(deviceEntity.getIeeeAddress()), getDeviceFullName(), builder ->
            builder.setDescription(applianceModel.getGroupDescription()).setLocked(true).setIcon(deviceEntity.getEntityIcon()));
    }

    public @NotNull List<ConfigDeviceDefinition> findDevices() {
        if (models == null) {
            models = CONFIG_DEVICE_SERVICE.findDeviceDefinitionModels(getModel(), getExposes());
        }
        return models;
    }

    private void downLinkQualityToZero() {
        Optional.ofNullable(endpoints.get(Z2MDeviceEndpointGeneral.ENDPOINT_SIGNAL)).ifPresent(endpoint -> {
            if (!endpoint.getValue().stringValue().equals("0")) {
                endpoint.setValue(new DecimalType(0), false);
            }
        });
    }
}
