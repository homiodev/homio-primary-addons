package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class ZigBeeLinkKeySetting implements SettingPluginText {

    @Override
    public int order() {
        return 900;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
