package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginOptionsInteger;

public class ZigBeePortBaudSetting implements SettingPluginOptionsInteger {

    @Override
    public int defaultValue() {
        return 115200;
    }

    @Override
    public int[] availableValues() {
        return new int[]{38400, 57600, 115200};
    }

    @Override
    public int order() {
        return 750;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
