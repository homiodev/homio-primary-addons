package org.touchhome.bundle.serial.settings.header;

import org.touchhome.bundle.api.setting.SettingPluginToggle;
import org.touchhome.bundle.api.setting.header.HeaderSettingPlugin;

public class ConsoleHeaderSerialOpenPortSetting implements HeaderSettingPlugin<Boolean>, SettingPluginToggle {

    @Override
    public String getIcon() {
        return "fas fa-door-open";
    }

    @Override
    public String getToggleIcon() {
        return "fas fa-door-closed";
    }
}
