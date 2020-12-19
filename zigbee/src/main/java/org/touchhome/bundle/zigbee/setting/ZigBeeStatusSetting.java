package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginStatus;

public class ZigBeeStatusSetting implements SettingPluginStatus {

    @Override
    public int order() {
        return 400;
    }
}
