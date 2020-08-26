package org.touchhome.bundle.raspberry.settings;

import org.touchhome.bundle.api.setting.BundleSettingPluginSlider;

public class RaspberryOneWireIntervalSetting implements BundleSettingPluginSlider {

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return 120;
    }

    @Override
    public String getHeader() {
        return "S";
    }

    @Override
    public int defaultValue() {
        return 30;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean isReverted() {
        return true;
    }
}
