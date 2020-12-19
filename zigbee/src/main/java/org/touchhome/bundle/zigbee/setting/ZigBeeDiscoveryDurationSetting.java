package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginSlider;

public class ZigBeeDiscoveryDurationSetting implements SettingPluginSlider {

    @Override
    public Integer getMin() {
        return 60;
    }

    @Override
    public Integer getMax() {
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
