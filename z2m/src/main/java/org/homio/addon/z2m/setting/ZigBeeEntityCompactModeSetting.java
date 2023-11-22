package org.homio.addon.z2m.setting;

import org.homio.api.Context;
import org.homio.api.entity.BaseEntity;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.Icon;
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
    public Icon getIcon() {
        return new Icon("fas fa-minimize");
    }

    @Override
    public Icon getToggleIcon() {
        return new Icon("fas fa-maximize");
    }

    @Override
    public boolean isVisible(Context context) {
        return false;
    }
}
