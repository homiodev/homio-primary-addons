package org.touchhome.bundle.zigbee.setting.advanced;

import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ZigBeeMeshUpdatePeriodSetting implements SettingPluginOptionsInteger {

    @Override
    public int defaultValue() {
        return 86400;
    }

    @Override
    public Collection<OptionModel> getOptions(EntityContext entityContext, JSONObject params) {
        return new ArrayList<>(Arrays.asList(
                OptionModel.of("0", "NEVER"),
                OptionModel.of("300", "5 Minutes"),
                OptionModel.of("1800", "30 Minutes"),
                OptionModel.of("3600", "1 Hour"),
                OptionModel.of("21600", "6 Minutes"),
                OptionModel.of("86400", "1 Day"),
                OptionModel.of("604800", "1 Week")));
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
