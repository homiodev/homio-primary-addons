package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class CloudServerUrlSetting implements BundleSettingPluginText {

    @Override
    public String getDefaultValue() {
        return "https://touchhome.org";
    }

    @Override
    public int order() {
        return 40;
    }
}
