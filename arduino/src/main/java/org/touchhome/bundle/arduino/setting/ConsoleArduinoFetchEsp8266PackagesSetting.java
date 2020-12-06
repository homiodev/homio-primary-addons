package org.touchhome.bundle.arduino.setting;

import cc.arduino.contributions.packages.ContributionsIndexer;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.BundleSettingPluginButton;
import org.touchhome.bundle.api.setting.console.BundleConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;

public class ConsoleArduinoFetchEsp8266PackagesSetting implements BundleConsoleSettingPlugin<JSONObject>, BundleSettingPluginButton {

    public static final String URL = "http://arduino.esp8266.com/stable/package_esp8266com_index.json";

    @Override
    public boolean isDisabled(EntityContext entityContext) {
        return entityContext.getBean(ContributionsIndexer.class).getInstalled("esp8266", "esp8266") != null;
    }

    @Override
    public String getIcon() {
        return "fas fa-download";
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.Button;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
        return consolePlugin instanceof ArduinoConsolePlugin;
    }
}
