package org.touchhome.bundle.serial.settings;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.BundleSettingPluginPort;

public class SerialPortSetting implements BundleSettingPluginPort {

    @Override
    public String getIcon() {
        return "fas fa-project-diagram";
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean transientState() {
        return true;
    }

    @Override
    public boolean isVisible(EntityContext entityContext) {
        return false;
    }

    @Override
    public boolean withEmpty() {
        return false;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
