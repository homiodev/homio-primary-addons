package org.homio.bundle.mqtt.entity;

import java.util.Objects;
import javax.persistence.Entity;
import lombok.val;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.model.ActionResponseModel;
import org.homio.bundle.api.model.HasEntityLog;
import org.homio.bundle.api.ui.UISidebarChildren;
import org.homio.bundle.api.ui.field.UIField;
import org.homio.bundle.api.ui.field.UIFieldIgnore;
import org.homio.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.homio.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.homio.bundle.api.util.SecureString;
import org.homio.bundle.mqtt.MQTTDependencyExecutableInstaller;
import org.homio.bundle.mqtt.workspace.Scratch3MQTTBlocks;
import org.jetbrains.annotations.NotNull;

@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTLocalClientEntity extends MQTTBaseEntity implements HasDynamicContextMenuActions,
    HasEntityLog {

    private static String version;

    @Override
    public String getEntityPrefix() {
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

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {
        if (!getStatus().isOnline()) {
            MQTTDependencyExecutableInstaller mosquitoInstaller = uiInputBuilder.getEntityContext().getBean(MQTTDependencyExecutableInstaller.class);
            if (mosquitoInstaller.isRequireInstallDependencies(uiInputBuilder.getEntityContext(), true)) {
                uiInputBuilder.addSelectableButton("mqtt.install-mosquito", "fas fa-mosquito-net", "#74BA36", (entityContext, params) -> {
                    entityContext.bgp().runWithProgress("install-deps-" + getClass().getSimpleName(), false,
                        progressBar -> mosquitoInstaller.installDependency(entityContext, progressBar), exception -> {
                            if (exception == null) {
                                this.testService();
                            }
                        },
                        () -> new RuntimeException("INSTALL_DEPENDENCY_IN_PROGRESS"));
                    return ActionResponseModel.success();
                });
            }
        }
    }

    @UIField(order = 1, hideInEdit = true)
    public String getVersion() {
        if (MQTTLocalClientEntity.version == null) {
            EntityContext entityContext = getEntityContext();
            val installer = entityContext.getBean(MQTTDependencyExecutableInstaller.class);
            MQTTLocalClientEntity.version = installer.getVersion(entityContext);
        }
        return version;
    }

    @Override
    public boolean deepEqual(@NotNull MQTTBaseEntity mqttEntity) {
        if (super.deepEqual(mqttEntity) && Objects.equals(this.getHostname(), mqttEntity.getHostname())) {
            return true;
        }
        return false;
    }

    @Override
    public void logBuilder(EntityLogBuilder logBuilder) {
        logBuilder.addTopicFilterByEntityID("org.homio.bundle.mqtt");
    }
}
