package org.homio.addon.mqtt.entity;

import static org.homio.addon.mqtt.entity.MQTTBaseEntity.normalize;

import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.homio.addon.mqtt.console.MQTTExplorerConsolePlugin;
import org.homio.addon.mqtt.console.header.ConsoleMQTTPublishButtonSetting;
import org.homio.addon.mqtt.setting.ConsoleMQTTClearHistorySetting;
import org.homio.addon.mqtt.setting.ConsoleRemoveNodeHeaderButtonSetting;
import org.homio.api.EntityContext;
import org.homio.api.fs.TreeConfiguration;
import org.homio.api.fs.TreeNode;
import org.homio.api.fs.TreeNodeChip;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.service.EntityService;
import org.homio.api.storage.DataStorageService;
import org.homio.api.storage.InMemoryDB;
import org.homio.api.storage.SourceHistory;
import org.homio.api.storage.SourceHistoryItem;
import org.homio.api.ui.UI;
import org.homio.api.util.CommonUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

@Log4j2
public class MQTTService implements EntityService.ServiceInstance<MQTTBaseEntity> {

    private final Map<String, List<MQTTMessage>> sysHistoryMap = new HashMap<>();

    @Getter
    @NotNull
    private final DataStorageService<MQTTMessage> storage;
    @NotNull
    private final EntityContext entityContext;
    @Getter
    private final TreeNode root = new TreeNode().setChildrenMap(new HashMap<>());
    @NotNull
    private final String entityID;
    @Getter
    private MqttClient mqttClient;
    @Getter
    @NotNull
    private MQTTBaseEntity entity;

    public MQTTService(@NotNull MQTTBaseEntity entity, @NotNull EntityContext entityContext) {
        this.entity = entity;
        this.entityID = entity.getEntityID();
        this.entityContext = entityContext;
        this.storage = InMemoryDB.getOrCreateService(MQTTMessage.class, entityID, (long) entity.getHistorySize());

        MQTTExplorerConsolePlugin mqttPlugin = new MQTTExplorerConsolePlugin(entityContext, this);
        this.entityContext.ui().registerConsolePlugin(entityID, mqttPlugin);

        entityContext.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
            jsonObject -> entity.publish(jsonObject.getString("Topic"), jsonObject.getString("Content"),
                jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));
        entityContext.setting().listenValue(ConsoleMQTTPublishButtonSetting.class, entityID + "-mqtt-publish",
            jsonObject -> entity.publish(jsonObject.getString("Topic"), jsonObject.getString("Content"),
                jsonObject.getInt("QoS"), jsonObject.getBoolean("Retain")));

        entityContext.setting().listenValue(ConsoleMQTTClearHistorySetting.class, entityID + "-mqtt-clear-history",
            this::clearHistory);

        entityContext.setting().listenValue(ConsoleRemoveNodeHeaderButtonSetting.class, "mqtt-remove-node", data -> {
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

    public void clearHistory() {
        storage.deleteAll();
        this.sysHistoryMap.clear();
    }

    @Override
    @SneakyThrows
    public boolean entityUpdated(MQTTBaseEntity entity) {
        boolean updated = false;
        this.storage.updateQuota((long) entity.getHistorySize());
        if (!entity.getIncludeSys()) {
            sysHistoryMap.clear();
        }

        if (!Objects.equals(this.entity.getMqttCleanSessionOnConnect(), entity.getMqttCleanSessionOnConnect()) ||
            !Objects.equals(this.entity.getConnectionTimeout(), entity.getConnectionTimeout()) ||
            !Objects.equals(this.entity.getMqttKeepAlive(), entity.getMqttKeepAlive()) ||
            !Objects.equals(this.entity.getMqttUser(), entity.getMqttUser()) ||
            !Objects.equals(this.entity.getMqttPassword().asString(), entity.getMqttPassword().asString()) ||
            !Objects.equals(this.entity.getHostname(), entity.getHostname()) ||
            !Objects.equals(this.entity.getMqttClientID(), entity.getMqttClientID()) ||
            !Objects.equals(this.entity.getMqttPort(), entity.getMqttPort())) {
            this.destroy();
            updated = true;

        }
        this.entity = entity;
        return updated;
    }

    @Override
    public void destroy() throws Exception {
        if (mqttClient.isConnected()) {
            mqttClient.disconnectForcibly();
            mqttClient.close(true);
        }
        updateNotificationBlock();
    }

    @Override
    public boolean testService() throws Exception {
        createMQTTClient();
        return true;
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
        entityContext.ui().updateNotificationBlock("MQTT", builder -> builder.addEntityInfo(entity));
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

    private void messageArrived(String topic, MqttMessage mqttMessage, Object payload, int payloadLength) {
        try {
            boolean sysNode = topic.startsWith("$SYS");
            if (!entity.getIncludeSys() && sysNode) {
                return;
            }
            TreeNode cursor;
            if (payload == null) {
                cursor = this.removeTopic(topic);
            } else {
                cursor = findTopic(topic, root, payloadLength);
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
        this.entityContext.event().fireEvent(entityID, root);
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
        String serverURL = String.format("tcp://%s:%d", entity.getHostname(), entity.getMqttPort());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);
        options.setCleanSession(entity.getMqttCleanSessionOnConnect());
        options.setConnectionTimeout((int) TimeUnit.SECONDS.toMillis(entity.getConnectionTimeout()));
        options.setKeepAliveInterval(entity.getMqttKeepAlive());
        options.setUserName(entity.getMqttUser());
        options.setPassword(entity.getMqttPassword().asString().toCharArray());

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
        if (CommonUtils.isValidJson(payload)) {
            return "#06B2D4";
        }
        if (NumberUtils.isCreatable(payload)) {
            return "#80D406";
        }
        return null;
    }

    @RequiredArgsConstructor
    private class MqttClientCallbackExtended implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                entityContext.event().fireEvent("mqtt-status", Status.ONLINE);
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
                entityContext.event().fireEvent("mqtt-status", Status.OFFLINE);
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

                MQTTService.this.messageArrived(topic, mqttMessage, convertedPayload, payload.length());

                entityContext.event().fireEvent(entityID, payload).fireEvent(entityID + "-" + topic, payload);
                if (payload.contains("/")) {
                    entityContext.event().fireEvent(entityID, payload).fireEvent(entityID + "-" + topic.substring(0, topic.indexOf("/")), payload);
                }
            } catch (Exception ex) {
                log.error("[{}]: Unexpected mqtt error", entityID, ex);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

}
