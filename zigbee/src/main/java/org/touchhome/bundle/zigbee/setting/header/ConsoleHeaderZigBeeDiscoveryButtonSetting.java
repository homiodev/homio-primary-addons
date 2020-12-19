package org.touchhome.bundle.zigbee.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.header.HeaderSettingPlugin;

public class ConsoleHeaderZigBeeDiscoveryButtonSetting implements HeaderSettingPlugin<JSONObject>, SettingPluginButton {

    @Override
    public String getIcon() {
        return "fas fa-search-location";
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public String getConfirmMsg() {
        return null;
    }
}
