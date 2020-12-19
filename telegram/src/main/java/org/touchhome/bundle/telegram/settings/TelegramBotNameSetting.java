package org.touchhome.bundle.telegram.settings;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class TelegramBotNameSetting implements SettingPluginText {

    @Override
    public int order() {
        return 100;
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
