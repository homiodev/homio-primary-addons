package org.homio.addon.mqtt.setting;

import org.homio.api.console.ConsolePlugin;
import org.homio.api.setting.SettingPluginButton;
import org.homio.api.setting.console.ConsoleSettingPlugin;
import org.homio.api.ui.UI;
import org.homio.addon.mqtt.console.MQTTExplorerConsolePlugin;
import org.json.JSONObject;

public class ConsoleMQTTClearHistorySetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

  @Override
  public String getConfirmMsg() {
    return "MQTT.CONFIRM_CLEAR_HISTORY";
  }

  @Override
  public String getIcon() {
    return "fas fa-brush";
  }

  @Override
  public String getIconColor() {
    return UI.Color.RED;
  }

  @Override
  public int order() {
    return 200;
  }

  @Override
  public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
    return consolePlugin instanceof MQTTExplorerConsolePlugin;
  }
}
