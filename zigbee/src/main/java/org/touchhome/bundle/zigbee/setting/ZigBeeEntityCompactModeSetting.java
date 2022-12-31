package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.setting.SettingPluginToggle;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceBaseEntity;

public class ZigBeeEntityCompactModeSetting implements SettingPluginToggle {

    @Override
    public Class<? extends BaseEntity> availableForEntity() {
        return ZigBeeDeviceBaseEntity.class;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String getIcon() {
        return "fas fa-minimize";
    }

    @Override
    public String getToggleIcon() {
        return "fas fa-maximize";
    }

    @Override
    public boolean isVisible(EntityContext entityContext) {
        return false;
    }
}
