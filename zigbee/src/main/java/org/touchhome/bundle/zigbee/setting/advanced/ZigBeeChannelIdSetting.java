package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsInteger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ZigBeeChannelIdSetting implements SettingPluginOptionsInteger {

    @Override
    public int defaultValue() {
        return 0;
    }

    @Override
    public Collection<OptionModel> getOptions(EntityContext entityContext) {
        List<OptionModel> options = new ArrayList<>();
        options.add(OptionModel.of("0", "AUTO"));
        options.addAll(IntStream.range(11, 25)
                .mapToObj(value -> OptionModel.of(String.valueOf(value), "Channel " + value)).collect(Collectors.toList()));
        return options;
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
