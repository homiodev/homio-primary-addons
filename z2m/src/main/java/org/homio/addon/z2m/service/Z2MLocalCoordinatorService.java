package org.homio.addon.z2m.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pivovarit.function.ThrowingBiConsumer;
import com.pivovarit.function.ThrowingConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.homio.addon.z2m.ZigBee2MQTTFrontendConsolePlugin;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.service.endpoints.inline.Z2MDeviceEndpointInline;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition;
import org.homio.addon.z2m.util.Z2MConfiguration;
import org.homio.addon.z2m.util.ZigBeeUtil;
import org.homio.api.EntityContext;
import org.homio.api.EntityContextBGP.ProcessContext;
import org.homio.api.EntityContextBGP.ThreadContext;
import org.homio.api.EntityContextService.MQTTEntityService;
import org.homio.api.EntityContextUI;
import org.homio.api.console.ConsolePluginFrame.FrameConfiguration;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.model.HasEntityIdentifier;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService.ServiceInstance;
import org.homio.api.service.EntityService.WatchdogService;
import org.homio.api.ui.UI.Color;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.homio.addon.z2m.service.Z2MDeviceService.CONFIG_DEVICE_SERVICE;
import static org.homio.addon.z2m.util.ZigBeeUtil.zigbee2mqttGitHub;
import static org.homio.api.util.CommonUtils.*;

/**
 * The {@link Z2MLocalCoordinatorService} is responsible for handling commands, which are sent to one of the zigbeeRequireEndpoints.
 * <p>
 * This is the base coordinator handler. It handles the majority of the interaction with the ZigBeeNetworkManager.
 * <p>
 * The interface coordinators are responsible for opening a ZigBeeTransport implementation and passing this to the {@link Z2MLocalCoordinatorService}.
 */
@Log4j2
public class Z2MLocalCoordinatorService extends ServiceInstance<Z2MLocalCoordinatorEntity>
    implements HasEntityIdentifier {

    private final Path zigbee2mqttConfigurationPath = zigbee2mqttGitHub
        .getLocalProjectPath().resolve("data/configuration.yaml");

    private final Object updateCoordinatorSync = new Object();
    private final AtomicBoolean scanStarted = new AtomicBoolean(false);
    // Map ieeeAddress - Z2MDeviceService
    @Getter private final Map<String, Z2MDeviceService> deviceHandlers = new ConcurrentHashMap<>();

    @Getter private boolean initialized;
    private Status desiredStatus;
    @Getter private @Nullable Status updatingStatus;
    @Getter
    private MQTTEntityService mqttEntityService;
    @Getter
    public static final Map<String, Class<? extends Z2MDeviceEndpoint>> allEndpoints = new HashMap<>();
    private URL z2mFrontendURL;

    private ThreadContext<Void> checkFrontendTC;
    private ThreadContext<Void> configurationWatchDogTC;
    private ThreadContext<Void> internalTestStatusWatchdog;

    private ProcessContext nodePC;
    @Getter
    private Z2MConfiguration configuration;
    @Getter
    private boolean isZ2MRunningLocally;

    public Z2MLocalCoordinatorService(@NotNull EntityContext entityContext) {
        super(entityContext);
    }

    @SneakyThrows
    public ActionResponseModel reinstallZ2M() {
        this.dispose(null);
        zigbee2mqttGitHub.deleteProject();
        installZ2MOrRestartCoordinator();
        return ActionResponseModel.fired();
    }

    private void installZ2MOrRestartCoordinator() {
        if (!ZigBeeUtil.isZ2MInstalled()) {
            entityContext.event().runOnceOnInternetUp("z2m", () ->
                    ZigBeeUtil.installZ2M(entityContext, this::restartCoordinator));
        } else {
            restartCoordinator();
            entityContext.event().runOnceOnInternetUp("z2m", this::updateNotificationBlock);
        }
    }

    @Override
    public WatchdogService getWatchdog() {
        return new WatchdogService() {
            @Override
            public void restartService() {
                restartZ2M();
            }

            @Override
            public boolean isRequireRestartService() {
                return !zigbee2mqttGitHub.isUpdating()
                    && entity.isEnableWatchdog()
                    && Z2MLocalCoordinatorService.this.isRequireRestartService();
            }
        };
    }

    @SneakyThrows
    public void initializeZ2M() {
        initialized = false;
        log.info("[{}]: Initializing ZigBee network.", entityID);

        mqttEntityService = entity.getMqttEntityService();
        runZigBee2MQTT();
    }

    @Override
    protected long getEntityHashCode(Z2MLocalCoordinatorEntity entity) {
        return entity.getDeepHashCode();
    }

    public void dispose(@Nullable Exception ex) {
        initialized = false;
        if (ex != null) {
            this.entity.setStatusError(ex);
        } else {
            this.entity.setStatus(Status.OFFLINE, null);
        }
        if (internalTestStatusWatchdog != null) {
            internalTestStatusWatchdog.cancel();
            internalTestStatusWatchdog = null;
        }
        if (configurationWatchDogTC != null) {
            configurationWatchDogTC.cancel();
            configurationWatchDogTC = null;
        }
        if (checkFrontendTC != null) {
            checkFrontendTC.cancel();
            checkFrontendTC = null;
        }
        if (nodePC != null) {
            nodePC.cancel(true);
            nodePC = null;
        }

        log.warn("[{}]: Dispose coordinator", entityID, ex);

        entityContext.ui().unRegisterConsolePlugin("zigbee2mqtt-console-" + entityID);
        entityContext.ui().sendWarningMessage("Dispose zigBee coordinator");
        entityContext.ui().removeHeaderButton("discover-" + entityID);
        for (Z2MDeviceService deviceHandler : deviceHandlers.values()) {
            deviceHandler.dispose();
        }

        updateNotificationBlock();
    }

    public void restartCoordinator() {
        this.desiredStatus = calcEntityDesiredStatus(entity);
        if (this.desiredStatus == null) {
            if (!this.entity.isStart() && this.entity.getStatus() == Status.ERROR) {
                this.entity.setStatus(Status.OFFLINE, null);
                updateNotificationBlock();
            }
        } else {
            scheduleUpdateStatusIfRequire();
            updateNotificationBlock();
        }
    }

    @Override
    protected void firstInitialize() {
        assembleAllEndpoints(entityContext);
        initialize();
    }

    @Override
    protected void initialize() {
        installZ2MOrRestartCoordinator();
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
        if (isRequireRestartService() || !isZigbee2MqttRunning()) {
            log.info("Force restart z2m");
            this.dispose(null);
            this.restartCoordinator();

        }
        return ActionResponseModel.fired();
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
                ZigBeeUtil.zigbeeScanStarted(entityContext, entityID, duration,
                    () -> scanStarted.set(false),
                    () -> {
                        sendRequest("permit_join", new JSONObject().put("value", false).toString());
                        scanStarted.set(false);
                        entityContext.ui().removeHeaderButton("zigbee-scan-" + entityID);
                    });
            } catch (Exception ex) {
                log.error("[{}]: Unable to send request to discover devices. {}", entityID, getErrorMessage(ex));
                return ActionResponseModel.showError(ex);
            }
            return ActionResponseModel.showSuccess("ZIGBEE.START_SUCCESS");
        }
    }

    @Override
    public void destroy() {
        this.dispose(null);
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

    private boolean isRequireRestartService() {
        return desiredStatus == null && updatingStatus == null && entity.getStatus() != Status.ONLINE && entity.isStart();
    }

    private @Nullable Status calcEntityDesiredStatus(@NotNull Z2MLocalCoordinatorEntity entity) {
        return entity.isStart() ? restartIfRequire(entity) : (initialized ? Status.CLOSING : null);
    }

    private void scheduleUpdateStatusIfRequire() {
        if (isRequireStartOrStop()) {
            synchronized (updateCoordinatorSync) {
                if (isRequireStartOrStop()) {
                    updatingStatus = desiredStatus;
                    desiredStatus = null;
                    entityContext.bgp().builder("z2m-updating-" + entityID).execute(this::executeUpdateStateIfRequire);
                } else {
                    this.desiredStatus = null;
                }
            }
        } else {
            this.desiredStatus = null;
        }
    }

    private void executeUpdateStateIfRequire() {
        try {
            switch (Objects.requireNonNull(updatingStatus)) {
                case CLOSING -> {
                    if (initialized) {
                        this.dispose(null);
                    }
                }
                case INITIALIZE, RESTARTING -> {
                    if (initialized) {
                        this.dispose(null);
                    }
                    this.initializeZ2M();
                }
            }
            entityContext.ui().updateItem(entity);
            // fire recursively if state updated since last time
            scheduleUpdateStatusIfRequire();
        } finally {
            this.updatingStatus = null;
        }
    }

    private boolean isRequireStartOrStop() {
        if (updatingStatus != null) {
            return false;
        }
        if (desiredStatus == Status.INITIALIZE && !initialized) {
            return true;
        }
        return desiredStatus == Status.CLOSING && initialized || desiredStatus == Status.RESTARTING;
    }

    private Status restartIfRequire(Z2MLocalCoordinatorEntity entity) {
        String error = validateEntity(entity);
        if (error != null) {
            entity.setStatusError(error);
            return Status.CLOSING;
        }

        // do check reinitialize only if coordinator already started
        if (initialized) {
            boolean reinitialize;
            if (this.mqttEntityService == null) {
                reinitialize = true;
            } else {
                reinitialize = !entity.deepEqual(this.entity);
            }

            if (reinitialize) {
                return Status.RESTARTING;
            }
        }

        return Status.INITIALIZE;
    }

    private String validateEntity(Z2MLocalCoordinatorEntity newEntity) {
        if (isEmpty(newEntity.getPort())) {
            return "ZIGBEE.ERROR.NO_PORT";
        } else if (CommonUtils.getSerialPort(entity.getPort()) == null) {
            return Lang.getServerMessage("ZIGBEE.ERROR.PORT_NOT_FOUND", entity.getPort());
        }
        if (isEmpty(entity.getRawMqttEntity())) {
            return "ZIGBEE.ERROR.NO_MQTT";
        }
        if (entity.getMqttEntityService() == null) {
            return "ZIGBEE.ERROR.MQTT_NOT_FOUND";
        }
        return null;
    }

    @SneakyThrows
    private void runZigBee2MQTT() {
        try {
            // create configuration file if not exists
            if (zigbee2mqttConfigurationPath.toFile().createNewFile()) {
                log.info("[{}]: Created zigbee2mqtt configuration file: {}", entityID, zigbee2mqttConfigurationPath);
            }

            syncConfiguration();

            isZ2MRunningLocally = !isZigbee2MqttRunning();
            if (isZ2MRunningLocally) {
                startZ2MLocalProcess();
            } else {
                runInternalWatchdog();
                setEntityOnline();
            }
        } catch (Exception ex) {
            log.error("[{}]: Error while start zigbee2mqtt {}", entityID, getErrorMessage(ex));
            dispose(ex);
        }
        updateNotificationBlock();
    }

    private void runInternalWatchdog() {
        this.internalTestStatusWatchdog = entityContext
            .bgp()
            .builder("z2m-internal-watchdog")
            .delay(Duration.ofSeconds(10))
            .interval(Duration.ofSeconds(10))
            .execute(() -> {
                if (entity.getStatus().isOnline() && !isZigbee2MqttRunning()) {
                    entity.setStatusError("Z2M not responsible");
                }
            });
    }

    public void updateNotificationBlock() {
        entityContext.ui().addNotificationBlock(entityID, "ZigBee2MQTT", new Icon("fas fa-bezier-curve", "#899343"), builder -> {
            builder.setStatus(entity.getStatus());
            builder.linkToEntity(entity);
            builder.setUpdating(zigbee2mqttGitHub.isUpdating());
            builder.setVersion(zigbee2mqttGitHub.getInstalledVersion());

            builder.setUpdatable((progressBar, version) -> {
                    if (!version.equals(zigbee2mqttGitHub.getInstalledVersion())) {
                        dispose(null);
                        return zigbee2mqttGitHub.updateProject("zigbee2mqtt", progressBar, true, projectUpdate -> {
                            if (!version.equals(zigbee2mqttGitHub.getInstalledVersion())) {
                                ZigBeeUtil.installOrUpdateZ2M(entityContext, version, projectUpdate);
                                if (entity.isStart()) {
                                    restartCoordinator();
                                }
                            }
                            return ActionResponseModel.fired();
                        }, null);
                    }
                    return ActionResponseModel.showError("Z2M same version");
                },
                zigbee2mqttGitHub.getReleasesSince(zigbee2mqttGitHub.getInstalledVersion(), false));
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
                            entityContext.save(entity.setStart(true));
                            return ActionResponseModel.success();
                        }
                        return ActionResponseModel.showWarn("Z2M.COORDINATOR_ALREADY_STARTED");
                    });
                }
                contextAction.addSelectableButton("ZIGBEE_START_SCAN", new Icon("fas fa-search-location", "#899343"),
                    (ec, params) -> entity.scan());
                contextAction.addSelectableButton("RESTART", new Icon("fas fa-power-off", Color.RED),
                    (ec, params) -> restartZ2M());
            });
        });
    }

    private void setEntityOnline() {
        initialized = true;
        entity.setStatusOnline();
        for (Z2MDeviceService deviceService : deviceHandlers.values()) {
            deviceService.setEntityOnline();
        }

        entityContext.ui().headerButtonBuilder("discover-" + entityID)
                     .title("CONTEXT.ACTION.ZIGBEE_START_SCAN")
                     .icon(new Icon("fas fa-search-location", "#3E7BBD"))
                     .availableForPage(ZigBeeDeviceBaseEntity.class)
                     .clickAction(this::startScan).build();

        // register frame console
        checkFrontendTC = entityContext.bgp().builder("z2m-check-frontend-" + getEntityID())
                                       .delay(Duration.ofSeconds(10)).execute(() ->
                entityContext.ui().registerConsolePlugin("zigbee2mqtt-frontend-" + entityID,
                    new ZigBee2MQTTFrontendConsolePlugin(entityContext, new FrameConfiguration(z2mFrontendURL.toString()))));

        // register mqtt bridge event listeners
        for (Z2MBridgeTopicHandlers handler : Z2MBridgeTopicHandlers.values()) {
            addMqttTopicListener(getBridgeTopic(handler), payload -> handler.handler.accept(payload, this), handler.logLevel);
        }
        for (Z2MBridgeResponseTopicHandlers response : Z2MBridgeResponseTopicHandlers.values()) {
            addMqttTopicListener(
                "%s/bridge/response/%s".formatted(entity.getBasicTopic(), response.topic),
                payload -> {
                    if ("ok".equalsIgnoreCase(payload.get("status").asText())) {
                        response.handler.accept(payload.get("data"), this);
                    } else {
                        log.error("[{}]: ZigBee2MQTT {} response status failed. {}", entityID, response.topic, payload);
                    }
                }, response.logLevel);
        }
    }

    private void addMqttTopicListener(String topic, ThrowingConsumer<ObjectNode, Exception> handler, Level logLevel) {
        mqttEntityService.addListener(topic, "z2m", value -> {
            log.log(logLevel, "[{}]: ZigBee2MQTT {}: {}", entityID, topic, value);
            String payload = value == null ? "" : value.toString();
            if (!payload.isEmpty()) {
                ObjectNode node;
                try {
                    node = OBJECT_MAPPER.readValue(payload, ObjectNode.class);
                } catch (Exception ex) {
                    node = OBJECT_MAPPER.createObjectNode().put("raw", payload);
                }
                try {
                    handler.accept(node);
                } catch (Exception ex) {
                    log.error("[{}]: Unable to handle mqtt payload: {}. Msg: {}", entityID, payload, getErrorMessage(ex));
                }
            }
        });
    }

    private boolean isZigbee2MqttRunning() {
        try {
            URL url = new URL("http://localhost:%d".formatted(configuration.getFrontend().getPort()));
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            if (HttpURLConnection.HTTP_OK == huc.getResponseCode()) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
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
        this.z2mFrontendURL = new URL("http://localhost:%d".formatted(configuration.getFrontend().getPort()));

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
        entityContext.ui().updateItem(entity);
        deviceService.dispose();
        deviceHandlers.remove(deviceService.getIeeeAddress());
    }

    private @NotNull String getBridgeTopic(@NotNull Z2MLocalCoordinatorService.Z2MBridgeTopicHandlers z2MBridgeTopicHandlers) {
        return "%s/bridge/%s".formatted(entity.getBasicTopic(), z2MBridgeTopicHandlers.name());
    }

    private void startZ2MLocalProcess() {
        AtomicReference<Level> infoLevel = new AtomicReference<>(Level.INFO);
        nodePC = entityContext
            .bgp().processBuilder(getEntityID())
            .onStarted(t -> {
                runInternalWatchdog();
                setEntityOnline();
            })
            .onFinished((ex, responseCode) -> {
                if (ex != null) {
                    log.error("[{}]: Error while start zigbee2mqtt {}", entityID, getErrorMessage(ex));
                } else {
                    log.warn("[{}]: zigbee2mqtt nodeJs finished with status: {}", entityID, responseCode);
                }
                nodePC = null;
                dispose(ex);
            })
                .setErrorLoggerOutput(log::error)
                .setInputLoggerOutput(msg -> {
                    if (msg.contains("!!!!")) {
                        infoLevel.set(Level.ERROR);
                    }
                    log.log(infoLevel.get(), "[{}]: ZigBee2MQTT: {}", entityID, msg);
                })
                .execute(getNpm() + " start --prefix " + zigbee2mqttGitHub.getLocalProjectPath());
    }

    private void assembleAllEndpoints(EntityContext entityContext) {
        if (allEndpoints.isEmpty()) {
            List<Class<? extends Z2MDeviceEndpoint>> z2mClusters = entityContext.getClassesWithParent(Z2MDeviceEndpoint.class);
            for (Class<? extends Z2MDeviceEndpoint> z2mCluster : z2mClusters) {
                if (!Z2MDeviceEndpointInline.class.isAssignableFrom(z2mCluster)) {
                    Z2MDeviceEndpoint endpoint = CommonUtils.newInstance(z2mCluster);
                    allEndpoints.put(endpoint.getEndpointDefinition(), z2mCluster);
                }
            }
        }
    }

    private void addUpdateButton(EntityContextUI.NotificationBlockBuilder builder) {
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
                    service.entityContext.ui().removeItem(entry.getValue().getDeviceEntity());
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
                    Z2MLocalCoordinatorService.log.info("[{}]: New device added: {}", service.entityID, newApplianceModel);
                    service.deviceHandlers.put(newApplianceModel.getIeeeAddress(), new Z2MDeviceService(service, newApplianceModel));
                    service.entityContext.ui().updateItem(service.entity);
                }
                Z2MDeviceService deviceService = service.deviceHandlers.get(newApplianceModel.getIeeeAddress());
                deviceService.deviceUpdated(newApplianceModel);
                service.entityContext.ui().updateItem(deviceService.getDeviceEntity());
            }
        }),
        extensions(Level.INFO, (payload, service) -> {}),
        groups(Level.INFO, (payload, service) -> {}),
        event(Level.INFO, (payload, service) -> {
            JsonNode data = payload.get("data");
            String ieeeAddress = data.path("ieee_address").asText();
            switch (payload.get("type").asText()) {
                case "device_interview" -> {
                    String status = data.get("status").asText();
                    Level level = status.equals("failed") ? Level.ERROR : Level.INFO;
                    Z2MLocalCoordinatorService.log.log(level, "[{}]: ZigBee2MQTT Device interview {} for device {}", service.entityID, status, ieeeAddress);
                }
                case "device_announce" -> Z2MLocalCoordinatorService.log.info("[{}]: ZigBee2MQTT Device announce {}", service.entityID, ieeeAddress);
            }
        }),
        info(Level.DEBUG, (payload, service) -> {}),
        log(Level.DEBUG, (payload, service) -> {}),
        logging(Level.DEBUG, (payload, service) -> {}),
        state(Level.WARN, (payload, service) -> {
            String status = payload.get("raw").asText();
            service.entity.setStatus("online".equals(status) ? Status.ONLINE : "offline".equals(status) ? Status.OFFLINE : Status.ERROR);
            service.entityContext.ui().sendInfoMessage("ZigBee2MQTT coordinator status: " + payload);
            service.entityContext.event().fireEvent("zigbee_coordinator-" + service.getEntityID(), service.entity.getStatus());
        });

        private final Level logLevel;

        private static Map<String, ApplianceModel> getApplianceModelMap(List<ApplianceModel> models) {
            return models.stream()
                    .filter(d -> !d.getType().equals("Coordinator"))
                    .collect(Collectors.toMap(ApplianceModel::getIeeeAddress, d -> d));
        }

        private final ThrowingBiConsumer<JsonNode, Z2MLocalCoordinatorService, Exception> handler;
    }

    @RequiredArgsConstructor
    private enum Z2MBridgeResponseTopicHandlers {
        otaCheck("device/ota_update/check", Level.INFO, (payload, service) ->
                service.entityContext.ui().sendSuccessMessage(
                        Lang.getServerMessage("ZIGBEE.OTA_CHECK_" + payload.get("updateAvailable").asText().toUpperCase(),
                                payload.get("id").asText())));

        private final String topic;
        private final Level logLevel;
        private final ThrowingBiConsumer<JsonNode, Z2MLocalCoordinatorService, Exception> handler;
    }
}
