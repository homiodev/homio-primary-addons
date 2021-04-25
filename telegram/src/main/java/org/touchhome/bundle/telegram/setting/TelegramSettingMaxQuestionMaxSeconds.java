package org.touchhome.bundle.telegram.setting;

import org.touchhome.bundle.api.setting.SettingPluginSlider;

public class TelegramSettingMaxQuestionMaxSeconds implements SettingPluginSlider {

    @Override
    public Integer getMax() {
        return 360;
    }

    @Override
    public Integer getMin() {
        return 5;
    }

    @Override
    public int defaultValue() {
        return 60;
    }

    @Override
    public int order() {
        return 100;
    }
}
