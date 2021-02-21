package org.touchhome.bundle.camera.setting.rtsp;

import org.touchhome.bundle.api.setting.SettingPluginSlider;

public class ScanRtspIpAddressMaxPingTimeoutSetting implements SettingPluginSlider {

    @Override
    public int order() {
        return 120;
    }

    @Override
    public Integer getMin() {
        return 1;
    }

    @Override
    public Integer getMax() {
        return 5000;
    }

    @Override
    public int defaultValue() {
        return 500;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public String group() {
        return "scan_rtsp";
    }
}