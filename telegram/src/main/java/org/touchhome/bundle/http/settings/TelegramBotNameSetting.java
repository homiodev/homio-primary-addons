package org.touchhome.bundle.http.settings;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class TelegramBotNameSetting implements BundleSettingPluginText {

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
