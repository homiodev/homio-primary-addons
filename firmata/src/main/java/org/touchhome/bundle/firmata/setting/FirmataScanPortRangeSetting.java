package org.touchhome.bundle.firmata.setting;

import org.touchhome.bundle.api.hardware.network.NetworkDescription;
import org.touchhome.bundle.api.setting.SettingPlugin;
import org.touchhome.bundle.api.setting.SettingPluginTextSet;

import java.util.Set;

import static org.touchhome.bundle.api.util.TouchHomeUtils.MACHINE_IP_ADDRESS;

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
        return new String[]{MACHINE_IP_ADDRESS.substring(0, MACHINE_IP_ADDRESS.lastIndexOf(".") + 1) + "0-255"};
    }
}
