package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class ZigBeeNetworkKeySetting implements SettingPluginText {

    @Override
    public String getDefaultValue() {
        return "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";
    }

    @Override
    public int order() {
        return 1300;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
