package org.touchhome.bundle.raspberry;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.raspberry.model.RaspberryDeviceEntity;

import static org.touchhome.bundle.raspberry.model.RaspberryDeviceEntity.DEFAULT_DEVICE_ENTITY_ID;

@Log4j2
@Component
@RequiredArgsConstructor
public class RaspberryEntryPoint implements BundleEntryPoint {

    private final EntityContext entityContext;
    private final RaspberryGPIOService raspberryGPIOService;

    public void init() {
        if (entityContext.getEntity(DEFAULT_DEVICE_ENTITY_ID) == null) {
            entityContext.save(new RaspberryDeviceEntity().computeEntityID(() -> DEFAULT_DEVICE_ENTITY_ID));
        }
        raspberryGPIOService.init();
    }

    @Override
    public String getBundleId() {
        return "raspberry";
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
