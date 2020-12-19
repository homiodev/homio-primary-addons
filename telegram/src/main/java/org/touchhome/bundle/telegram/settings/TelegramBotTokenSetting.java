package org.touchhome.bundle.telegram.settings;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class TelegramBotTokenSetting implements SettingPluginText {

    @Override
    public int order() {
        return 200;
    }

    @Override
    public boolean isSecuredValue() {
        return true;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
