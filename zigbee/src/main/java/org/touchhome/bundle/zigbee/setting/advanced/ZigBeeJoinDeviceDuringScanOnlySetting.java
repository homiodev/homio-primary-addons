package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.setting.BundleSettingPluginBoolean;

public class ZigBeeJoinDeviceDuringScanOnlySetting implements BundleSettingPluginBoolean {

    @Override
    public int order() {
        return 600;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
