package org.homio.addon.z2m.service;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.homio.addon.z2m.service.Z2MDeviceService.CONFIG_DEVICE_SERVICE;
import static org.homio.addon.z2m.util.ZigBeeUtil.zigbee2mqttGitHub;
import static org.homio.api.util.CommonUtils.getErrorMessage;
import static org.homio.api.util.JsonUtils.OBJECT_MAPPER;
import static org.homio.api.util.JsonUtils.YAML_OBJECT_MAPPER;
import static org.homio.api.util.JsonUtils.updateJsonPath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pivovarit.function.ThrowingBiConsumer;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.homio.addon.z2m.ZigBee2MQTTFrontendConsolePlugin;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceEndpointInline;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.Context;
import org.homio.api.ContextBGP;
import org.homio.api.ContextBGP.ProcessContext;
import org.homio.api.ContextBGP.ThreadContext;
import org.homio.api.ContextService.MQTTEntityService;
import org.homio.api.ContextUI;
import org.homio.api.console.ConsolePluginFrame.FrameConfiguration;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.HasEntityIdentifier;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService.ServiceInstance;
import org.homio.api.state.StringType;
import org.homio.api.ui.UI.Color;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * The {@link Z2MLocalCoordinatorService} is responsible for handling commands, which are sent to one of the zigbeeRequireEndpoints.
 * <p>
 * This is the base coordinator handler. It handles the majority of the interaction with the ZigBeeNetworkManager.
 * <p>
 * The interface coordinators are responsible for opening a ZigBeeTransport implementation and passing this to the {@link Z2MLocalCoordinatorService}.
 */
@Getter
public class Z2MLocalCoordinatorService extends ServiceInstance<Z2MLocalCoordinatorEntity>
        implements HasEntityIdentifier {

    public static final Map<String, Class<? extends Z2MDeviceEndpoint>> allEndpoints = new HashMap<>();
    private final Path zigbee2mqttConfigurationPath = zigbee2mqttGitHub
            .getLocalProjectPath().resolve("data/configuration.yaml");
    private final AtomicBoolean scanStarted = new AtomicBoolean(false);
    // Map ieeeAddress - Z2MDeviceService
    private final Map<String, Z2MDeviceService> deviceHandlers = new ConcurrentHashMap<>();
    private boolean initialized;
    private MQTTEntityService mqttEntityService;
    private URL frontendURL;

    private ThreadContext<Void> checkFrontendTC;

    private ProcessContext processContext;
    private Z2MConfiguration configuration;
    private boolean isRunningLocally;

    public Z2MLocalCoordinatorService(@NotNull Context context, @NotNull Z2MLocalCoordinatorEntity entity) {
        super(context, entity, true);
    }

    @SneakyThrows
    public ActionResponseModel reinstallZ2M() {
        this.dispose(null);
        context.event().ensureInternetUp("Unable to reinstall z2m without internet");
        zigbee2mqttGitHub.deleteProject();
        ZigBeeUtil.installZ2M(context);
        return forceRestartCoordinator();
    }

    public ActionResponseModel updateFirmware(ProgressBar progressBar, String version) {
        if (!version.equals(entity.getFirmwareVersion())) {
            dispose(null);
            return zigbee2mqttGitHub.updateProject("zigbee2mqtt", progressBar, true, projectUpdate -> {
                if (!version.equals(zigbee2mqttGitHub.getInstalledVersion(context))) {
                    ZigBeeUtil.installOrUpdateZ2M(context, version, projectUpdate);
                    forceRestartCoordinator();
                }
                return ActionResponseModel.fired();
            }, null);
        }
        return ActionResponseModel.showError("Z2M same version");
    }

    public void dispose(@Nullable Exception ex) {
        if (!initialized) {
            return;
        }
        initialized = false;
        if (ex != null) {
            this.entity.setStatusError(ex);
        } else {
            this.entity.setStatus(Status.OFFLINE, null);
        }
        ContextBGP.cancel(checkFrontendTC);
        ContextBGP.cancel(processContext);

        log.warn("[{}]: Dispose coordinator", entityID, ex);

        context.ui().console().unRegisterPlugin("zigbee2mqtt-console-" + entityID);
        context.ui().toastr().warn("Dispose zigBee coordinator");
        context.ui().removeHeaderButton("discover-" + entityID);
        for (Z2MDeviceService deviceHandler : deviceHandlers.values()) {
            deviceHandler.dispose();
        }

        updateNotificationBlock();
    }

    @Override
    public String isRequireRestartService() {
        if (zigbee2mqttGitHub.isUpdating()) {return null;}
        if (!entity.getStatus().isOnline()) {return "Status: " + entity.getStatus();}
        int status = getApiStatus();
        if (status != 200) {return "API status[%s]".formatted(status);}
        return null;
    }

    @Override
    public void restartService() {
        restartZ2M();
    }

    @SneakyThrows
    public void initializeZ2M() {
        initialized = false;
        log.info("[{}]: Initializing ZigBee network.", entityID);

        mqttEntityService = entity.getMqttEntityService();
        runZigBee2MQTT();
    }

    public ActionResponseModel startScan() {
        synchronized (scanStarted) {
            if (scanStarted.get()) {
                throw new IllegalStateException("ZIGBEE.ERROR.SCAN_ALREADY_STARTED");
            }
            if (!entity.getStatus().isOnline()) {
                throw new IllegalStateException("ZIGBEE.ERROR.COORDINATOR_OFFLINE");
            }
            log.info("[{}]: Start scanning...", entityID);
            scanStarted.set(true);
            int duration = entity.getDiscoveryDuration();

            try {
                sendRequest("permit_join", new JSONObject().put("value", true).put("time", duration).toString());
                ZigBeeUtil.zigbeeScanStarted(context, entityID, duration,
                        () -> scanStarted.set(false),
                        () -> {
                            sendRequest("permit_join", new JSONObject().put("value", false).toString());
                            scanStarted.set(false);
                            context.ui().removeHeaderButton("zigbee-scan-" + entityID);
                        });
            } catch (Exception ex) {
                log.error("[{}]: Unable to send request to discover devices. {}", entityID, getErrorMessage(ex));
                return ActionResponseModel.showError(ex);
            }
            return ActionResponseModel.showSuccess("ZIGBEE.START_SUCCESS");
        }
    }

    public ActionResponseModel forceRestartCoordinator() {
        entity.setRestartToken(System.currentTimeMillis());
        context.db().save(entity);
        return ActionResponseModel.fired();
    }

    public void updateNotificationBlock() {
        context.ui().notification().addBlock(entityID, "ZigBee2MQTT", new Icon("fas fa-bezier-curve", "#899343"), builder -> {
            builder.setStatus(entity.getStatus());
            builder.linkToEntity(entity);
            builder.setUpdatable(entity);

            if (entity.getStatus().isOnline()) {
                builder.addInfo("ACTION.SUCCESS", new Icon("fas fa-seedling", Color.GREEN));
            } else {
                builder.addErrorStatusInfo(entity.getStatusMessage());
            }
            addUpdateButton(builder);

            builder.contextMenuActionBuilder(contextAction -> {
                if (!entity.isStart()) {
                    contextAction.addSelectableButton("START", new Icon("fas fa-play", Color.PRIMARY_COLOR), (ec, params) -> {
                        if (!entity.isStart()) {
                            context.db().save(entity.setStart(true));
                            return ActionResponseModel.success();
                        }
                        return ActionResponseModel.showWarn("W.ERROR.COORDINATOR_ALREADY_STARTED");
                    });
                }
                contextAction.addSelectableButton("ZIGBEE_START_SCAN", new Icon("fas fa-search-location", "#899343"),
                        (ec, params) -> entity.scan());
                contextAction.addSelectableButton("RESTART", new Icon("fas fa-power-off", Color.RED),
                        (ec, params) -> restartZ2M());
            });

            List<Z2MDeviceEntity> devices = deviceHandlers.values().stream()
                    .map(Z2MDeviceService::getDeviceEntity)
                    .collect(Collectors.toList());
            builder.setDevices(devices);
        });
    }

    @SneakyThrows
    public ActionResponseModel restartZ2M() {
        if (!entity.isStart()) {
            return ActionResponseModel.showError("ERROR.NOT_STARTED", entity.getTitle());
        }
        log.info("Request restart z2m");
        if (mqttEntityService != null) {
            log.info("Send mqtt request to restart z2m");
            sendRequest("restart", "");
            // wait, maybe z2m back online?
            Thread.sleep(5000);
        }
        return forceRestartCoordinator();
    }

    @Override
    public void destroy(boolean forRestart, Exception ex) {
        this.dispose(ex);
    }

    @Override
    protected void firstInitialize() {
        try {
            assembleAllEndpoints(context);
            initialize();
        } catch (Exception ex) {
            updateNotificationBlock();
            throw ex;
        }
    }

    @Override
    protected void initialize() {
        ZigBeeUtil.installZ2M(context);
        initializeZ2M();
        updateNotificationBlock();
    }

    @SneakyThrows
    public void updateDeviceConfiguration(Z2MDeviceService deviceService, String endpointName, Object value) {
        if (!this.initialized) {
            throw new IllegalStateException("ZIGBEE.ERROR.COORDINATOR_OFFLINE");
        }
        ObjectNode deviceConfiguration = this.configuration.getDevices().computeIfAbsent(deviceService.getIeeeAddress(),
                ieee -> node(deviceService.getIeeeAddress()));
        if (value == null) {
            deviceConfiguration.remove(endpointName);
        } else {
            deviceConfiguration.putPOJO(endpointName, value);
        }
        saveConfiguration();
        // update value in runtime
        publish(
                "bridge/config/device_options",
                new JSONObject()
                        .put("friendly_name", deviceService.getIeeeAddress())
                        .put("options", new JSONObject().put(endpointName, value)));
    }

    @SneakyThrows
    public void publish(String topic, JSONObject payload) {
        mqttEntityService.publish(entity.getBasicTopic() + "/" + topic, payload.toString().getBytes(), 0, false);
    }

    public void addMissingEndpoints(String ieeeAddress, Z2MDeviceEndpoint endpoint) {
        ObjectNode deviceConfiguration = configuration.getDevices().computeIfAbsent(ieeeAddress,
                ieee -> node(ieeeAddress));
        String extraActions = deviceConfiguration.path("missingActions").asText("");
        for (String action : extraActions.split(";")) {
            if (action.startsWith(endpoint.getEndpointEntityID() + ":")) {
                log.warn("Endpoint '{}' already exists in configuration for device: '{}'",
                        endpoint.getEndpointEntityID(), ieeeAddress);
                return;
            }
        }
        extraActions += "%s:%s;".formatted(endpoint.getEndpointEntityID(), endpoint.getEndpointDefinition());
        deviceConfiguration.put("missingActions", extraActions);
        saveConfiguration();
    }

    public List<Pair<String, String>> getMissingEndpoints(String ieeeAddress) {
        ObjectNode device = configuration.getDevices().get(ieeeAddress);
        if (device != null) {
            return Stream.of(device.path("missingActions").asText("").split(";"))
                    .filter(s -> !s.isEmpty()).map(s -> {
                        String[] actionItems = s.split(":");
                        return Pair.of(actionItems[0], actionItems[1]);
                    }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    private void runZigBee2MQTT() {
        try {
            // create configuration file if not exists
            if (zigbee2mqttConfigurationPath.toFile().createNewFile()) {
                log.info("[{}]: Created zigbee2mqtt configuration file: {}", entityID, zigbee2mqttConfigurationPath);
            }

            syncConfiguration();

            isRunningLocally = getApiStatus() != 200;
            if (isRunningLocally) {
                startZ2MLocalProcess();
            } else {
                setEntityOnline();
            }
        } catch (Exception ex) {
            log.error("[{}]: Error while start zigbee2mqtt {}", entityID, getErrorMessage(ex));
            dispose(ex);
        }
        updateNotificationBlock();
    }

    private void setEntityOnline() {
        initialized = true;
        entity.setStatusOnline();
        for (Z2MDeviceService deviceService : deviceHandlers.values()) {
            deviceService.setEntityOnline();
        }

        context.ui().headerButtonBuilder("discover-" + entityID)
                .title("ZIGBEE_START_SCAN")
                .icon(new Icon("fas fa-search-location", "#3E7BBD"))
                .availableForPage(ZigBeeDeviceBaseEntity.class)
                .clickAction(this::startScan).build();

        // register frame console
        checkFrontendTC = context.bgp().builder("z2m-check-frontend-" + getEntityID())
                .delay(Duration.ofSeconds(10)).execute(() ->
                context.ui().console().registerPlugin("zigbee2mqtt-frontend-" + entityID,
                    new ZigBee2MQTTFrontendConsolePlugin(context, new FrameConfiguration(frontendURL.toString()))));

        // register mqtt bridge event listeners
        for (Z2MBridgeTopicHandlers handler : Z2MBridgeTopicHandlers.values()) {
            addMqttTopicListener(getBridgeTopic(handler), (topic, payload) -> handler.handler.accept(payload, this), handler.logLevel);
        }
        for (Z2MBridgeResponseTopicHandlers response : Z2MBridgeResponseTopicHandlers.values()) {
            addMqttTopicListener(
                    "%s/bridge/response/%s".formatted(entity.getBasicTopic(), response.topic),
                (topic, payload) -> {
                        if ("ok".equalsIgnoreCase(payload.get("status").asText())) {
                            response.handler.accept(payload.get("data"), this);
                        } else {
                            log.error("[{}]: ZigBee2MQTT {} response status failed. {}", entityID, response.topic, payload);
                        }
                    }, response.logLevel);
        }
    }

    private void addMqttTopicListener(String topic, ThrowingBiConsumer<String, ObjectNode, Exception> handler, Level logLevel) {
        mqttEntityService.addPayloadListener(Set.of(topic), "z2m", entityID, log, logLevel, handler);
    }

    private int getApiStatus() {
        try {
            URL url = new URL("http://localhost:%d".formatted(configuration.getFrontend().getPort()));
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            return huc.getResponseCode();
        } catch (Exception ignore) {
        }
        return 500;
    }

    @SneakyThrows
    public void sendRequest(String path, String payload) {
        String topic = "%s/bridge/request/%s".formatted(entity.getBasicTopic(), path);
        mqttEntityService.publish(topic, payload.getBytes(), 0, false);
    }

    private String getNpm() {
        return SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
    }

    @SneakyThrows
    private void syncConfiguration() {
        try {
            configuration = YAML_OBJECT_MAPPER.readValue(zigbee2mqttConfigurationPath.toFile(), Z2MConfiguration.class);
            if (configuration == null) {
                configuration = new Z2MConfiguration();
            }
        } catch (Exception ex) {
            log.error("[{}]: Unable to read zigbee2mqtt configuration file: {}", entityID, zigbee2mqttConfigurationPath.toAbsolutePath(), ex);
            configuration = new Z2MConfiguration();
        }
        this.frontendURL = new URL("http://localhost:%d".formatted(configuration.getFrontend().getPort()));

        JsonNode availability = configuration.getOrCreateObjectNode("availability");
        boolean updated = updateJsonPath(availability, "active/timeout", entity.getAvailabilityActiveTimeout());
        updated |= updateJsonPath(availability, "passive/timeout", entity.getAvailabilityPassiveTimeout() * 60);

        if (configuration.isPermitJoin() != entity.isPermitJoin()) {
            configuration.setPermitJoin(entity.isPermitJoin());
            updated = true;
        }
        if (!Objects.equals(
                configuration.getMqtt().getBaseTopic(), trimToNull(entity.getBasicTopic()))) {
            configuration.getMqtt().setBaseTopic(trimToNull(entity.getBasicTopic()));
            updated = true;
        }
        String server = "mqtt://%s:%d".formatted(mqttEntityService.getHostname(), mqttEntityService.getPort());
        if (!Objects.equals(configuration.getMqtt().getServer(), server)) {
            configuration.getMqtt().setServer(server);
            updated = true;
        }
        if (!Objects.equals(
                configuration.getMqtt().getUser(), trimToNull(mqttEntityService.getUser()))) {
            configuration.getMqtt().setUser(trimToNull(mqttEntityService.getUser()));
            updated = true;
        }
        if (!Objects.equals(
                configuration.getMqtt().getPassword(),
                trimToNull(mqttEntityService.getPassword().asString()))) {
            configuration.getMqtt().setPassword(trimToNull(mqttEntityService.getPassword().asString()));
            updated = true;
        }
        if (!Objects.equals(configuration.getSerial().getPort(), trimToNull(entity.getPort()))) {
            configuration.getSerial().setPort(entity.getPort());
            updated = true;
        }
        if (updated) {
            saveConfiguration();
        }
    }

    private ObjectNode node(String ieeeAddress) {
        return OBJECT_MAPPER.createObjectNode().put("friendly_name", ieeeAddress);
    }

    @SneakyThrows
    private void saveConfiguration() {
        try (OutputStream outputStream = Files.newOutputStream(zigbee2mqttConfigurationPath, StandardOpenOption.TRUNCATE_EXISTING)) {
            YAML_OBJECT_MAPPER.writeValue(outputStream, configuration);
        }
    }

    private void deviceRemoved(Z2MDeviceService deviceService) {
        log.info("[{}]: Device removed: {}", entityID, deviceService);
        context.ui().updateItem(entity);
        deviceService.dispose();
        deviceHandlers.remove(deviceService.getIeeeAddress());
    }

    private @NotNull String getBridgeTopic(@NotNull Z2MLocalCoordinatorService.Z2MBridgeTopicHandlers z2MBridgeTopicHandlers) {
        return "%s/bridge/%s".formatted(entity.getBasicTopic(), z2MBridgeTopicHandlers.name());
    }

    private void startZ2MLocalProcess() {
        AtomicReference<Level> infoLevel = new AtomicReference<>(Level.INFO);

        processContext =
            context.bgp().processBuilder(entity, log)
                   .setInputLoggerOutput(msg -> {
                       if (msg.contains("!!!!")) {
                           infoLevel.set(Level.ERROR);
                       }
                       log.log(infoLevel.get(), "[{}]: ZigBee2MQTT: {}", entityID, msg);
                   })
                   .execute(getNpm() + " start --prefix " + zigbee2mqttGitHub.getLocalProjectPath());
    }

    private void assembleAllEndpoints(Context context) {
        if (allEndpoints.isEmpty()) {
            List<Class<? extends Z2MDeviceEndpoint>> z2mClusters = context.getClassesWithParent(Z2MDeviceEndpoint.class);
            for (Class<? extends Z2MDeviceEndpoint> z2mCluster : z2mClusters) {
                if (!Z2MDeviceEndpointInline.class.isAssignableFrom(z2mCluster)) {
                    Z2MDeviceEndpoint endpoint = CommonUtils.newInstance(z2mCluster, context);
                    allEndpoints.put(endpoint.getEndpointDefinition(), z2mCluster);
                }
            }
        }
    }

    private void addUpdateButton(ContextUI.NotificationBlockBuilder builder) {
        if (!CONFIG_DEVICE_SERVICE.isEqualServerConfig()) {
            builder.addInfo("ZIGBEE.CONFIG_OUTDATED", null).setRightButton(new Icon("fas fa-download"), "UPDATE",
                    "W.CONFIRM.Z2M_DOWNLOAD_CONFIG", (ec, params) -> {
                        CONFIG_DEVICE_SERVICE.syncConfigurationFile();
                        return ActionResponseModel.fired();
                    });
        }
    }

    @RequiredArgsConstructor
    private enum Z2MBridgeTopicHandlers {
        config(Level.INFO, (payload, service) -> {
        }),
        devices(Level.INFO, (payload, service) -> {
            String value = payload.get("raw").asText();
            List<ApplianceModel> models = OBJECT_MAPPER.readValue(value, new TypeReference<>() {
            });
            Map<String, ApplianceModel> applianceModelMap = getApplianceModelMap(models);
            for (Iterator<Entry<String, Z2MDeviceService>> iterator = service.deviceHandlers.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<String, Z2MDeviceService> entry = iterator.next();
                if (!applianceModelMap.containsKey(entry.getKey())) {
                    service.deviceRemoved(entry.getValue());
                    iterator.remove();
                    service.context.ui().removeItem(entry.getValue().getDeviceEntity());
                }
            }

            for (ApplianceModel newApplianceModel : applianceModelMap.values()) {
                if (newApplianceModel.getDefinition() == null) {
                    newApplianceModel.setDefinition(new Z2MDeviceDefinition());
                }
                if (newApplianceModel.getDefinition().getExposes() == null) {
                    newApplianceModel.getDefinition().setExposes(Collections.emptyList());
                }
                if (!service.deviceHandlers.containsKey(newApplianceModel.getIeeeAddress())) {
                    service.log.info("[{}]: New device added: {}", service.entityID, newApplianceModel);
                    service.deviceHandlers.put(newApplianceModel.getIeeeAddress(), new Z2MDeviceService(service, newApplianceModel));
                    service.context.ui().updateItem(service.entity);
                }
                Z2MDeviceService deviceService = service.deviceHandlers.get(newApplianceModel.getIeeeAddress());
                deviceService.deviceUpdated(newApplianceModel);
                service.context.ui().updateItem(deviceService.getDeviceEntity());
            }
        }),
        extensions(Level.INFO, (payload, service) -> {
        }),
        groups(Level.INFO, (payload, service) -> {
        }),
        event(Level.INFO, (payload, service) -> {
            JsonNode data = payload.get("data");
            String ieeeAddress = data.path("ieee_address").asText();
            switch (payload.get("type").asText()) {
                case "device_interview" -> {
                    String status = data.get("status").asText();
                    Level level = status.equals("failed") ? Level.ERROR : Level.INFO;
                    service.log.log(level, "[{}]: ZigBee2MQTT Device interview {} for device {}", service.entityID, status, ieeeAddress);
                }
                case "device_announce" -> service.log.info("[{}]: ZigBee2MQTT Device announce {}", service.entityID, ieeeAddress);
            }
        }),
        info(Level.DEBUG, (payload, service) -> {
        }),
        log(Level.DEBUG, (payload, service) -> {
        }),
        logging(Level.DEBUG, (payload, service) -> {
        }),
        state(Level.WARN, (payload, service) -> {
            String status = payload.get("raw").asText();
            service.entity.setStatus("online".equals(status) ? Status.ONLINE : "offline".equals(status) ? Status.OFFLINE : Status.ERROR);
            service.context.ui().toastr().info("ZigBee2MQTT coordinator status: " + payload);
            service.context.event().fireEvent("zigbee_coordinator-" + service.getEntityID(),
                new StringType(service.entity.getStatus().toString()));
        });

        private final Level logLevel;
        private final ThrowingBiConsumer<JsonNode, Z2MLocalCoordinatorService, Exception> handler;

        private static Map<String, ApplianceModel> getApplianceModelMap(List<ApplianceModel> models) {
            return models.stream()
                    .filter(d -> !d.getType().equals("Coordinator"))
                    .collect(Collectors.toMap(ApplianceModel::getIeeeAddress, d -> d));
        }
    }

    @RequiredArgsConstructor
    private enum Z2MBridgeResponseTopicHandlers {
        otaCheck("device/ota_update/check", Level.INFO, (payload, service) ->
            service.context.ui().toastr().success(
                        Lang.getServerMessage("ZIGBEE.OTA_CHECK_" + payload.get("updateAvailable").asText().toUpperCase(),
                                payload.get("id").asText())));

        private final String topic;
        private final Level logLevel;
        private final ThrowingBiConsumer<JsonNode, Z2MLocalCoordinatorService, Exception> handler;
    }
}
