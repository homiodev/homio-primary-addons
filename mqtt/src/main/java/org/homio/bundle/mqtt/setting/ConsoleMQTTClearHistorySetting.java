package org.homio.bundle.mqtt.setting;

import org.homio.bundle.api.console.ConsolePlugin;
import org.homio.bundle.api.setting.SettingPluginButton;
import org.homio.bundle.api.setting.console.ConsoleSettingPlugin;
import org.homio.bundle.api.ui.UI;
import org.homio.bundle.mqtt.console.MQTTExplorerConsolePlugin;
import org.json.JSONObject;

public class ConsoleMQTTClearHistorySetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

  @Override
  public String getConfirmMsg() {
    return "mqtt.confirm_clear_history";
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
