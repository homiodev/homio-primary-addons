package org.touchhome.bundle.arduino.setting.header;

import org.touchhome.bundle.api.setting.header.dynamic.BundleHeaderDynamicContainerSettingPlugin;

public class ConsoleHeaderGetBoardsDynamicSetting implements BundleHeaderDynamicContainerSettingPlugin {

    @Override
    public String getIcon() {
        return "fas fa-cubes";
    }

    @Override
    public String getIconColor() {
        return "#1C9CB0";
    }

    @Override
    public int order() {
        return 100;
    }
}
