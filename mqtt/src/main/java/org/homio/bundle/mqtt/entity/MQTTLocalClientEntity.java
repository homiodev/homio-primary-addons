package org.homio.bundle.mqtt.entity;

import java.util.Objects;
import jakarta.persistence.Entity;
import org.homio.bundle.api.model.HasEntityLog;
import org.homio.bundle.api.ui.UISidebarChildren;
import org.homio.bundle.api.ui.field.UIField;
import org.homio.bundle.api.ui.field.UIFieldIgnore;
import org.homio.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.homio.bundle.api.util.SecureString;
import org.homio.bundle.mqtt.workspace.Scratch3MQTTBlocks;
import org.jetbrains.annotations.NotNull;

@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTLocalClientEntity extends MQTTBaseEntity implements HasEntityLog {

    @Override
    public @NotNull String getEntityPrefix() {
        return "mqttl_";
    }

    @Override
    public String getDefaultName() {
        return "MQTT Mosquito";
    }

    @UIFieldIgnore
    public String getHostname() {
        return super.getHostname();
    }

    @UIFieldIgnore
    public int getMqttPort() {
        return super.getMqttPort();
    }

    @UIFieldIgnore
    public String getMqttUser() {
        return super.getMqttUser();
    }

    @UIFieldIgnore
    public SecureString getMqttPassword() {
        return super.getMqttPassword();
    }

    @Override
    public String selectionIconColor() {
        return "#1CA6E2";
    }

    @UIField(order = 1, hideInEdit = true)
    public String getVersion() {
        return getEntityContext().install().mosquitto().getVersion();
    }

    @Override
    public boolean deepEqual(@NotNull MQTTBaseEntity mqttEntity) {
        return super.deepEqual(mqttEntity) && Objects.equals(this.getHostname(), mqttEntity.getHostname());
    }

    @Override
    public void logBuilder(EntityLogBuilder logBuilder) {
        logBuilder.addTopicFilterByEntityID("org.homio.bundle.mqtt");
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }
}
