package org.touchhome.bundle.http.settings;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class TelegramBotTokenSetting implements BundleSettingPlugin<String> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Text;
    }

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
