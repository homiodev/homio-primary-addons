package org.touchhome.bundle.mqtt.entity;

import java.util.Objects;
import javax.persistence.Entity;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.HasEntityLog;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIFieldIgnore;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.util.SecureString;
import org.touchhome.bundle.mqtt.MQTTDependencyExecutableInstaller;
import org.touchhome.bundle.mqtt.workspace.Scratch3MQTTBlocks;

@Entity
@UISidebarChildren(icon = "fas fa-building", color = Scratch3MQTTBlocks.COLOR)
public class MQTTLocalClientEntity extends MQTTBaseEntity implements HasDynamicContextMenuActions,
    HasEntityLog {

  @Override
  public String getEntityPrefix() {
    return "mqttl_";
  }

  @Override
  public String getDefaultName() {
    return "MQTT local client";
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

  @Override
  public boolean deepEqual(@NotNull MQTTBaseEntity mqttEntity) {
    if (super.deepEqual(mqttEntity) && Objects.equals(this.getHostname(), mqttEntity.getHostname())) {
      return true;
    }
    return false;
  }

  @Override
  public void logBuilder(EntityLogBuilder logBuilder) {
    logBuilder.addTopicFilterByEntityID("org.touchhome.bundle.mqtt");
  }
}
