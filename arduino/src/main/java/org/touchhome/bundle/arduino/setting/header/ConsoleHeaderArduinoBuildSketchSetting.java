package org.touchhome.bundle.arduino.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.header.HeaderSettingPlugin;

public class ConsoleHeaderArduinoBuildSketchSetting implements HeaderSettingPlugin<JSONObject>, SettingPluginButton {

    @Override
    public String getConfirmMsg() {
        return null;
    }

    @Override
    public String getIconColor() {
        return "#2F8B44";
    }

    @Override
    public String getIcon() {
        return "fas fa-check";
    }

    @Override
    public String[] fireActionsBeforeChange() {
        return new String[]{"st_ShowInlineReadOnlyConsoleHeaderSetting/true", "SAVE"};
    }
}
