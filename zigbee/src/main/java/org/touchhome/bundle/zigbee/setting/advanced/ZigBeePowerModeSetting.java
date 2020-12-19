package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsInteger;

import java.util.Collection;

public class ZigBeePowerModeSetting implements SettingPluginOptionsInteger {

    @Override
    public int defaultValue() {
        return 1;
    }

    @Override
    public Collection<OptionModel> getOptions(EntityContext entityContext) {
        return OptionModel.list(OptionModel.of("0", "Normal"), OptionModel.of("1", "Boost"));
    }

    @Override
    public int[] availableValues() {
        return new int[0];
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
