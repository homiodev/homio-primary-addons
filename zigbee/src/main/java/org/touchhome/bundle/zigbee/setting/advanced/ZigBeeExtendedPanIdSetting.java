package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class ZigBeeExtendedPanIdSetting implements SettingPluginText {

    @Override
    public String getDefaultValue() {
        return "0000000000000000";
    }

    @Override
    public int order() {
        return 600;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
