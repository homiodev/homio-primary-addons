package org.touchhome.bundle.arduino.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.console.ConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;

import java.util.Collections;
import java.util.List;

public class ConsoleArduinoUploadUsingProgrammerSetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

    @Override
    public String getIcon() {
        return "fas fa-upload";
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
        return consolePlugin instanceof ArduinoConsolePlugin;
    }
}
