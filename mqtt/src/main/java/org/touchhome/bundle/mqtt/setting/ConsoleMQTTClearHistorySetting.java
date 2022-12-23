package org.touchhome.bundle.mqtt.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.console.ConsoleSettingPlugin;
import org.touchhome.bundle.api.ui.UI;
import org.touchhome.bundle.mqtt.console.MQTTExplorerConsolePlugin;

public class ConsoleMQTTClearHistorySetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

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
