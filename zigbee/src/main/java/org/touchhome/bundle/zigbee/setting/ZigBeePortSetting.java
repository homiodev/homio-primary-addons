package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginPort;

public class ZigBeePortSetting implements BundleSettingPluginPort {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
