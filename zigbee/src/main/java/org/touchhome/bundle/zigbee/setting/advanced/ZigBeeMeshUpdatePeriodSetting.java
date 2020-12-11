package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZigBeeMeshUpdatePeriodSetting implements BundleSettingPluginSelectBoxInteger {

    @Override
    public int defaultValue() {
        return 86400;
    }

    @Override
    public List<Option> loadAvailableValues(EntityContext entityContext) {
        return new ArrayList<>(Arrays.asList(
                Option.of("0", "NEVER"),
                Option.of("300", "5 Minutes"),
                Option.of("1800", "30 Minutes"),
                Option.of("3600", "1 Hour"),
                Option.of("21600", "6 Minutes"),
                Option.of("86400", "1 Day"),
                Option.of("604800", "1 Week")));
    }

    @Override
    public int[] availableValues() {
        return new int[0];
    }

    @Override
    public int order() {
        return 1100;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public boolean isReverted() {
        return true;
    }
}
