package org.touchhome.bundle.z2m;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.z2m.model.Z2MDeviceEntity;
import org.touchhome.bundle.z2m.setting.ZigBeeEntityCompactModeSetting;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZigBeeEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;

    @Override
    public void init() {
        entityContext.ui().registerConsolePluginName("zigbee");
        entityContext.setting().listenValue(ZigBeeEntityCompactModeSetting.class, "zigbee-compact-mode",
            (value) -> entityContext.ui().updateItems(Z2MDeviceEntity.class));
        entityContext.var().createGroup("z2m", "ZigBee2MQTT", true, "fab fa-laravel", "#ED3A3A");
    }

    @Override
    public int order() {
        return 600;
    }
}
