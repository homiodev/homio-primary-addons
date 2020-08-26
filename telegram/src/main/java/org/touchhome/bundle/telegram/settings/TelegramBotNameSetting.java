package org.touchhome.bundle.telegram.settings;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class TelegramBotNameSetting implements BundleSettingPlugin<String> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Text;
    }

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
