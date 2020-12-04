package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginButton;

public class ZigbeeDiscoveryButtonSetting implements BundleSettingPluginButton {

    @Override
    public String getIcon() {
        return "fas fa-search-location";
    }

    @Override
    public int order() {
        return 100;
    }
}
