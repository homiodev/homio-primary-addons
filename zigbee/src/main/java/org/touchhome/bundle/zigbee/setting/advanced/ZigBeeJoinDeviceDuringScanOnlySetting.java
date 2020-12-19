package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.SettingPluginBoolean;

public class ZigBeeJoinDeviceDuringScanOnlySetting implements SettingPluginBoolean {

    @Override
    public int order() {
        return 600;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
