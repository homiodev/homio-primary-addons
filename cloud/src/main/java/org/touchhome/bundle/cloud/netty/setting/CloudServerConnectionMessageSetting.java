package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginInfo;

public class CloudServerConnectionMessageSetting implements BundleSettingPluginInfo {

    @Override
    public int order() {
        return 30;
    }
}
