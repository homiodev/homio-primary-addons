package org.touchhome.bundle.http.settings;

import org.touchhome.bundle.api.setting.BundleSettingPluginButton;

public class TelegramRestartBotButtonSetting implements BundleSettingPluginButton {

    @Override
    public int order() {
        return 300;
    }
}
