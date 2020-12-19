package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginButton;

public class ZigBeeResetNetworkButtonSetting implements SettingPluginButton {

    @Override
    public int order() {
        return 1500;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
