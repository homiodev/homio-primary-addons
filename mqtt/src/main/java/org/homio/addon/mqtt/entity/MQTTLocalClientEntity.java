package org.homio.addon.mqtt.entity;

import jakarta.persistence.Entity;
import org.homio.addon.mqtt.workspace.Scratch3MQTTBlocks;
import org.homio.api.entity.HasFirmwareVersion;
import org.homio.api.entity.log.HasEntityLog;
import org.homio.api.model.Icon;
import org.homio.api.ui.UISidebarChildren;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldIgnore;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.util.SecureString;
import org.jetbrains.annotations.NotNull;

@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTLocalClientEntity extends MQTTBaseEntity implements HasEntityLog, HasFirmwareVersion {

    @Override
    protected @NotNull String getDevicePrefix() {
        return "mqtt_local";
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
    public int getPort() {
        return super.getPort();
    }

    @UIFieldIgnore
    public String getUser() {
        return super.getUser();
    }

    @UIFieldIgnore
    public SecureString getPassword() {
        return super.getPassword();
    }

    @Override
    public @NotNull Icon selectionIcon() {
        return new Icon("fas fa-mattress-pillow", "#1CA6E2");
    }

    @Override
    @UIField(order = 1, hideInEdit = true)
    public String getFirmwareVersion() {
        return getEntityContext().install().mosquitto().getVersion();
    }

    @Override
    public void logBuilder(EntityLogBuilder logBuilder) {
        logBuilder.addTopicFilterByEntityID("org.homio.addon.mqtt");
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }
}
