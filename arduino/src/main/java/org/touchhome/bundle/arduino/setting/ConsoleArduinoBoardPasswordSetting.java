package org.touchhome.bundle.arduino.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.console.ConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;

import java.util.Collections;
import java.util.List;

public class ConsoleArduinoBoardPasswordSetting implements SettingPluginButton, ConsoleSettingPlugin<JSONObject> {

    @Override
    public String getIcon() {
        return "fas fa-unlock-alt";
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
        return consolePlugin instanceof ArduinoConsolePlugin;
    }

    @Override
    public List<InputParameter> getInputParameters(EntityContext entityContext, String value) {
        return Collections.singletonList(new InputParameter("PASSWORD", InputParameterType.password, null, "")
                .setDescription("arduino.PASSWORD_DESCRIPTION"));
    }
}
