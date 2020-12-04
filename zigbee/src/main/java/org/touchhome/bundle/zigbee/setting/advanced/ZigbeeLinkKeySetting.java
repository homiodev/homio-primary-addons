package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginText;

public class ZigbeeLinkKeySetting implements BundleSettingPluginText {

    @Override
    public int order() {
        return 900;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
