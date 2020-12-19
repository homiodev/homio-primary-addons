package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginOptionsBean;
import org.touchhome.bundle.zigbee.ZigBeeCoordinatorHandler;
import org.touchhome.bundle.zigbee.handler.CC2531Handler;

public class ZigBeeCoordinatorHandlerSetting implements SettingPluginOptionsBean<ZigBeeCoordinatorHandler> {

    @Override
    public Class<ZigBeeCoordinatorHandler> getType() {
        return ZigBeeCoordinatorHandler.class;
    }

    @Override
    public String getDefaultValue() {
        return CC2531Handler.class.getSimpleName();
    }

    @Override
    public int order() {
        return 400;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
