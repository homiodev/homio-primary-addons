package org.touchhome.bundle.arduino.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.BundleSettingPluginButton;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class ArduinoGetBoardInfoSetting implements BundleHeaderSettingPlugin<JSONObject>, BundleSettingPluginButton {

    @Override
    public String getConfirmMsg() {
        return null;
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
