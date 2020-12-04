package org.touchhome.bundle.serial.settings;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.BundleSettingPlugin;
import org.touchhome.bundle.api.setting.BundleSettingPluginToggle;

public class SerialOpenPortSetting implements BundleSettingPluginToggle {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public String getIcon() {
        return "fas fa-door-open";
    }

    @Override
    public String getToggleIcon() {
        return "fas fa-door-closed";
    }

    @Override
    public boolean transientState() {
        return true;
    }

    @Override
    public boolean isVisible(EntityContext entityContext) {
        return false;
    }
}
