package org.homio.addon.mqtt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.homio.addon.mqtt.MosquittoInstaller;
import org.homio.addon.mqtt.console.MQTTExplorerConsolePlugin;
import org.homio.addon.mqtt.console.header.ConsoleMQTTPublishButtonSetting;
import org.homio.addon.mqtt.setting.ConsoleMQTTClearHistorySetting;
import org.homio.addon.mqtt.setting.ConsoleRemoveMqttTreeNodeHeaderButtonSetting;
import org.homio.api.Context;
import org.homio.api.ContextBGP;
import org.homio.api.ContextBGP.ProcessContext;
import org.homio.api.fs.TreeConfiguration;
import org.homio.api.fs.TreeNode;
import org.homio.api.fs.TreeNodeChip;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService.ServiceInstance;
import org.homio.api.storage.DataStorageService;
import org.homio.api.storage.SourceHistory;
import org.homio.api.storage.SourceHistoryItem;
import org.homio.api.ui.UI;
import org.homio.api.util.CommonUtils;
import org.homio.api.util.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.homio.addon.mqtt.entity.MQTTClientEntity.normalize;

public class MQTTService extends ServiceInstance<MQTTClientEntity> {

  private final Map<String, List<MQTTMessage>> sysHistoryMap = new HashMap<>();
  private final @Getter TreeNode root = new TreeNode().setChildrenMap(new HashMap<>());
  private final @Getter MosquittoInstaller mosquittoInstaller;
  private final @Getter Map<String, Map<String, BiConsumer<String, String>>> eventListeners = new ConcurrentHashMap<>();
  private final @Getter Map<String, Map<String, Pair<Pattern, BiConsumer<String, String>>>> eventPatternListeners = new ConcurrentHashMap<>();
  private final @Getter Map<String, String> lastValues = new ConcurrentHashMap<>();
  private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
  private @Getter DataStorageService<MQTTMessage> storage;
  private @Getter MqttClient mqttClient;
  private ProcessContext processContext;
  private Boolean isService;

  public MQTTService(@NotNull Context context, @NotNull MQTTClientEntity entity) {
    super(context, entity, true, "MQTT", true);
    setExposeService(true);
    setParent("MQTT");
    mosquittoInstaller = new MosquittoInstaller(context);

    new Thread(() -> {
      while (true) {
        try {
          Event event = eventQueue.take();
          handleEventListeners(event.key, event.value);
        } catch (Exception ex) {
          log.error("Error while execute event handler", ex);
        }
      }
    }, "MqttEventHandler").start();
  }

  public static TreeNode buildUpdateTree(TreeNode treeNode) {
    if (treeNode.getParent() != null && treeNode.getParent().getId() != null) {
      TreeNode parent = buildUpdateTree(treeNode.getParent());
      if (parent != null) {
        return parent.addChild(treeNode.clone(false));
      }
    }
    return treeNode.clone(false);
  }

  @SneakyThrows
  public Path getConfigFile() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(requireNonNull(mosquittoInstaller.getExecutablePath(Paths.get("mosquitto.conf"))));
    } else {
      return Files.createDirectories(Paths.get("/etc/mosquitto/conf.d")).resolve("default.conf");
    }
  }

  public void updateConfiguration(String configuration) {
    CommonUtils.writeToFile(getConfigFile(), configuration, false);
    restart();
  }

  public void removeEventListener(@NotNull String discriminator, @Nullable String topic) {
    removeListener(discriminator, topic, eventListeners);
    removeListener(discriminator, topic, eventPatternListeners);
  }

  public void addEventBehaviourListener(@NotNull String topic, @NotNull String discriminator, @NotNull BiConsumer<String, String> listener) {
    if (topic.contains("+") || topic.contains("#")) {
      String patternTopic = topic
        .replace("+", "[^/]+") // Replace "+" with "[^/]+" to match a single level
        .replace("#", ".*"); // Replace "#" with ".*" to match zero or more levels
      Pair<Pattern, BiConsumer<String, String>> handler = Pair.of(Pattern.compile(patternTopic), listener);
      eventPatternListeners.computeIfAbsent(discriminator, d -> new ConcurrentHashMap<>()).put(topic, handler);
      fireBehaviourEvent(listener, incomeTopic -> handler.getKey().matcher(incomeTopic).matches());
    } else {
      eventListeners.computeIfAbsent(discriminator, d -> new ConcurrentHashMap<>()).put(topic, listener);
      fireBehaviourEvent(listener, topic::equals);
    }
  }

  public void fireEvent(String topic, String payload) {
    lastValues.put(topic, payload);
    eventQueue.add(new Event(topic, payload));
  }

  @Override
  public void destroy(boolean forRestart, Exception ex) {
  }

  public List<TreeConfiguration> getValue() {
    rebuildMetadata(null);
    Set<TreeNode> values = root.getChildren();
    if (!entity.getIncludeSys() && values != null) {
      values = values.stream().filter(v -> !"$SYS".equals(v.getId())).collect(Collectors.toSet());
    }
    TreeConfiguration treeConfiguration =
      new TreeConfiguration(entityID, entity.getTitle() + " (" + entity.getHostname() + ")",
        values).setDynamicUpdateId("tree-" + entityID);
    treeConfiguration.setIcon(new Icon("fas fa-m", entity.getStatus() == Status.ONLINE ? UI.Color.GREEN : UI.Color.RED));
    return Collections.singletonList(treeConfiguration);
  }

  @Override
  @SneakyThrows
  protected void testService() {
    if (mqttClient != null) {
      if (mqttClient.isConnected()) {
        return;
      }
      mqttClient.disconnectForcibly(5000, 5000);
      mqttClient.close(true);
    }
    createMQTTClient();
  }

  private byte[] getContent(JSONObject jsonObject) {
    String content = jsonObject.getString("Content");
    return content == null ? new byte[0] : content.getBytes(UTF_8);
  }

  public void clearHistory() {
    storage.deleteAll();
    this.sysHistoryMap.clear();
  }

  @Override
  @SneakyThrows
  protected void initialize() {
    if (!entity.isStart()) {
      entity.setStatus(Status.OFFLINE);
      return;
    }
    this.storage.updateQuota((long) entity.getHistorySize());
    if (!entity.getIncludeSys()) {
      sysHistoryMap.clear();
    }
    if (isService == null) {
      isService = SystemUtils.IS_OS_LINUX && context.hardware().isSystemCtlExists("mosquitto");
    }
    testServiceWithSetStatus();
    if (!entity.getStatus().isOnline()) {
      restart();
    }
  }

  @Override
  public String isRequireRestartService() {
    if (!entity.getStatus().isOnline()) {
      return "Status: " + entity.getStatus();
    }
    if (mqttClient == null || !mqttClient.isConnected()) {
      return "Mqtt client disconnected";
    }
    return null;
  }

  private void handleEventListeners(String key, String value) {
    for (Map<String, BiConsumer<String, String>> entry : eventListeners.values()) {
      BiConsumer<String, String> listener = entry.get(key);
      if (listener != null) {
        listener.accept(key, value);
      }
    }
    for (Map<String, Pair<Pattern, BiConsumer<String, String>>> entry : eventPatternListeners.values()) {
      for (Pair<Pattern, BiConsumer<String, String>> listener : entry.values()) {
        if (listener.getKey().matcher(key).matches()) {
          listener.getValue().accept(key, value);
        }
      }
    }
  }

  @SneakyThrows
  private void restart() {
    mosquittoInstaller.installLatest();
    if (isService) {
      context.hardware().stopSystemCtl("mosquitto");
    } else {
      ContextBGP.cancel(processContext);
    }
    Path pwdFile = createMosquittoPasswordFile();
    Path configPath = rewriteConfiguration(pwdFile);

    if (isService) {
      context.hardware().startSystemCtl("mosquitto");
    } else {
      processContext = context.bgp().processBuilder(entity, log)
        .execute(mosquittoInstaller.getExecutable(), "-c", configPath.toString());
    }
    testServiceWithSetStatus();
  }

  private Path rewriteConfiguration(Path pwdFile) {
    Path configFile = getConfigFile();
    String configContent = Stream.of(
      "listener " + entity.getPort() + " 0.0.0.0",
      "persistence " + entity.isPersistenceData(),
      "allow_anonymous false",
      "password_file " + pwdFile,
      "log_dest stdout",
      "log_dest file " + entity.getLogFilePath(),
      ""
    ).collect(Collectors.joining(System.lineSeparator()));
    try {
      if (!configContent.equals(Files.readString(configFile))) {
        throw new IllegalArgumentException("Data not match");
      }
    } catch (Exception ignore) {
      CommonUtils.writeToFile(configFile, configContent, false);
    }
    return configFile;
  }

  // TODO: Very not efficient way
  public void rebuildMetadata(Boolean sysPath) {
    Predicate<? super TreeNode> filter = treeNode -> true;
    if (sysPath != null) {
      filter = treeNode -> Boolean.TRUE.equals(sysPath) && treeNode.getId().startsWith("$SYS") ||
                           Boolean.FALSE.equals(sysPath) && !treeNode.getId().startsWith("$SYS");
    }
    for (TreeNode child : root.getChildren().stream().filter(filter).toList()) {
      Map<String, List<MQTTMessage>> sourceMap = child.getId().equals("$SYS") ? sysHistoryMap :
        storage.findByPattern("topic", child.getId() + ".*").stream()
          .collect(Collectors.groupingBy(MQTTMessage::getTopic));
      child.visitTree(treeNode -> {
        List<MQTTMessage> histList = sourceMap.get(treeNode.getId());
        if (histList != null && histList.size() > 1) {
          TreeNodeChip hist = new TreeNodeChip(new Icon("fas fa-landmark"),
            "H[" + treeNode.getAttributes().getMeta().getInt("hs") + "]")
            .setClickable(true).setBgColor("#83614A").setMetadata(new JSONObject().put("type", "history"));
          treeNode.getAttributes().setChips(hist);
        }

        TreeNode.TextBuilder textBuilder = treeNode.getAttributes().textsBuilder();
        if (treeNode.getAttributes().isDir()) {
          textBuilder.addText(getDirText(treeNode, sourceMap), null);
        }
        if (histList != null) {
          String payload = histList.get(histList.size() - 1).getValue().toString();
          textBuilder.addText("= ", null);
          String color = getTextColor(payload);
          treeNode.getAttributes().setColor(color);

          textBuilder.addText(payload, color);
        }
      });
    }
  }

  public SourceHistory getSourceHistory(String topic) {
    SourceHistory sourceHistory;
    if (topic.startsWith("$SYS/")) {
      List<MQTTMessage> mqttMessages = sysHistoryMap.getOrDefault(topic, Collections.emptyList());
      sourceHistory = new SourceHistory(mqttMessages.size(), null, null, null);
    } else {
      sourceHistory = storage.getSourceHistory("topic", topic);
    }
    return sourceHistory.setName(topic);
  }

  public List<SourceHistoryItem> getSourceHistoryItems(String topic, int from, int count) {
    if (topic.startsWith("$SYS/")) {
      List<MQTTMessage> mqttMessages = sysHistoryMap.getOrDefault(topic, Collections.emptyList());
      return mqttMessages.subList(from, from + count).stream()
        .map(mqttMessage -> new SourceHistoryItem(mqttMessage.getCreated(), mqttMessage))
        .sorted()
        .collect(Collectors.toList());
    }
    return storage.getSourceHistoryItems("topic", topic, from, count);
  }

  public Object getRawValue(String topic) {
    if (topic.startsWith("$SYS/")) {
      List<MQTTMessage> mqttMessages = sysHistoryMap.getOrDefault(topic, Collections.emptyList());
      return mqttMessages.isEmpty() ? null : mqttMessages.iterator().next().getValue();
    }
    MQTTMessage mqttMessage = storage.findLatestBy("topic", topic);
    return mqttMessage == null ? null : mqttMessage.getValue();
  }

  @SneakyThrows
  @Override
  protected void firstInitialize() {
    this.storage = context.db().getOrCreateInMemoryService(MQTTMessage.class, entityID, (long) entity.getHistorySize());

    MQTTExplorerConsolePlugin mqttPlugin = new MQTTExplorerConsolePlugin(context, this);
    context.ui().console().registerPlugin(entityID, mqttPlugin);

    context.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
      jsonObject -> entity.publish(jsonObject.getString("Topic"), getContent(jsonObject),
        jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));
    context.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
      jsonObject -> entity.publish(jsonObject.getString("Topic"), getContent(jsonObject),
        jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));

    context.setting().listenValue(ConsoleMQTTClearHistorySetting.class, entityID + "-mqtt-clear-history",
      this::clearHistory);

    context.setting().listenValue(ConsoleRemoveMqttTreeNodeHeaderButtonSetting.class, "mqtt-remove-node", data -> {
      if (data != null && this.entityID.equals(data.getTabID())) {
        TreeNode removedTopic = this.removeTopic(data.getNodeID());
        if (removedTopic != null) {
          sendUpdatesToUI(data.getNodeID(), removedTopic);
        }
      }
    });

    initialize();
  }

  private TreeNode removeTopic(String topic) {
    TreeNode cursor = root.getChild(Paths.get(topic));
    if (cursor != null) {
      // remove all history from removed node and all sub nodes
      cursor.visitTree(treeNode -> {
        sysHistoryMap.remove(treeNode.getId());
        storage.deleteBy("topic", treeNode.getId());
      });
      cursor.remove();
      cursor.getAttributes().setRemoved(true);
    }
    return cursor;
  }

  private void messageArrived(String topic, MqttMessage mqttMessage, Object payload, String rawPayload) {
    try {
      boolean sysNode = topic.startsWith("$SYS");
      if (!entity.getIncludeSys() && sysNode) {
        return;
      }

      // fire events with raw payload
      fireEvent(topic, rawPayload);

      TreeNode cursor;
      if (payload == null) {
        cursor = this.removeTopic(topic);
      } else {
        cursor = findTopic(topic, root, rawPayload.length());
        updateNodeHistory(topic, mqttMessage, payload, cursor, sysNode);
      }

      if (cursor != null) {
        sendUpdatesToUI(topic, cursor);
      }
    } catch (Exception ex) {
      log.error("[{}]: Unable to proceed topic: {}, message: {}", entityID, topic, mqttMessage, ex);
    }
  }

  private Path createMosquittoPasswordFile() {
    int newCode = (entity.getUser() + entity.getPassword().asString()).hashCode();
    Path passwordFile = CommonUtils.getConfigPath().resolve("mosquitto_passwd");
    if (entity.getUserPasswordHash() != newCode || !Files.exists(passwordFile)) {
      String pwdCmd = mosquittoInstaller.getExecutablePath(Paths.get("mosquitto_passwd"));
      String cmd = "%s -b -c %s %s %s".formatted(pwdCmd, passwordFile, entity.getUser(), entity.getPassword().asString());
      context.hardware().execute(cmd, 600);
      context.db().updateDelayed(entity, e -> e.setUserPasswordHash(newCode));

      if (!Files.exists(passwordFile)) {
        throw new IllegalStateException("Unable to create mosquitto password file");
      }
    }
    return passwordFile;
  }

  private TreeNode findTopic(String value, TreeNode cursor, int payloadLength) {
    for (String path : value.split("/")) {
      if (!path.isBlank()) {
        cursor = cursor.addChildIfNotFound(path, () -> {
          TreeNode treeNode =
            new TreeNode(false, true, path, path, (long) payloadLength, System.currentTimeMillis(), null, null);
          treeNode.getAttributes().setMeta(new JSONObject().put("hs", 0));
          return treeNode;
        }, true);
      }
    }
    return cursor;
  }

  private void updateNodeHistory(String topic, MqttMessage mqttMessage, Object payload, TreeNode cursor, boolean sysNode) {
    JSONObject meta = cursor.getAttributes().getMeta();
    meta.put("hs", meta.getInt("hs") + 1);

    MQTTMessage message = new MQTTMessage(
      topic,
      payload,
      mqttMessage.isRetained(),
      mqttMessage.isDuplicate(),
      mqttMessage.getQos());

    if (sysNode) {
      // keep only one message
      sysHistoryMap.putIfAbsent(cursor.getId(), new ArrayList<>(List.of(message)));
      sysHistoryMap.get(cursor.getId()).set(0, message);
    } else {
      storage.save(message);
    }
  }

  private void createMQTTClient() throws MqttException {
    String serverURL = format("tcp://%s:%d", entity.getHostname(), entity.getPort());
    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(false);
    options.setCleanSession(entity.getMqttCleanSessionOnConnect());
    options.setConnectionTimeout((int) TimeUnit.SECONDS.toMillis(entity.getConnectionTimeout()));
    options.setKeepAliveInterval(entity.getMqttKeepAlive());
    options.setUserName(entity.getUser());
    options.setPassword(entity.getPassword().asString().toCharArray());

    mqttClient = new MqttClient(serverURL, entity.getMqttClientID(), new MemoryPersistence());
    mqttClient.setCallback(new MqttClientCallbackExtended());
    mqttClient.connect(options);
    mqttClient.subscribe(new String[]{"#", "$SYS/#"});
  }

  private String getDirText(TreeNode cursor, Map<String, List<MQTTMessage>> sourceMap) {
    AtomicInteger topics = new AtomicInteger();
    AtomicInteger messages = new AtomicInteger();

    cursor.visitTree(treeNode -> {
      topics.incrementAndGet();
      int msgs = Optional.ofNullable(sourceMap.get(treeNode.getId())).map(List::size).orElse(0);
      messages.addAndGet(msgs);
    });
    return "(" + topics.get() + " topics, " + messages.get() + " messages)";
  }

  private String getTextColor(String payload) {
    if (JsonUtils.isValidJson(payload)) {
      return "#06B2D4";
    }
    if (NumberUtils.isCreatable(payload)) {
      return "#80D406";
    }
    return null;
  }

  private void sendUpdatesToUI(String topic, TreeNode cursor) {
    rebuildMetadata(topic.startsWith("$SYS"));
    TreeNode treeNode = buildUpdateTree(cursor);
    while (treeNode.getParent() != null) {
      treeNode = treeNode.getParent();
    }
    TreeNode root = new TreeNode();
    root.addChild(treeNode);
    context.ui().sendDynamicUpdate("tree-" + entityID, root);
  }

  private void removeListener(@NotNull String discriminator, @Nullable String topic, @NotNull Map<String, ?> holder) {
    if (topic == null) {
      holder.remove(discriminator);
    } else {
      Map<String, ?> map = (Map<String, ?>) holder.get(discriminator);
      if (map != null) {
        map.remove(topic);
        if (map.isEmpty()) {
          holder.remove(discriminator);
        }
      }
    }
  }

  private void fireBehaviourEvent(BiConsumer<String, String> listener, Function<String, Boolean> predicate) {
    if (!lastValues.isEmpty()) {
      Map<String, String> copy = new HashMap<>(lastValues);
      context.bgp().builder("mqtt-behaviour-handle-" + System.currentTimeMillis())
        .execute(() -> {
          for (Entry<String, String> entry : copy.entrySet()) {
            if (predicate.apply(entry.getKey())) {
              listener.accept(entry.getKey(), entry.getValue());
            }
          }
        });
    }
  }

  private record Event(String key, String value) {
  }

  @RequiredArgsConstructor
  private class MqttClientCallbackExtended implements MqttCallbackExtended {

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
      try {
        // fireEvent("STATUS", Status.ONLINE.toString());
        entity.setStatusOnline();
        context.ui().toastr().info("MQTT server connected");
      } catch (Exception ex) {
        log.error("[{}]: Unexpected error", entityID, ex);
      }
    }

    @Override
    public void connectionLost(Throwable cause) {
      try {
        context.ui().toastr().error("MQTT connection lost", (Exception) cause);
        String msg = CommonUtils.getErrorMessage(cause);
        entity.setStatus(Status.ERROR, "Connection lost: " + msg);
        // fireEvent("STATUS", Status.OFFLINE.toString());
        entity.destroyService(null);

        // retry create service
        context.bgp().builder("MQTT-reconnect").delay(Duration.ofSeconds(30)).execute(
          () -> entity.getOrCreateService(context));
      } catch (Exception ex) {
        log.error("[{}]: Unexpected error", entityID, ex);
      }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
      try {
        topic = normalize(topic);
        String payload = new String(mqttMessage.getPayload());
        Object convertedPayload;
        try {
          convertedPayload = NumberFormat.getInstance().parse(payload).floatValue();
        } catch (Exception ex) {
          convertedPayload = StringUtils.isEmpty(payload) ? null : payload;
        }

        MQTTService.this.messageArrived(topic, mqttMessage, convertedPayload, payload);
      } catch (Exception ex) {
        log.error("[{}]: Unexpected mqtt error", entityID, ex);
      }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
  }
}
