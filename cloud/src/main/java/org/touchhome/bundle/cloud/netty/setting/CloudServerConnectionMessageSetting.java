package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class CloudServerConnectionMessageSetting implements BundleSettingPlugin<String> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Info;
    }

    @Override
    public int order() {
        return 30;
    }
}
