package org.touchhome.bundle.arduino.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.header.HeaderSettingPlugin;

public class ConsoleHeaderGetBoardInfoSetting implements HeaderSettingPlugin<JSONObject>, SettingPluginButton {

    @Override
    public String getIconColor() {
        return "#4279AE";
    }

    @Override
    public String getIcon() {
        return "fas fa-info";
    }

    @Override
    public String getConfirmMsg() {
        return null;
    }
}
