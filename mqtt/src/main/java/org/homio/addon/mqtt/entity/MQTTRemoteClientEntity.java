package org.homio.addon.mqtt.entity;

import jakarta.persistence.Entity;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.addon.mqtt.workspace.Scratch3MQTTBlocks;

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
