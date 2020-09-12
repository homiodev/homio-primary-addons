package org.touchhome.bundle.zigbee;

import com.zsmartsystems.zigbee.IeeeAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.BundleSettingPluginStatus;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.setting.ZigbeeCoordinatorHandlerSetting;
import org.touchhome.bundle.zigbee.setting.ZigbeeStatusSetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@RequiredArgsConstructor
public class ZigBeeBundleEntrypoint implements BundleEntrypoint {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final EntityContext entityContext;
    private final ZigBeeChannelConverterFactory zigBeeChannelConverterFactory;
    private final ZigBeeDeviceUpdateValueListener deviceUpdateListener;

    private final ZigBeeIsAliveTracker zigBeeIsAliveTracker = new ZigBeeIsAliveTracker();

    private ZigBeeDiscoveryService zigBeeDiscoveryService;
    @Getter
    private ZigBeeCoordinatorHandler coordinatorHandler;

    @Override
    public void init() {
        this.coordinatorHandler = entityContext.getSettingValue(ZigbeeCoordinatorHandlerSetting.class);
        this.zigBeeDiscoveryService = new ZigBeeDiscoveryService(
                entityContext, coordinatorHandler,
                zigBeeIsAliveTracker,
                zigBeeChannelConverterFactory,
                scheduler,
                deviceUpdateListener);

        this.entityContext.listenSettingValue(ZigbeeStatusSetting.class, "zb-fetch-devices", status -> {
            if (status.isOnline()) {
                for (ZigBeeDeviceEntity zigbeeDeviceEntity : entityContext.findAll(ZigBeeDeviceEntity.class)) {
                    zigBeeDiscoveryService.addZigBeeDevice(new IeeeAddress(zigbeeDeviceEntity.getIeeeAddress()));
                }
            }
        });

        coordinatorHandler.initialize();
    }

    @Override
    public void destroy() {
        this.coordinatorHandler.dispose();
    }

    @Override
    public String getBundleId() {
        return "zigbee";
    }

    @Override
    public int order() {
        return 600;
    }

    @Override
    public Class<? extends BundleSettingPluginStatus> getBundleStatusSetting() {
        return ZigbeeStatusSetting.class;
    }
}
