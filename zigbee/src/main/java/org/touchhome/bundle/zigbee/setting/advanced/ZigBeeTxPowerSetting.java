package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginSlider;

/**
 * <option value="8">High</option>
 * <option value="0">Normal</option>
 */
public class ZigBeeTxPowerSetting implements BundleSettingPluginSlider {

    @Override
    public int order() {
        return 1400;
    }

    @Override
    public Integer getMin() {
        return 0;
    }

    @Override
    public Integer getMax() {
        return 8;
    }

    @Override
    public int defaultValue() {
        return 0;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public boolean isReverted() {
        return true;
    }
}
