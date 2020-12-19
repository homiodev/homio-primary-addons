package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.SettingPluginButton;

public class CloudServerRestartSetting implements SettingPluginButton {

    @Override
    public int order() {
        return 10;
    }
}
