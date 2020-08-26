package org.touchhome.bundle.zigbee.setting;

import org.json.JSONObject;
import org.touchhome.bundle.api.setting.BundleSettingPlugin;
import org.touchhome.bundle.api.EntityContext;

public class ZigbeeDiscoveryDurationSetting implements BundleSettingPlugin<Integer> {

    @Override
    public String getDefaultValue() {
        return "254";
    }

    @Override
    public JSONObject getParameters(EntityContext entityContext, String value) {
        return new JSONObject().put("min", 60).put("max", 254).put("step", 1);
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.Slider;
    }

    @Override
    public int order() {
        return 200;
    }
}
