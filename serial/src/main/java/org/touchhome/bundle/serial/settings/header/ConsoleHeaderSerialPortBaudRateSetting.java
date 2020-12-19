package org.touchhome.bundle.serial.settings.header;

import org.touchhome.bundle.api.setting.SettingPluginOptionsInteger;
import org.touchhome.bundle.api.setting.console.header.ConsoleHeaderSettingPlugin;

public class ConsoleHeaderSerialPortBaudRateSetting implements ConsoleHeaderSettingPlugin<Integer>, SettingPluginOptionsInteger {

    @Override
    public String getIcon() {
        return "fas fa-tachometer-alt";
    }

    @Override
    public Integer getMaxWidth() {
        return 85;
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
    public int[] availableValues() {
        return new int[]{300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 31250, 38400, 57600, 115200};
    }
}
