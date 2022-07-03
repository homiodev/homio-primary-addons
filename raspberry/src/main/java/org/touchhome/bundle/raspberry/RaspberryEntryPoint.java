package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.raspberry.console.GpioConsolePlugin;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;

import static org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity.DEFAULT_DEVICE_ENTITY_ID;

@Log4j2
@Component
@RequiredArgsConstructor
public class RaspberryEntryPoint implements BundleEntryPoint {

    private final EntityContext entityContext;

    public void init() {
        entityContext.getBean(RaspberryGPIOService.class).init();
        entityContext.getBean(GpioConsolePlugin.class).init();
    }

    @Override
    public int order() {
        return 300;
    }

    @Override
    public BundleImageColorIndex getBundleImageColorIndex() {
        return BundleImageColorIndex.ONE;
    }
}
