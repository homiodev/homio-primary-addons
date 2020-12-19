package org.touchhome.bundle.arduino.setting.header;

import org.touchhome.bundle.api.setting.header.dynamic.DynamicHeaderContainerSettingPlugin;

public class ConsoleHeaderGetBoardsDynamicSetting implements DynamicHeaderContainerSettingPlugin {

    @Override
    public String getIcon() {
        return "fas fa-cubes";
    }

    @Override
    public String getIconColor() {
        return "#1C9CB0";
    }
}
