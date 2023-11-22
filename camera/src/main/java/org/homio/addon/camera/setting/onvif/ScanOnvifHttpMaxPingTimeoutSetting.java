package org.homio.addon.camera.setting.onvif;

import org.homio.api.setting.SettingPluginSlider;

public class ScanOnvifHttpMaxPingTimeoutSetting implements SettingPluginSlider {

    @Override
    public int order() {
        return 330;
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
        return "scan_onvif_http";
    }
}