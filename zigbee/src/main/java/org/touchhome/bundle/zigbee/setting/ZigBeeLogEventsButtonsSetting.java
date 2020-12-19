package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginBoolean;

public class ZigBeeLogEventsButtonsSetting implements SettingPluginBoolean {

    @Override
    public String getDefaultValue() {
        return Boolean.TRUE.toString();
    }


    @Override
    public int order() {
        return 300;
    }
}
