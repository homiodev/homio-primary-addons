package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.SettingPluginInfo;

public class CloudServerConnectionMessageSetting implements SettingPluginInfo {

    @Override
    public int order() {
        return 30;
    }
}
