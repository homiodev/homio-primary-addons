package org.homio.addon.mqtt.workspace;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.mqtt.MQTTEntrypoint;
import org.homio.api.Context;
import org.homio.api.ContextService;
import org.homio.api.ContextService.MQTTEntityService;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.selection.UIFieldEntityTypeSelection;
import org.homio.api.workspace.Lock;
import org.homio.api.workspace.WorkspaceBlock;
import org.homio.api.workspace.scratch.Scratch3Block;
import org.homio.api.workspace.scratch.Scratch3Block.ScratchSettingBaseEntity;
import org.homio.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Scratch3MQTTBlocks extends Scratch3ExtensionBlocks {

    public static final String COLOR = "#7E2CAC";
    private static final String TOPIC = "TOPIC";
    private static final String PAYLOAD = "PAYLOAD";
    private static final String LEVEL = "LEVEL";
    private static final String RETAINED = "RETAINED";

    private final MQTTEntrypoint mqttEntrypoint;

    private final Scratch3Block publish;
    private final Scratch3Block subscribeToValue;

    public Scratch3MQTTBlocks(Context context, MQTTEntrypoint mqttEntrypoint) {
        super(COLOR, context, mqttEntrypoint);
        setParent(ScratchParent.communication);
        this.mqttEntrypoint = mqttEntrypoint;

        String mqttEntity = context.service().getPrimaryMqttEntity();

        // blocks
        this.subscribeToValue = ofMqtt(blockHat(30, "subscribe_payload",
            "Subscribe [TOPIC] | [SETTING]", this::subscribeToValue), "#");
        this.subscribeToValue.addSetting(new SubscribeSettings(mqttEntity));

        this.publish = ofMqtt(blockCommand(40, "publish",
                "Publish [PAYLOAD] => [TOPIC] | [SETTING]",
                        this::publish),
                "iotTopic");
        this.publish.addArgument(PAYLOAD);
        this.publish.addSetting(new PublishSettings(mqttEntity));
    }

    private Scratch3Block ofMqtt(Scratch3Block scratch3Block, String topic) {
        scratch3Block.addArgument(TOPIC, topic);
        return scratch3Block;
    }

    private void subscribeToValue(WorkspaceBlock workspaceBlock) {
        SubscribeSettings setting = workspaceBlock.getSetting(SubscribeSettings.class);
        String payload = setting.payloadFilter;
        if (isBlank(payload) || "*".equals(payload)) {
            subscribeToValue(workspaceBlock, null, setting);
        } else {
            subscribeToValue(workspaceBlock, payload, setting);
        }
    }

    private void subscribeToValue(@NotNull WorkspaceBlock workspaceBlock, String payload, SubscribeSettings setting) {
        String topic = workspaceBlock.getInputString(TOPIC);
        MQTTEntityService mqttEntityService = getMqttEntityService(setting.getMqttEntity());
        Pattern pattern = payload == null ? null : Pattern.compile(payload);
        workspaceBlock.handleNextOptional(next -> {
            String key = isEmpty(topic) ? "#" : topic;
            Lock lock = workspaceBlock.getLockManager().getLock(workspaceBlock, key, payload);
            mqttEntityService.addListener(topic, workspaceBlock.getBlockId(), value -> {
                if (StringUtils.isEmpty(payload) || pattern.matcher(value).matches()) {
                    lock.signalAll(value);
                }
            });
            workspaceBlock.subscribeToLock(lock, next::handle);
        });
        workspaceBlock.onRelease(() -> mqttEntityService.removeListener(topic, workspaceBlock.getBlockId()));
    }

    private @NotNull MQTTEntityService getMqttEntityService(String mqttEntityID) {
        MQTTEntityService mqttEntityService = context.service().getMQTTEntityService(mqttEntityID);
        if (mqttEntityService == null) {
            throw new IllegalArgumentException("Unable to find mqtt service: " + mqttEntityID);
        }
        return mqttEntityService;
    }

    private void publish(WorkspaceBlock workspaceBlock) {
        PublishSettings setting = workspaceBlock.getSetting(PublishSettings.class);
        MQTTEntityService mqttEntityService = getMqttEntityService(setting.getMqttEntity());
        String topic = workspaceBlock.getInputStringRequired(TOPIC);
        String payload = workspaceBlock.getInputString(PAYLOAD);
        mqttEntityService.publish(topic, payload == null ? new byte[0] : payload.getBytes(UTF_8), setting.qos.ordinal(), setting.retain);
    }

    @RequiredArgsConstructor
    private enum QoSLevel {
        AtMostOnce("At Most Once (0)"),
        AtLeastOnce("At Least Once (1)"),
        ExactlyOnce("Exactly Once (2)");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SubscribeSettings implements ScratchSettingBaseEntity {

        @UIField(order = 1)
        @UIFieldEntityTypeSelection(type = ContextService.MQTT_SERVICE)
        private String mqttEntity;

        @UIField(order = 2)
        private String payloadFilter = "*";

        public SubscribeSettings(String mqttEntity) {
            this.mqttEntity = mqttEntity;
        }
    }

    @Getter
    @Setter
    public static class PublishSettings implements ScratchSettingBaseEntity {

        @UIField(order = 1)
        @UIFieldEntityTypeSelection(type = ContextService.MQTT_SERVICE)
        private String mqttEntity;

        @UIField(order = 2, icon = "fa fa-empire")
        private QoSLevel qos = QoSLevel.AtMostOnce;

        @UIField(order = 3, icon = "fa fa-history")
        private boolean retain;

        public PublishSettings(String mqttEntity) {
            this.mqttEntity = mqttEntity;
        }
    }
}
