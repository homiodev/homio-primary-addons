package org.touchhome.bundle.zigbee.setting.advanced;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.BundleSettingPluginSelectBoxInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ZigbeeChannelIdSetting implements BundleSettingPluginSelectBoxInteger {

    @Override
    public int defaultValue() {
        return 0;
    }

    @Override
    public List<Option> loadAvailableValues(EntityContext entityContext) {
        List<Option> options = new ArrayList<>();
        options.add(Option.of("0", "AUTO"));
        options.addAll(IntStream.range(11, 25)
                .mapToObj(value -> Option.of(String.valueOf(value), "Channel " + value)).collect(Collectors.toList()));
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
