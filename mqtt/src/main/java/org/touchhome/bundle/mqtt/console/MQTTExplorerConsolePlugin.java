package org.touchhome.bundle.mqtt.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginTree;
import org.touchhome.bundle.api.entity.TreeConfiguration;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;
import org.touchhome.bundle.api.setting.console.header.RemoveNodeConsoleHeaderButtonSetting;
import org.touchhome.bundle.mqtt.console.header.ConsoleMQTTPublishButtonSetting;
import org.touchhome.bundle.mqtt.entity.MQTTService;

@Log4j2
public class MQTTExplorerConsolePlugin implements ConsolePluginTree {

  @Getter
  private final EntityContext entityContext;
  private final MQTTService mqttService;

  public MQTTExplorerConsolePlugin(EntityContext entityContext, MQTTService mqttService) {
    this.entityContext = entityContext;
    this.mqttService = mqttService;
  }

  @Override
  public String getParentTab() {
    return "MQTT";
  }

  @Override
  public ActionResponseModel executeAction(String entityID, JSONObject metadata, JSONObject params) {
    if ("history".equals(metadata.optString("type"))) {
      return ActionResponseModel.showJson("History", new ArrayList<>(mqttService.getStorage().findAllBy("topic", entityID)));
    }
    return ActionResponseModel.showWarn("Unable to handle command: " + entityID);
  }

  @Override
  public List<TreeConfiguration> getValue() {
    return mqttService.getValue();
  }

  @Override
  public RenderType getRenderType() {
    return RenderType.tree;
  }

  @Override
  public boolean isEnabled() {
    return entityContext.isAdminUserOrNone();
  }

  @Override
  public String getName() {
    return "MQTT";
  }

  @Override
  public Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> getHeaderActions() {
    Map<String, Class<? extends ConsoleHeaderSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
    headerActions.put("publish", ConsoleMQTTPublishButtonSetting.class);
    headerActions.put("remove", RemoveNodeConsoleHeaderButtonSetting.class);
    return headerActions;
  }

  @Override
  public boolean hasRefreshIntervalSetting() {
    return false;
  }
}