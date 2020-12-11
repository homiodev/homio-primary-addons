package org.touchhome.bundle.serial.settings.header;

import org.touchhome.bundle.api.setting.BundleSettingPluginToggle;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class ConsoleHeaderSerialOpenPortSetting implements BundleHeaderSettingPlugin<Boolean>, BundleSettingPluginToggle {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public String getIcon() {
        return "fas fa-door-open";
    }

    @Override
    public String getToggleIcon() {
        return "fas fa-door-closed";
    }
}
