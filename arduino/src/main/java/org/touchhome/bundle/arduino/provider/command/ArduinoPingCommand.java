package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.PING;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoPingCommand implements ArduinoCommandPlugin {

    private final EntityContext entityContext;

    @Override
    public ArduinoCommandType getCommand() {
        return PING;
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        ArduinoDeviceEntity entity = message.getArduinoDeviceEntity();
        entity.setMissedPings(0);
        entity.setLiveStatus(Status.ONLINE);
        entityContext.saveDelayed(entity);
    }
}
