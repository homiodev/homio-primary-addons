package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class ZigbeeResetNetworkButtonSetting implements BundleSettingPlugin<Void> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Button;
    }

    @Override
    public int order() {
        return 1500;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}