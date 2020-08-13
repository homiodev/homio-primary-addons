package org.touchhome.bundle.raspberry.settings;

import org.json.JSONObject;
import org.touchhome.bundle.api.BundleSettingPlugin;
import org.touchhome.bundle.api.EntityContext;

public class RaspberryOneWireIntervalSetting implements BundleSettingPlugin<Integer> {

    @Override
    public String getDefaultValue() {
        return "30";
    }

    @Override
    public JSONObject getParameters(EntityContext entityContext, String value) {
        return new JSONObject().put("min", 10).put("max", 120).put("step", 1).put("header", "S");
    }

    @Override
    public BundleSettingPlugin.SettingType getSettingType() {
        return BundleSettingPlugin.SettingType.Slider;
    }

    @Override
    public int order() {
        return 100;
    }
}
