package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginInteger;

public class ZigBeePanIdSetting implements SettingPluginInteger {

    @Override
    public Integer getMin() {
        return 1;
    }

    @Override
    public Integer getMax() {
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
