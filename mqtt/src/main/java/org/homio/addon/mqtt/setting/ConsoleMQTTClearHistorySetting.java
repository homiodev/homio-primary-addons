package org.homio.addon.mqtt.setting;

import org.homio.addon.mqtt.console.MQTTExplorerConsolePlugin;
import org.homio.api.console.ConsolePlugin;
import org.homio.api.model.Icon;
import org.homio.api.setting.SettingPluginButton;
import org.homio.api.setting.console.ConsoleSettingPlugin;
import org.homio.api.ui.UI;
import org.json.JSONObject;

public class ConsoleMQTTClearHistorySetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

    @Override
    public String getConfirmMsg() {
        return "W.CONFIRM.CLEAR_HISTORY";
    }

    @Override
    public Icon getIcon() {
        return new Icon("fas fa-brush", UI.Color.RED);
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
