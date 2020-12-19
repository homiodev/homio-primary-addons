package org.touchhome.bundle.serial.settings.header;

import org.touchhome.bundle.api.setting.SettingPluginToggle;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;

public class ConsoleHeaderSerialOpenPortSetting implements ConsoleHeaderSettingPlugin<Boolean>, SettingPluginToggle {

    @Override
    public String getIcon() {
        return "fas fa-door-open";
    }

    @Override
    public String getToggleIcon() {
        return "fas fa-door-closed";
    }
}
