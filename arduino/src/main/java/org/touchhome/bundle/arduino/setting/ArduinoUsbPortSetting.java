package org.touchhome.bundle.arduino.setting;

import org.touchhome.bundle.api.setting.BundleSettingPluginPort;

public class ArduinoUsbPortSetting implements BundleSettingPluginPort {

    @Override
    public int order() {
        return 100;
    }
}
