package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginButton;

public class CloudServerRestartSetting implements BundleSettingPluginButton {

    @Override
    public int order() {
        return 10;
    }
}
