package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxInteger;

import java.util.List;

public class ZigbeePowerModeSetting implements BundleSettingPluginSelectBoxInteger {

    @Override
    public int defaultValue() {
        return 1;
    }

    @Override
    public List<Option> loadAvailableValues(EntityContext entityContext) {
        return Option.list(Option.of("0", "Normal"), Option.of("1", "Boost"));
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
