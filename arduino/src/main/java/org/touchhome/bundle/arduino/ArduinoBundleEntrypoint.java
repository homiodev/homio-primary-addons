package org.touchhome.bundle.arduino;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.setting.ArduinoUsbPortSetting;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoBundleEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;

    public void init() {
        startArduinoCommunicators();
    }

    private void startArduinoCommunicators() {
        // start arduino communication providers if specified
        restartArduinoProviders();

        // listen arduino changes and start communicators is set
        this.entityContext.addEntityUpdateListener(ArduinoDeviceEntity.class, this::restartArduinoCommunicator);

        this.entityContext.listenSettingValue(ArduinoUsbPortSetting.class, "arduino-listen-for-reload", port -> {
            if (port != null) {
                restartArduinoProviders();
            }
        });
    }

    private void restartArduinoCommunicator(ArduinoDeviceEntity arduinoDeviceEntity) {
        if (arduinoDeviceEntity.getCommunicationProvider() != null) {
            boolean restarted = arduinoDeviceEntity.getCommunicationProvider().restart();
            arduinoDeviceEntity.setCommunicatorStatus(restarted ? Status.ONLINE : Status.OFFLINE);
        }
    }

    private void restartArduinoProviders() {
        this.entityContext.findAll(ArduinoDeviceEntity.class).forEach(this::restartArduinoCommunicator);
    }

    @Override
    public String getBundleId() {
        return "arduino";
    }

    @Override
    public int order() {
        return 500;
    }
}
