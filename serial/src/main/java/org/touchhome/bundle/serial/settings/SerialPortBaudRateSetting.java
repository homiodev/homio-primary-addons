package org.touchhome.bundle.serial.settings;

import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxInteger;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;

public class SerialPortBaudRateSetting implements BundleHeaderSettingPlugin<Integer>, BundleSettingPluginSelectBoxInteger {

    @Override
    public String getIcon() {
        return "fas fa-tachometer-alt";
    }

    @Override
    public Integer getMaxWidth() {
        return 85;
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.SelectBox;
    }

    @Override
    public int defaultValue() {
        return 9600;
    }

    @Override
    public boolean transientState() {
        return false;
    }

    @Override
    public int[] availableValues() {
        return new int[]{300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 31250, 38400, 57600, 115200};
    }
}
