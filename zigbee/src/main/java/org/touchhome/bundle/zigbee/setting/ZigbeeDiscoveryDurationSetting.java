package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginSlider;

public class ZigbeeDiscoveryDurationSetting implements BundleSettingPluginSlider {

    @Override
    public int getMin() {
        return 60;
    }

    @Override
    public int getMax() {
        return 254;
    }

    @Override
    public int defaultValue() {
        return 254;
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public boolean isReverted() {
        return true;
    }
}
