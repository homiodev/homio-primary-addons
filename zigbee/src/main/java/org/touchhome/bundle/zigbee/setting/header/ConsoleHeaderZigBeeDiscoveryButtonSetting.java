package org.touchhome.bundle.zigbee.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.BundleSettingPluginButton;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class ConsoleHeaderZigBeeDiscoveryButtonSetting implements BundleSettingPluginButton, BundleHeaderSettingPlugin<JSONObject> {

    @Override
    public String getIcon() {
        return "fas fa-search-location";
    }

    @Override
    public int order() {
        return 100;
    }
}
