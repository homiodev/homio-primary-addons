package org.homio.addon.z2m.setting;

import org.homio.api.EntityContext;
import org.homio.api.entity.BaseEntity;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.setting.SettingPluginToggle;

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
