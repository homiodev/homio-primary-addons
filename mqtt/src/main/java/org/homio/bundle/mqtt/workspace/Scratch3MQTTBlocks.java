package org.homio.bundle.mqtt.workspace;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.entity.EntityFieldMetadata;
import org.homio.bundle.api.ui.field.UIField;
import org.homio.bundle.api.workspace.BroadcastLock;
import org.homio.bundle.api.workspace.WorkspaceBlock;
import org.homio.bundle.api.workspace.scratch.MenuBlock;
import org.homio.bundle.api.workspace.scratch.Scratch3Block;
import org.homio.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.homio.bundle.mqtt.MQTTEntrypoint;
import org.homio.bundle.mqtt.entity.MQTTBaseEntity;
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
  private final MenuBlock.ServerMenuBlock mqttMenu;

  public Scratch3MQTTBlocks(EntityContext entityContext, MQTTEntrypoint mqttEntrypoint) {
    super(COLOR, entityContext, mqttEntrypoint);
    setParent("storage");
    this.mqttEntrypoint = mqttEntrypoint;

    // menu
    this.mqttMenu = menuServerItems("mqttMenu", MQTTBaseEntity.class, "Select MQTT");

    // blocks
    this.subscribeToValue = ofMqtt(blockHat(30, "subscribe_payload",
        "Subscribe [TOPIC] of [MQTT] | [SETTING]", this::subscribeToValue), "#");
    this.subscribeToValue.addSetting(SubscribeSettings.class);

    this.publish = ofMqtt(blockCommand(40, "publish",
            "Publish [PAYLOAD] => [TOPIC] of [MQTT] | [SETTING]",
            this::publish),
        "iotTopic");
    this.publish.addArgument(PAYLOAD);
    this.publish.addSetting(PublishSettings.class);
  }

  private Scratch3Block ofMqtt(Scratch3Block scratch3Block, String topic) {
    scratch3Block.addArgument("MQTT", this.mqttMenu);
    scratch3Block.addArgument(TOPIC, topic);
    return scratch3Block;
  }

  private void subscribeToValue(WorkspaceBlock workspaceBlock) {
    SubscribeSettings setting = workspaceBlock.getSetting(SubscribeSettings.class);
    String payload = setting.payloadFilter;
    if (isBlank(payload) || "*".equals(payload)) {
      subscribeToValue(workspaceBlock, null);
    } else {
      subscribeToValue(workspaceBlock, payload);
    }
  }

  private void subscribeToValue(WorkspaceBlock workspaceBlock, String payload) {
    subscribeToValue(workspaceBlock, payload, workspaceBlock.getInputString(TOPIC));
  }

  private void subscribeToValue(WorkspaceBlock workspaceBlock, String payload, String topic) {
    String entityID = workspaceBlock.getMenuValue("MQTT", this.mqttMenu);
    workspaceBlock.handleNextOptional(next -> {
      String key = isEmpty(topic) ? entityID : entityID + "_" + topic;
      BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock, key, payload);
      workspaceBlock.subscribeToLock(lock, next::handle);
    });
  }

  private void publish(WorkspaceBlock workspaceBlock) {
    PublishSettings setting = workspaceBlock.getSetting(PublishSettings.class);
    MQTTBaseEntity mqttClientEntity = workspaceBlock.getMenuValueEntityRequired("MQTT", this.mqttMenu);
    String topic = workspaceBlock.getInputStringRequired(TOPIC);
    String payload = workspaceBlock.getInputStringRequired(PAYLOAD);
    mqttClientEntity.publish(topic, payload, setting.qos.ordinal(), setting.retain);
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
  public static class SubscribeSettings implements EntityFieldMetadata {

    @UIField(order = 1)
    private String payloadFilter = "*";

    @Override
    public @NotNull String getEntityID() {
      return "subs";
    }
  }

  @Getter
  @Setter
  public static class PublishSettings implements EntityFieldMetadata {

    @UIField(order = 1, icon = "fa fa-empire")
    private QoSLevel qos = QoSLevel.AtMostOnce;

    @UIField(order = 2, icon = "fa fa-history")
    private boolean retain;

    @Override
    public @NotNull String getEntityID() {
      return "subs";
    }
  }
}
