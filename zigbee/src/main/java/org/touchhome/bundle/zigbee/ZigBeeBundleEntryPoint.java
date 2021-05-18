package org.touchhome.bundle.zigbee;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.transport.TransportConfig;
import com.zsmartsystems.zigbee.transport.TransportConfigOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeChannelConverterFactory;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.setting.ZigBeeCoordinatorHandlerSetting;
import org.touchhome.bundle.zigbee.setting.ZigBeeNetworkIdSetting;
import org.touchhome.bundle.zigbee.setting.ZigBeePortSetting;
import org.touchhome.bundle.zigbee.setting.ZigBeeStatusSetting;
import org.touchhome.bundle.zigbee.setting.advanced.*;
import org.touchhome.bundle.zigbee.setting.header.ConsoleHeaderZigBeeDiscoveryButtonSetting;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZigBeeBundleEntryPoint implements BundleEntryPoint {

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
        this.zigBeeDiscoveryService = new ZigBeeDiscoveryService(
                entityContext, () -> this.coordinatorHandler,
                zigBeeIsAliveTracker,
                zigBeeChannelConverterFactory,
                scheduler,
                deviceUpdateListener);

        this.coordinatorHandler = entityContext.setting().getValue(ZigBeeCoordinatorHandlerSetting.class);
        this.coordinatorHandler.addNetworkNodeListener(zigBeeDiscoveryService);

        entityContext.setting().listenValue(ZigBeeCoordinatorHandlerSetting.class, "zb-coordinator-changed", coordinatorHandler -> {
            this.coordinatorHandler = coordinatorHandler;
            this.coordinatorHandler.addNetworkNodeListener(zigBeeDiscoveryService);
            this.reInitialize();
        });

        this.entityContext.setting().listenValue(ConsoleHeaderZigBeeDiscoveryButtonSetting.class, "zb-start-scan", () ->
                zigBeeDiscoveryService.startScan());

        entityContext.setting().listenValue(ZigBeePortBaudSetting.class, "zb-port-baud-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeNetworkIdSetting.class, "zb-network-id-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeLinkKeySetting.class, "zb-link-key-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeExtendedPanIdSetting.class, "zb-extended-pan-id-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeNetworkKeySetting.class, "zb-network-key-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeePanIdSetting.class, "zb-pan-id-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeChannelIdSetting.class, "zb-channel-id-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeePowerModeSetting.class, "zb-power-mode-changed", this::reInitialize);
        entityContext.setting().listenValue(ZigBeeResetNetworkButtonSetting.class, "zb-reset-network-btn", () -> {
            entityContext.setting().setValue(ZigBeeNetworkIdSetting.class, null);
            this.reInitialize();
        });

        entityContext.setting().listenValue(ZigBeeTrustCenterModeSetting.class, "zb-trust-center-changed", linkMode -> {
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.addOption(TransportConfigOption.TRUST_CENTRE_JOIN_MODE, linkMode);
            coordinatorHandler.getZigBeeTransport().updateTransportConfig(transportConfig);
        });

        entityContext.setting().listenValue(ZigBeeTxPowerSetting.class, "zb-tx-power-changed", txPower -> {
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.addOption(TransportConfigOption.RADIO_TX_POWER, txPower);
            coordinatorHandler.getZigBeeTransport().updateTransportConfig(transportConfig);
        });

        this.entityContext.setting().listenValue(ZigBeeStatusSetting.class, "zb-status-changed", status -> {
            if (status.isOnline()) {
                // init devices
                for (ZigBeeDeviceEntity zigbeeDeviceEntity : entityContext.findAll(ZigBeeDeviceEntity.class)) {
                    zigBeeDiscoveryService.addZigBeeDevice(new IeeeAddress(zigbeeDeviceEntity.getIeeeAddress()));
                }
            }

            for (ZigBeeDevice zigBeeDevice : coordinatorHandler.getZigBeeDevices().values()) {
                zigBeeDevice.tryInitializeDevice(entityContext.setting().getValue(ZigBeeStatusSetting.class).getStatus());
            }
        });

        // not tested
        entityContext.setting().listenValue(ZigBeeInstallCodeSetting.class, "zb-install-code-changed", code ->
                coordinatorHandler.addInstallCode(code));

        reInitialize();
    }

    private void reInitialize() {
        coordinatorHandler.dispose();

        // check if zigbee port selected
        if (entityContext.setting().getValue(ZigBeePortSetting.class) == null) {
            log.error("No zigbee coordinator port selected");
            entityContext.setting().setValue(ZigBeeStatusSetting.class,
                    new SettingPluginStatus.BundleStatusInfo(Status.OFFLINE, "No zigbee coordinator port selected"));
            return;
        }

        coordinatorHandler.initialize();
    }

    @Override
    public void destroy() {
        this.coordinatorHandler.dispose();
    }

    @Override
    public int order() {
        return 600;
    }

    @Override
    public Class<? extends SettingPluginStatus> getBundleStatusSetting() {
        return ZigBeeStatusSetting.class;
    }
}
