package org.touchhome.bundle.firmata.setting;

import org.touchhome.bundle.api.hardware.network.NetworkDescription;
import org.touchhome.bundle.api.setting.SettingPlugin;
import org.touchhome.bundle.api.setting.SettingPluginTextSet;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.util.Set;

public class FirmataScanPortRangeSetting implements SettingPluginTextSet, SettingPlugin<Set<String>> {

    @Override
    public int order() {
        return 12;
    }

    @Override
    public String getPattern() {
        return NetworkDescription.IP_RANGE_PATTERN;
    }

    @Override
    public String[] defaultValue() {
        return new String[]{TouchHomeUtils.MACHINE_IP_ADDRESS.substring(0, TouchHomeUtils.MACHINE_IP_ADDRESS.lastIndexOf(".")) + "-255"};
    }
}
