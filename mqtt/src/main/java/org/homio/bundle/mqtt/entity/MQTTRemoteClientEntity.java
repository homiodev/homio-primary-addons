package org.homio.bundle.mqtt.entity;

import jakarta.persistence.Entity;
import org.homio.bundle.api.ui.UISidebarChildren;
import org.homio.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.homio.bundle.mqtt.workspace.Scratch3MQTTBlocks;
import org.jetbrains.annotations.NotNull;

@Entity
@UISidebarChildren(icon = "far fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTRemoteClientEntity extends MQTTBaseEntity {

  @Override
  public String getEntityPrefix() {
    return "mqttr_";
  }

  @Override
  public String getDefaultName() {
    return "MQTT remote client";
  }

  @Override
  public String selectionIconColor() {
    return "#AD971A";
  }

  @Override
  public void assembleActions(UIInputBuilder uiInputBuilder) {

  }
}
