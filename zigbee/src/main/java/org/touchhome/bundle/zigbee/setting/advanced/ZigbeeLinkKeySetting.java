package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPlugin;

public class ZigbeeLinkKeySetting implements BundleSettingPlugin<String> {

    @Override
    public SettingType getSettingType() {
        return SettingType.Text;
    }

    @Override
    public int order() {
        return 900;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}