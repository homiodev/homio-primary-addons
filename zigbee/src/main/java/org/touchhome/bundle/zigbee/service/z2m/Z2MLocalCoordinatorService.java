package org.touchhome.bundle.zigbee.service.z2m;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;
import static org.touchhome.common.util.CommonUtils.YAML_OBJECT_MAPPER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pivovarit.function.ThrowingBiConsumer;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginFrame.FrameConfiguration;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.hquery.LinesReader;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.mqtt.entity.MQTTBaseEntity;
import org.touchhome.bundle.zigbee.NodeJSDependencyExecutableInstaller;
import org.touchhome.bundle.zigbee.ZigBee2MQTTFrontendConsolePlugin;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceBaseEntity;
import org.touchhome.bundle.zigbee.model.z2m.Z2MLocalCoordinatorEntity;
import org.touchhome.bundle.zigbee.service.z2m.properties.Z2MPropertyUnknown;
import org.touchhome.bundle.zigbee.util.Z2MConfiguration;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO;
import org.touchhome.bundle.zigbee.util.ZigBeeUtil;
import org.touchhome.common.model.ProgressBar;
import org.touchhome.common.util.ArchiveUtil;
import org.touchhome.common.util.ArchiveUtil.UnzipFileIssueHandler;
import org.touchhome.common.util.CommonUtils;
import org.touchhome.common.util.Curl;
import org.touchhome.common.util.Lang;

/**
 * The {@link Z2MLocalCoordinatorService} is responsible for handling commands, which are sent to one of the zigbeeRequireEndpoints.
 * <p>
 * This is the base coordinator handler. It handles the majority of the interaction with the ZigBeeNetworkManager.
 * <p>
 * The interface coordinators are responsible for opening a ZigBeeTransport implementation and passing this to the {@link Z2MLocalCoordinatorService}.
 */
@Log4j2
public class Z2MLocalCoordinatorService implements HasEntityIdentifier, ServiceInstance<Z2MLocalCoordinatorEntity> {

  @Getter
  protected final EntityContext entityContext;
  private final Path zigbee2mqttPath = TouchHomeUtils.getInstallPath().resolve("zigbee2mqtt");
  private final Path zigbee2mqttConfigurationPath = zigbee2mqttPath.resolve("data/configuration.yaml");

  @Getter
  private final String entityID;
  private final Object updateCoordinatorSync = new Object();
  private final AtomicBoolean scanStarted = new AtomicBoolean(false);
  @Getter
  private final Map<String, Z2MDeviceService> deviceHandlers = new ConcurrentHashMap<>();
  @Getter
  private final Map<String, Class<? extends Z2MProperty>> z2mConverters = new HashMap<>();
  @Getter
  @NotNull
  private Z2MLocalCoordinatorEntity entity;
  @Getter
  private boolean initialized;
  private Status desiredStatus;
  @Getter
  private @Nullable Status updatingStatus;
  @Getter
  private MQTTBaseEntity mqttEntity;
  private Process nodeProcess = null;
  private Thread inputThread;
  private Thread errorThread;
  @Getter
  private Z2MConfiguration configuration;

  public Z2MLocalCoordinatorService(EntityContext entityContext, Z2MLocalCoordinatorEntity entity) {
    this.entity = entity;
    this.entityID = entity.getEntityID();
    this.entityContext = entityContext;
    this.entityContext.bgp().executeOnExit(() -> {
      if (nodeProcess != null) {
        nodeProcess.destroyForcibly();
      }
    });
    // find all available Z2MProperty
    List<Class<? extends Z2MProperty>> z2mClusters = entityContext.getClassesWithParent(Z2MProperty.class);
    for (Class<? extends Z2MProperty> z2mCluster : z2mClusters) {
      if (!z2mCluster.equals(Z2MPropertyUnknown.class)) {
        Z2MProperty z2MProperty = CommonUtils.newInstance(z2mCluster);
        z2mConverters.put(z2MProperty.getProperty(), z2mCluster);
      }
    }

    /* this.entityContext.ui().registerConsolePlugin("zigbee2mqtt-console-" + entityID,
        new ZigBeeConsolePlugin(entityContext, this)); */
    this.desiredStatus = calcEntityDesiredStatus(entity);

    scheduleUpdateStatusIfRequire();

    entityContext.bgp().executeOnExit(this::disposeNodeProcess);
    // entityContext.ui().addUISidebarMenuEntities();
  }

  @SneakyThrows
  public void initialize(ProgressBar progressBar) {
    initialized = false;
    log.info("[{}]: Initializing ZigBee network.", entityID);

    entityContext.bgp().runFileWatchdog(zigbee2mqttConfigurationPath, getEntityID(), () -> {
      configuration = readConfiguration();
      entityContext.ui().updateItem(entity);
      for (Z2MDeviceService deviceService : deviceHandlers.values()) {
        entityContext.ui().updateItem(deviceService.getDeviceEntity());
      }
    });

    this.mqttEntity = entityContext.getEntity(entity.getMqttEntity());
    for (Z2MResponse z2MResponse : Z2MResponse.values()) {
      entityContext.event().addEventBehaviourListener(getBridgeTopic(z2MResponse), payload -> {
        try {
          z2MResponse.handler.accept(payload.toString(), this);
        } catch (Exception e) {
          log.error("Unable to handle {} mqtt payload: {}", z2MResponse, payload);
        }
      });
    }
    /* entityContext.event().addEventBehaviourListener(entity.getMqttEntity() + "-" + entity.getBasicTopic(), payload -> {
      try {
        ObjectNode node = OBJECT_MAPPER.readValue(payload.toString(), ObjectNode.class);
        System.out.println(node);
      } catch (JsonProcessingException e) {
        log.error("[{}]: Unable to parse income message {} to json", entityID, payload);
      }
    });*/

    Path zigbee2mqttPackagePath = zigbee2mqttPath.resolve("node_modules");
    Path targetZipPath = TouchHomeUtils.getInstallPath().resolve("zigbee2mqtt.tar.gz");
    if (!Files.exists(zigbee2mqttPackagePath)) {
      String version = "1.28.4";
      MachineHardwareRepository machineHardwareRepository = entityContext.getBean(MachineHardwareRepository.class);
      Curl.downloadWithProgress("https://github.com/Koenkk/zigbee2mqtt/archive/" + version + ".tar.gz",
          targetZipPath, progressBar);
      ArchiveUtil.unzip(targetZipPath, TouchHomeUtils.getInstallPath(), null,
          false, progressBar, UnzipFileIssueHandler.replace);
      Files.delete(targetZipPath);
      Files.move(TouchHomeUtils.getInstallPath().resolve("zigbee2mqtt-" + version), zigbee2mqttPath, StandardCopyOption.REPLACE_EXISTING);

      NodeJSDependencyExecutableInstaller installer = entityContext.getBean(NodeJSDependencyExecutableInstaller.class);
      if (installer.isRequireInstallDependencies(entityContext, true)) {
        progressBar.progress(0, "install-nodejs");
        installer.installDependency(entityContext, progressBar);
      }
      progressBar.progress(0, "install-zigbee2mqtt");
      machineHardwareRepository.execute("npm ci --prefix " + zigbee2mqttPath + " --no-audit --no-optional --no-update-notifier --unsafe-perm", 600, progressBar);
      machineHardwareRepository.execute("npm run build --prefix " + zigbee2mqttPath, 600, progressBar);
      progressBar.progress(50, "remove node_modules");
      if (SystemUtils.IS_OS_LINUX) {
        machineHardwareRepository.execute("rm -rf " + zigbee2mqttPath.resolve("node_modules"));
      } else {
        FileUtils.deleteDirectory(zigbee2mqttPath.resolve("node_modules").toFile());
      }
      machineHardwareRepository.execute("npm ci --prefix " + zigbee2mqttPath + " --no-audit --no-optional --no-update-notifier --only=production --unsafe-perm", 600, progressBar);

      runZigBee2MQTT();
    } else {
      runZigBee2MQTT();
    }
  }

  @NotNull
  private String getBridgeTopic(Z2MResponse z2MResponse) {
    return entity.getMqttEntity() + "-"
        + entity.getBasicTopic() + "/bridge/" + z2MResponse.name();
  }

  public void dispose(@Nullable Exception ex) {
    log.warn("[{}]: Dispose coordinator", entityID);
    if (ex != null) {
      this.entity.setStatusError(ex);
    } else {
      this.entity.setStatus(Status.OFFLINE, null);
    }
    entityContext.ui().unRegisterConsolePlugin("zigbee2mqtt-console-" + entityID);
    entityContext.ui().sendWarningMessage("Dispose zigBee coordinator");
    entityContext.ui().removeHeaderButton("discover-" + entityID);
    for (Z2MDeviceService deviceHandler : deviceHandlers.values()) {
      entityContext.event().removeEvents(getDeviceTopic(deviceHandler.getDevice()));
      deviceHandler.dispose();
    }

    for (Z2MResponse z2MResponse : Z2MResponse.values()) {
      entityContext.event().removeEvents(getBridgeTopic(z2MResponse));
    }

    disposeNodeProcess();
    this.initialized = false;
  }

  private void disposeNodeProcess() {
    if (nodeProcess != null && nodeProcess.isAlive()) {
      nodeProcess.destroyForcibly();
    }
    if (inputThread != null && inputThread.isAlive()) {
      inputThread.interrupt();
      inputThread = null;
    }
    if (errorThread != null && errorThread.isAlive()) {
      errorThread.interrupt();
      errorThread = null;
    }
  }

  public void restartCoordinator() {
    this.desiredStatus = calcEntityDesiredStatus(entity);
    if (this.desiredStatus == null) {
      if (!this.entity.isStart() && this.entity.getStatus() == Status.ERROR) {
        this.entity.setStatus(Status.OFFLINE, null);
      }
    } else {
      scheduleUpdateStatusIfRequire();
    }
  }

  @Override
  @SneakyThrows
  public boolean entityUpdated(@NotNull Z2MLocalCoordinatorEntity newEntity) {
    if (!this.entity.deepEqual(newEntity)) {
      this.desiredStatus = calcEntityDesiredStatus(newEntity);
      this.entity = newEntity;
      this.restartCoordinator();
    }
    return false;
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
          entityContext.bgp().runWithProgress("zigbee-coordinator-" + entityID, false, progressBar -> {
            try {
              if (updatingStatus == Status.CLOSING && initialized) {
                this.dispose(null);
              } else if (updatingStatus == Status.INITIALIZE || updatingStatus == Status.RESTARTING) {
                if (initialized) {
                  this.dispose(null);
                }
                this.initialize(progressBar);
              }
              entityContext.ui().updateItem(entity);
              // fire recursively if state updated since last time
              scheduleUpdateStatusIfRequire();
            } finally {
              this.updatingStatus = null;
            }
          });
        } else {
          this.desiredStatus = null;
        }
      }
    } else {
      this.desiredStatus = null;
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
      if (this.mqttEntity == null) {
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
      return "zigbee.error.no_port";
    } else if (TouchHomeUtils.getSerialPort(entity.getPort()) == null) {
      return Lang.getServerMessage("zigbee.error.port_not_found", "NAME", entity.getPort());
    }
    if (isEmpty(entity.getMqttEntity())) {
      return "zigbee.error.no_mqtt";
    }
    MQTTBaseEntity mqttEntity = entityContext.getEntity(entity.getMqttEntity());
    if (mqttEntity == null) {
      return "zigbee.error.mqtt_not_found";
    }
    return null;
  }

  @SneakyThrows
  private void runZigBee2MQTT() {
    try {
      if (!Files.exists(zigbee2mqttConfigurationPath)) {
        throw new IllegalStateException("Unable to find zigbee2mqtt configuration file: {}" + zigbee2mqttConfigurationPath);
      }
      this.configuration = rewriteConfigurationFile();

      entityContext.bgp().builder("zigbee2mqtt-service").hideOnUIAfterCancel(false).execute(() -> {
        nodeProcess = Runtime.getRuntime().exec(getNpm() + " start --prefix " + zigbee2mqttPath);
        initialized = nodeProcess.isAlive();
        if (initialized) {
          entity.setStatusOnline();
          entityContext.ui().headerButtonBuilder("discover-" + entityID)
                       .title("zigbee.action.start_scan")
                       .icon("fas fa-search-location", "#3E7BBD", false)
                       .availableForPage(ZigBeeDeviceBaseEntity.class)
                       .clickAction(this::startScan).build();
        }

        this.inputThread =
            new Thread(new LinesReader("zigbee2mqtt-inputReader", nodeProcess.getInputStream(), null, message ->
                log.debug("[{}]: zigbee2mqtt nodejs: {}", entityID, message)));
        this.errorThread =
            new Thread(new LinesReader("zigbee2mqtt-errorReader", nodeProcess.getErrorStream(), null, message ->
                log.error("[{}]: zigbee2mqtt nodejs: {}", entityID, message)));
        inputThread.start();
        errorThread.start();

        // register frame console
        entityContext.bgp().builder("zigbee2mqtt-check-frontend").delay(Duration.ofSeconds(10)).execute(() -> {
          URL url = new URL(format("http://localhost:%s", configuration.getFrontend().getPort()));
          HttpURLConnection huc = (HttpURLConnection) url.openConnection();
          huc.setRequestMethod("HEAD");
          if (HttpURLConnection.HTTP_OK == huc.getResponseCode()) {
            entityContext.ui().registerConsolePlugin("zigbee2mqtt-frontend-" + entityID,
                new ZigBee2MQTTFrontendConsolePlugin(entityContext, new FrameConfiguration(url.toString())));
          }
        });

        int responseCode = nodeProcess.waitFor();
        log.warn("[{}]: zigbee2mqtt nodeJs finished with status: {}", entityID, responseCode);
        entity.setStatus(Status.OFFLINE, null);
        initialized = false;
        disposeNodeProcess();
      }).onError(ex -> {
        log.error("[{}]: Error while start zigbee2mqtt {}", entityID, CommonUtils.getErrorMessage(ex));
        this.dispose(ex);
      });
    } catch (Exception ex) {
      log.error("[{}]: Error while start zigbee2mqtt {}", entityID, CommonUtils.getErrorMessage(ex));
      this.dispose(ex);
    }
  }

  public ActionResponseModel restart() {
    sendRequest("restart", "");
    return null;
  }

  public ActionResponseModel healthСheck() {
    sendRequest("health_check", "");
    return null;
  }

  public ActionResponseModel startScan() {
    synchronized (scanStarted) {
      if (scanStarted.get()) {
        throw new IllegalStateException("zigbee.error.scan_already_started");
      }
      if (!entity.getStatus().isOnline()) {
        throw new IllegalStateException("zigbee.error.coordinator_offline");
      }
      log.info("[{}]: Start scanning...", entityID);
      scanStarted.set(true);
      int duration = entity.getDiscoveryDuration();

      try {
        sendRequest("permit_join", new JSONObject().put("value", true).put("time", duration).toString());
        ZigBeeUtil.zigbeeScanStarted(entityContext, entityID, duration, () -> scanStarted.set(false), () -> {
          sendRequest("permit_join", new JSONObject().put("value", false).toString());
          scanStarted.set(false);
        });
      } catch (Exception ex) {
        log.error("[{}]: Unable to send request to discover devices. {}", entityID, CommonUtils.getErrorMessage(ex));
        return ActionResponseModel.showError(ex);
      }
      return ActionResponseModel.success();
    }
  }

  @SneakyThrows
  private void sendRequest(String path, String payload) {
    String topic = format("%s/bridge/request/%s", entity.getBasicTopic(), path);
    mqttEntity.getService().getMqttClient().publish(topic, payload.getBytes(), 0, false);
  }

  private String getNpm() {
    return SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
  }

  public Z2MConfiguration readConfiguration() {
    Z2MConfiguration configuration;
    try {
      configuration = YAML_OBJECT_MAPPER.readValue(zigbee2mqttConfigurationPath.toFile(), Z2MConfiguration.class);
    } catch (Exception ex) {
      log.error("[{}]: Unable to read zigbee2mqtt configuration file: {}", entityID, zigbee2mqttConfigurationPath.toAbsolutePath(), ex);
      configuration = new Z2MConfiguration();
    }
    return configuration;
  }

  @SneakyThrows
  private Z2MConfiguration rewriteConfigurationFile() {
    Z2MConfiguration configuration = readConfiguration();
    boolean updated = false;
    if (configuration.isPermitJoin() != entity.isPermitJoin()) {
      configuration.setPermitJoin(entity.isPermitJoin());
      updated = true;
    }
    if (!Objects.equals(configuration.getMqtt().getBaseTopic(), trimToNull(entity.getBasicTopic()))) {
      configuration.getMqtt().setBaseTopic(trimToNull(entity.getBasicTopic()));
      updated = true;
    }
    String server = format("mqtt://%s:%s", mqttEntity.getHostname(), mqttEntity.getMqttPort());
    if (!Objects.equals(configuration.getMqtt().getServer(), server)) {
      configuration.getMqtt().setServer(server);
      updated = true;
    }
    if (!Objects.equals(configuration.getMqtt().getUser(), trimToNull(mqttEntity.getMqttUser()))) {
      configuration.getMqtt().setUser(trimToNull(mqttEntity.getMqttUser()));
      updated = true;
    }
    if (!Objects.equals(configuration.getMqtt().getPassword(), trimToNull(mqttEntity.getMqttPassword().asString()))) {
      configuration.getMqtt().setPassword(trimToNull(mqttEntity.getMqttPassword().asString()));
      updated = true;
    }
    if (!Objects.equals(configuration.getSerial().getPort(), trimToNull(entity.getPort()))) {
      configuration.getSerial().setPort(entity.getPort());
      updated = true;
    }
    if (updated) {
      YAML_OBJECT_MAPPER.writeValue(new PrintWriter(zigbee2mqttConfigurationPath.toFile()), configuration);
    }
    return configuration;
  }

  @Override
  public boolean testService() {
    return false;
  }

  @Override
  public void destroy() {
    this.dispose(null);
  }

  private void deviceRemoved(Z2MDeviceService deviceHandler) {
    log.info("[{}]: Device removed: {}", entityID, deviceHandler);
    entityContext.ui().updateItem(entity);
    deviceHandler.dispose();
  }

  private String getDeviceTopic(Z2MDeviceDTO device) {
    return format("%s-%s/%s", mqttEntity.getEntityID(), entity.getBasicTopic(), device.getIeeeAddress());
  }

  @SneakyThrows
  public void updateDeviceConfiguration(Z2MDeviceService deviceService, String propertyName, Object value) {
    ObjectNode deviceConfiguration = this.configuration.getDevices().computeIfAbsent(deviceService.getDevice().getIeeeAddress(),
        ieee -> OBJECT_MAPPER.createObjectNode());
    if (value == null) {
      deviceConfiguration.remove(propertyName);
    } else {
      deviceConfiguration.putPOJO(propertyName, value);
    }
    YAML_OBJECT_MAPPER.writeValue(new PrintWriter(zigbee2mqttConfigurationPath.toFile()), configuration);
    // update value in runtime
    String payload = new JSONObject().put("friendly_name", deviceService.getDevice().getIeeeAddress())
                                     .put("options", new JSONObject().put(propertyName, value)).toString();
    mqttEntity.getService().getMqttClient().publish(entity.getBasicTopic() + "/bridge/config/device_options",
        payload.getBytes(), 0, false);
  }

  @RequiredArgsConstructor
  private enum Z2MResponse {
    config((payload, service) -> {

    }),
    devices((payload, service) -> {
      List<Z2MDeviceDTO> devices = OBJECT_MAPPER.readValue(payload, new TypeReference<>() {});
      Map<String, Z2MDeviceDTO> deviceMap = devices.stream().filter(d -> !d.getType().equals("Coordinator")).collect(Collectors.toMap(Z2MDeviceDTO::getIeeeAddress, d -> d));
      for (Iterator<Entry<String, Z2MDeviceService>> iterator = service.deviceHandlers.entrySet().iterator(); iterator.hasNext(); ) {
        Entry<String, Z2MDeviceService> entry = iterator.next();
        if (!deviceMap.containsKey(entry.getKey())) {
          service.deviceRemoved(entry.getValue());
          iterator.remove();
        }
      }

      for (Z2MDeviceDTO newDevice : deviceMap.values()) {
        if (!service.deviceHandlers.containsKey(newDevice.getIeeeAddress())) {
          log.info("[{}]: New device added: {}", service.entityID, newDevice);
          service.deviceHandlers.put(newDevice.getIeeeAddress(), new Z2MDeviceService(service, newDevice));
          service.entityContext.ui().updateItem(service.entity);
        }
        service.deviceHandlers.get(newDevice.getIeeeAddress()).deviceUpdated(newDevice);
      }
    }),
    extensions((payload, service) -> {

    }),
    groups((payload, service) -> {

    }),
    event((payload, service) -> {
      JSONObject jsonObject = new JSONObject(payload);
      String ieeeAddress = jsonObject.optJSONObject("data").optString("ieee_address");
      switch (jsonObject.getString("type")) {
        case "device_interview":
          String status = jsonObject.getString("status");
          Level level = status.equals("failed") ? Level.ERROR : Level.INFO;
          log.log(level, "[{}]: Device interview {} for device {}", service.entityID, status, ieeeAddress);
          break;
        case "device_announce":
          log.info("[{}]: Device announce {}", service.entityID, ieeeAddress);
          break;
      }
    }),
    info((payload, service) -> {

    }),
    logging((payload, service) -> {

    }),
    response((payload, z2MLocalCoordinatorService) -> {

    });

    private final ThrowingBiConsumer<String, Z2MLocalCoordinatorService, Exception> handler;
  }
}
