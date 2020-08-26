package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginInteger;

public class ZigbeePanIdSetting implements BundleSettingPluginInteger {

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return 65535;
    }

    @Override
    public int defaultValue() {
        return 65535;
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
