package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginBoolean;

public class ZigbeeLogEventsButtonsSetting implements BundleSettingPluginBoolean {

    @Override
    public String getDefaultValue() {
        return Boolean.TRUE.toString();
    }


    @Override
    public int order() {
        return 300;
    }
}
