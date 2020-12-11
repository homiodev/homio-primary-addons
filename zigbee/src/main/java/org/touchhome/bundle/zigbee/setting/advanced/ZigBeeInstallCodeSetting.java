package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class ZigBeeInstallCodeSetting implements BundleSettingPluginText {

    @Override
    public int order() {
        return 800;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
