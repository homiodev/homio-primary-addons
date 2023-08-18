package org.homio.addon.mqtt.entity;

import jakarta.persistence.Entity;
import org.homio.addon.mqtt.workspace.Scratch3MQTTBlocks;
import org.homio.api.model.Icon;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.jetbrains.annotations.NotNull;

@Entity
@UISidebarChildren(icon = "far fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTRemoteClientEntity extends MQTTBaseEntity {

    @Override
    protected @NotNull String getDevicePrefix() {
        return "mqtt_remote";
    }

    @Override
    public String getDefaultName() {
        return "MQTT remote client";
    }

    @Override
    public Icon selectionIcon() {
        return new Icon("fas fa-mattress-pillow", "#AD971A");
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }
}
