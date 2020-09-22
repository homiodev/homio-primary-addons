package org.touchhome.bundle.http.settings;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class TelegramRestartBotButtonSetting implements BundleSettingPlugin<Void> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Button;
    }

    @Override
    public int order() {
        return 300;
    }
}
