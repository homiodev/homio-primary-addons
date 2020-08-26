package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginStatus;

public class ZigbeeStatusSetting implements BundleSettingPluginStatus {

    @Override
    public int order() {
        return 400;
    }
}
