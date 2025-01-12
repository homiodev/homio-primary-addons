package org.homio.addon.mqtt.console;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.mqtt.console.header.ConsoleMQTTPublishButtonSetting;
import org.homio.addon.mqtt.entity.MQTTClientEntity;
import org.homio.addon.mqtt.entity.MQTTMessage;
import org.homio.addon.mqtt.entity.MQTTService;
import org.homio.addon.mqtt.setting.ConsoleRemoveMqttTreeNodeHeaderButtonSetting;
import org.homio.api.Context;
import org.homio.api.console.ConsolePluginTree;
import org.homio.api.entity.UserEntity;
import org.homio.api.fs.TreeConfiguration;
import org.homio.api.model.ActionResponseModel;
import org.homio.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.homio.api.storage.SortBy;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Getter
@Log4j2
public class MQTTExplorerConsolePlugin implements ConsolePluginTree {

  private final @Accessors(fluent = true) Context context;
  private final MQTTService mqttService;

  public MQTTExplorerConsolePlugin(Context context, MQTTService mqttService) {
    this.context = context;
    this.mqttService = mqttService;
  }

  @Override
  public String getParentTab() {
    return "MQTT";
  }

  @Override
  public ActionResponseModel executeAction(@NotNull String entityID, @NotNull JSONObject metadata) {
    assertMQTTAccess(UserEntity::assertEditAccess);

    if ("history".equals(metadata.optString("type"))) {
      List<MQTTMessage> topic = mqttService.getStorage().findAllBy("topic", entityID, SortBy.sortDesc("created"),
        null);
      return ActionResponseModel.showJson("History", new ArrayList<>(topic));
    }
    return ActionResponseModel.showWarn("Unable to handle command: " + entityID);
  }

  @Override
  public List<TreeConfiguration> getValue() {
    assertMQTTAccess(UserEntity::assertViewAccess);
    return mqttService.getValue();
  }

  @Override
  public boolean isEnabled() {
    assertMQTTAccess(UserEntity::assertViewAccess);
    return true;
  }

  @Override
  public @NotNull String getName() {
    return "MQTT";
  }

  @Override
  public Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> getHeaderActions() {
    Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
    headerActions.put("publish", ConsoleMQTTPublishButtonSetting.class);
    headerActions.put("remove", ConsoleRemoveMqttTreeNodeHeaderButtonSetting.class);
    return headerActions;
  }

  @Override
  public boolean hasRefreshIntervalSetting() {
    return false;
  }

  private void assertMQTTAccess(BiConsumer<UserEntity, MQTTClientEntity> predicate) {
    UserEntity user = context.user().getLoggedInUser();
    if (user != null) {
      predicate.accept(user, mqttService.getEntity());
    }
  }
}
