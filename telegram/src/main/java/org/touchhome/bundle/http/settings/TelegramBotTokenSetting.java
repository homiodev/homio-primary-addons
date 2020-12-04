package org.touchhome.bundle.http.settings;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class TelegramBotTokenSetting implements BundleSettingPluginText {

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
