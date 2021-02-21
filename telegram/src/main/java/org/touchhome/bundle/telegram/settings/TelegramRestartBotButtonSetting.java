package org.touchhome.bundle.telegram.settings;

import org.touchhome.bundle.api.setting.SettingPluginButton;

public class TelegramRestartBotButtonSetting implements SettingPluginButton {

    @Override
    public int order() {
        return 300;
    }

    @Override
    public String getIcon() {
        return "fas fa-power-off";
    }
}
