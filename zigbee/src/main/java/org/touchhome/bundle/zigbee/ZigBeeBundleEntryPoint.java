package org.touchhome.bundle.zigbee;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZigBeeBundleEntryPoint implements BundleEntryPoint {

  private final EntityContext entityContext;

  @Override
  public void init() {
    entityContext.ui().registerConsolePluginName("ZIGBEE");
    entityContext.var().createGroup("zigbee", "ZigBee", true);

    // lister start/stop status and any changes that require restart camera handler
    entityContext.bgp().builder("zigbee-init").execute(() -> {
      for (ZigbeeCoordinatorEntity coordinator : entityContext.findAll(ZigbeeCoordinatorEntity.class)) {
        coordinator.getService().entityUpdated(coordinator);
      }
    });

        /* entityContext.setting().listenValue(ZigBeeTrustCenterModeSetting.class, "zb-trust-center-changed", linkMode -> {
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.addOption(TransportConfigOption.TRUST_CENTRE_JOIN_MODE, linkMode);
            coordinatorHandler.getZigBeeTransport().updateTransportConfig(transportConfig);
        }); */

        /* entityContext.setting().listenValue(ZigBeeTxPowerSetting.class, "zb-tx-power-changed", txPower -> {
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.addOption(TransportConfigOption.RADIO_TX_POWER, txPower);
            coordinatorHandler.getZigBeeTransport().updateTransportConfig(transportConfig);
        }); */

    // TODO!!!!!!!!!!!!!!!!!!!!
        /* this.entityContext.setting().listenValue(ZigBeeStatusSetting.class, "zb-status-changed", status -> {
            entityContext.ui().addHeaderButton("zb-status", status.isOnline() ? TouchHomeUtils.Colors.GREEN : TouchHomeUtils
            .Colors.RED,
                    status.isOnline() ? "Zigbee success running" : "Zigbee '" + status.getStatus() + "': " + status.getMessage(),
                    "fas fa-bug", false, null, null, ZigBeeDeviceEntity.class, null);

            if (status.isOnline()) {
                // init devices
                for (ZigBeeDeviceEntity zigbeeDeviceEntity : entityContext.findAll(ZigBeeDeviceEntity.class)) {
                    zigBeeDiscoveryService.addZigBeeDevice(new IeeeAddress(zigbeeDeviceEntity.getIeeeAddress()));
                }
            }

            for (ZigBeeDevice zigBeeDevice : coordinatorHandler.getZigBeeDevices().values()) {
                zigBeeDevice.tryInitializeDevice(entityContext.setting().getValue(ZigBeeStatusSetting.class).getStatus());
            }

        });*/

        /* entityContext.setting().listenValue(ZigBeeInstallCodeSetting.class, "zb-install-code-changed", code ->
                coordinatorHandler.addInstallCode(code));*/

        /* entityContext.setting().listenValue(ZigBeeMeshUpdatePeriodSetting.class, "zb-install-mesh-changed", value ->
                coordinatorHandler.meshUpdatePeriod(value));*/

    // reInitialize();
  }

  @Override
  public int order() {
    return 600;
  }
}
