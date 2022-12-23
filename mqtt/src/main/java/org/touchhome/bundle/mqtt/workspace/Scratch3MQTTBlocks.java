package org.touchhome.bundle.mqtt.workspace;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.ArgumentType;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.mqtt.MQTTEntrypoint;
import org.touchhome.bundle.mqtt.entity.MQTTBaseEntity;

@Getter
@Component
public class Scratch3MQTTBlocks extends Scratch3ExtensionBlocks {

  public static final String COLOR = "#7E2CAC";
  private static final String TOPIC = "TOPIC";
  private static final String PAYLOAD = "PAYLOAD";
  private static final String LEVEL = "LEVEL";
  private static final String RETAINED = "RETAINED";

  private final MQTTEntrypoint mqttEntrypoint;
  private final MenuBlock.StaticMenuBlock<QoSLevel> publishLevelMenu;

  private final Scratch3Block publish;
  private final Scratch3Block subscribeToValue;
  private final MenuBlock.ServerMenuBlock mqttMenu;

  public Scratch3MQTTBlocks(EntityContext entityContext, MQTTEntrypoint mqttEntrypoint) {
    super(COLOR, entityContext, mqttEntrypoint);
    setParent("storage");
    this.mqttEntrypoint = mqttEntrypoint;

    // menu
    this.publishLevelMenu = menuStatic(LEVEL, QoSLevel.class, QoSLevel.AtMostOnce);
    this.mqttMenu = menuServerItems("mqttMenu", MQTTBaseEntity.class, "Select MQTT");

    // blocks
    this.subscribeToValue = ofMqtt(blockHat(30, "subscribe_payload",
        "Subscribe to topic [TOPIC] and data [PAYLOAD] of [MQTT]", this::subscribeToValue), "#");
    this.subscribeToValue.addArgument(PAYLOAD, "*");

    this.publish = ofMqtt(blockCommand(40, "publish",
            "Publish payload [PAYLOAD] to topic [TOPIC] of [MQTT] | Level: [LEVEL], Retained: [RETAINED]",
            this::publish),
        "iotTopic");
    this.publish.addArgument(PAYLOAD);
    this.publish.addArgument(LEVEL, this.publishLevelMenu);
    this.publish.addArgument(RETAINED, ArgumentType.checkbox);
  }

  private Scratch3Block ofMqtt(Scratch3Block scratch3Block, String topic) {
    scratch3Block.addArgument("MQTT", this.mqttMenu);
    scratch3Block.addArgument(TOPIC, topic);
    return scratch3Block;
  }

  private void subscribeToValue(WorkspaceBlock workspaceBlock) {
    String payload = workspaceBlock.getInputString(PAYLOAD);
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
    MQTTBaseEntity mqttClientEntity = workspaceBlock.getMenuValueEntityRequired("MQTT", this.mqttMenu);
    String topic = workspaceBlock.getInputStringRequired(TOPIC);
    String payload = workspaceBlock.getInputStringRequired(PAYLOAD);
    mqttClientEntity.publish(topic, payload, workspaceBlock.getMenuValue(LEVEL, this.publishLevelMenu).ordinal(),
        workspaceBlock.getInputBoolean(RETAINED));
  }

  @RequiredArgsConstructor
  private enum QoSLevel {
    AtMostOnce("At Most Once"),
    AtLeastOnce("At Least Once"),
    ExactlyOnce("Exactly Once");

    private final String name;

    @Override
    public String toString() {
      return name;
    }
  }
}
