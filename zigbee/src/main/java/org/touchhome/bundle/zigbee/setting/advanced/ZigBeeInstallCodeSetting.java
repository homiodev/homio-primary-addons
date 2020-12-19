package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class ZigBeeInstallCodeSetting implements SettingPluginText {

    @Override
    public int order() {
        return 800;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
