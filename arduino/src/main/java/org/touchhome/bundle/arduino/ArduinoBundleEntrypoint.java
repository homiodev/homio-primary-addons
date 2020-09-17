package org.touchhome.bundle.arduino;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommunicationProvider;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;
import org.touchhome.bundle.arduino.repository.ArduinoDeviceRepository;
import org.touchhome.bundle.arduino.setting.ArduinoUsbPortSetting;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoBundleEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;
    private final ArduinoDeviceRepository arduinoDeviceRepository;

    public void init() {
        this.arduinoDeviceRepository.resetStatuses();
        startArduinoCommunicators();
    }

    private void startArduinoCommunicators() {
        // start arduino communication providers if specified
        restartArduinoProviders();

        // listen arduino changes and start communicators is set
        this.entityContext.addEntityUpdateListener(ArduinoDeviceEntity.class, this::restartArduinoCommunicator);

        this.entityContext.schedule("arduino-ping", 1, TimeUnit.MINUTES, () ->
                // start ping only when communicator status is online and registration succeeded
                getArduinoDeviceEntities().filter(a -> a.getStatus() == Status.ONLINE && a.getLiveStatus() == Status.ONLINE).forEach(entity -> {
                    entity.setMissedPings(entity.getMissedPings() + 1);
                    entity.getCommunicationProvider()
                            .sendCommand(entity, SendCommand.sendPayload(ArduinoCommandType.PING, ArduinoMessage.empty(), entity.getTarget()));
                    if (entity.getMissedPings() % 10 == 0) {
                        entity.setLiveStatus(Status.ERROR);
                        this.restartArduinoCommunicator(entity);
                    }
                    entityContext.saveDelayed(entity);
                }), true);

        this.entityContext.listenSettingValue(ArduinoUsbPortSetting.class, "arduino-listen-for-reload", port -> {
            if (port != null) {
                restartArduinoProviders();
            }
        });
    }

    private void restartArduinoCommunicator(ArduinoDeviceEntity arduinoDeviceEntity) {
        log.info("Restarting arduino communicator for device: <{}>", arduinoDeviceEntity.getTitle());
        ArduinoCommunicationProvider provider = arduinoDeviceEntity.getCommunicationProvider();
        provider.restart(arduinoDeviceEntity);
    }

    private void restartArduinoProviders() {
        getArduinoDeviceEntities().forEach(this::restartArduinoCommunicator);
    }

    private Stream<ArduinoDeviceEntity> getArduinoDeviceEntities() {
        return this.entityContext.findAll(ArduinoDeviceEntity.class).stream()
                .filter(a -> a.getCommunicationProvider() != null);
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
