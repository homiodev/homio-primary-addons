package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginButton;

public class ZigBeeResetNetworkButtonSetting implements BundleSettingPluginButton {

    @Override
    public int order() {
        return 1500;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
