package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginOptionsPort;

public class ZigBeePortSetting implements SettingPluginOptionsPort {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
