package org.homio.addon.mqtt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.homio.addon.mqtt.console.MQTTExplorerConsolePlugin;
import org.homio.addon.mqtt.console.header.ConsoleMQTTPublishButtonSetting;
import org.homio.addon.mqtt.setting.ConsoleMQTTClearHistorySetting;
import org.homio.addon.mqtt.setting.ConsoleRemoveMqttTreeNodeHeaderButtonSetting;
import org.homio.api.EntityContext;
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
import org.json.JSONObject;

import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.homio.addon.mqtt.entity.MQTTBaseEntity.normalize;

@Log4j2
public class MQTTService extends ServiceInstance<MQTTBaseEntity> {

    private final Map<String, List<MQTTMessage>> sysHistoryMap = new HashMap<>();
    @Getter
    private final TreeNode root = new TreeNode().setChildrenMap(new HashMap<>());
    @Getter
    private DataStorageService<MQTTMessage> storage;
    @Getter
    private MqttClient mqttClient;
    @Getter
    private long serviceHashCode;

    public MQTTService(@NotNull EntityContext entityContext, @NotNull MQTTBaseEntity entity) {
        super(entityContext, entity, true);
    }

    public static TreeNode buildUpdateTree(TreeNode treeNode) {
        if (treeNode.getParent().getId() != null) {
            TreeNode parent = buildUpdateTree(treeNode.getParent());
            if (parent != null) {
                return parent.addChild(treeNode.clone(false));
            }
        }
        return treeNode.clone(false);
    }

    @Override
    @SneakyThrows
    protected void testService() {
        destroy();
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
    protected long getEntityHashCode(MQTTBaseEntity entity) {
        return entity.getDeepHashCode();
    }

    @Override
    protected void firstInitialize() {
        this.storage = entityContext.storage().getOrCreateInMemoryService(MQTTMessage.class, entityID, (long) entity.getHistorySize());

        MQTTExplorerConsolePlugin mqttPlugin = new MQTTExplorerConsolePlugin(entityContext, this);
        entityContext.ui().registerConsolePlugin(entityID, mqttPlugin);

        entityContext.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
                jsonObject -> entity.publish(jsonObject.getString("Topic"), getContent(jsonObject),
                        jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));
        entityContext.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
                jsonObject -> entity.publish(jsonObject.getString("Topic"), getContent(jsonObject),
                        jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));

        entityContext.setting().listenValue(ConsoleMQTTClearHistorySetting.class, entityID + "-mqtt-clear-history",
                this::clearHistory);

        entityContext.setting().listenValue(ConsoleRemoveMqttTreeNodeHeaderButtonSetting.class, "mqtt-remove-node", data -> {
            if (data != null && this.entityID.equals(data.getTabID())) {
                TreeNode removedTopic = this.removeTopic(data.getNodeID());
                if (removedTopic != null) {
                    sendUpdatesToUI(data.getNodeID(), removedTopic);
                }
            }
        });

        if (entity instanceof MQTTLocalClientEntity) {
            entityContext.event().runOnceOnInternetUp("mosquitto", () ->
                    entityContext.install().mosquitto().requireAsync(null, () ->
                            log.info("Mosquitto service successfully installed")));
        }

        initialize();
    }

    @Override
    @SneakyThrows
    protected void initialize() {
        this.storage.updateQuota((long) entity.getHistorySize());
        if (!entity.getIncludeSys()) {
            sysHistoryMap.clear();
        }

        long serviceHashCode = entity.getServiceHashCode();
        if (serviceHashCode != this.serviceHashCode) {
            this.serviceHashCode = serviceHashCode;
            testServiceWithSetStatus();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (mqttClient != null) {
            if (mqttClient.isConnected()) {
                mqttClient.disconnectForcibly();
                mqttClient.close(true);
            }
            updateNotificationBlock();
        }
    }

    public List<TreeConfiguration> getValue() {
        rebuildMetadata(null);
        Set<TreeNode> values = root.getChildren();
        if (!entity.getIncludeSys()) {
            values = values.stream().filter(v -> !v.getId().equals("$SYS")).collect(Collectors.toSet());
        }
        TreeConfiguration treeConfiguration =
                new TreeConfiguration(entityID, entity.getTitle() + " (" + entity.getHostname() + ")",
                        values).setDynamicUpdateId(entityID);
        treeConfiguration.setIcon(new Icon("fas fa-m", entity.getStatus() == Status.ONLINE ? UI.Color.GREEN : UI.Color.RED));
        return Collections.singletonList(treeConfiguration);
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

    public void updateNotificationBlock() {
        entityContext.ui().addNotificationBlockOptional("MQTT", "MQTT", new Icon("fas fa-satellite-dish", "#B65BE8"));
        entityContext.ui().updateNotificationBlock("MQTT", entity);
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
            if (rawPayload.contains("/")) {
                fireEvent(topic.substring(0, topic.indexOf("/")), rawPayload);
            }

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

    private void sendUpdatesToUI(String topic, TreeNode cursor) {
        rebuildMetadata(topic.startsWith("$SYS"));
        TreeNode treeNode = buildUpdateTree(cursor);
        while (treeNode.getParent() != null) {
            treeNode = treeNode.getParent();
        }
        TreeNode root = new TreeNode();
        root.addChild(treeNode);
        entityContext.event().fireEvent(entityID, root);
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

    public void fireEvent(String prefix, Object payload) {
        entityContext.event().fireEvent(entityID + "~~~" + prefix, payload);
        entityContext.event().fireEvent(entityID, payload);
    }

    @RequiredArgsConstructor
    private class MqttClientCallbackExtended implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                fireEvent("STATUS", Status.ONLINE);
                entity.setStatusOnline();
                entityContext.ui().sendInfoMessage("MQTT server connected");
            } catch (Exception ex) {
                log.error("[{}]: Unexpected error", entityID, ex);
            } finally {
                updateNotificationBlock();
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            try {
                entityContext.ui().sendErrorMessage("MQTT connection lost", (Exception) cause);
                String msg = CommonUtils.getErrorMessage(cause);
                entity.setStatus(Status.ERROR, "Connection lost: " + msg);
                fireEvent("STATUS", Status.OFFLINE);
                entity.destroyService();

                // retry create service
                entityContext.bgp().builder("MQTT-reconnect").delay(Duration.ofSeconds(30)).execute(
                        () -> entity.getOrCreateService(entityContext));
            } catch (Exception ex) {
                log.error("[{}]: Unexpected error", entityID, ex);
            } finally {
                updateNotificationBlock();
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
