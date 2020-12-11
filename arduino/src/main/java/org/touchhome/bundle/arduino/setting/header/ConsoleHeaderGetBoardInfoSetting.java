package org.touchhome.bundle.arduino.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.BundleSettingPluginButton;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class ConsoleHeaderGetBoardInfoSetting implements BundleHeaderSettingPlugin<JSONObject>, BundleSettingPluginButton {

    @Override
    public String getIconColor() {
        return "#4279AE";
    }

    @Override
    public String getIcon() {
        return "fas fa-info";
    }

    @Override
    public int order() {
        return 100;
    }
}
