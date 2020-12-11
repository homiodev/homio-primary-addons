package org.touchhome.bundle.arduino.setting.header;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.BundleSettingPluginButton;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class ConsoleHeaderArduinoBuildSketchSetting implements BundleHeaderSettingPlugin<JSONObject>, BundleSettingPluginButton {

    @Override
    public String getIconColor() {
        return "#2F8B44";
    }

    @Override
    public String getIcon() {
        return "fas fa-check";
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public String[] fireActionsBeforeChange() {
        return new String[]{"st_ShowInlineReadOnlyConsoleHeaderSetting/true", "SAVE"};
    }
}
