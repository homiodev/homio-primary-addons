package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class ZigBeeNetworkKeySetting implements BundleSettingPluginText {

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
