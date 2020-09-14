package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.REGISTER_CONFIRM_COMMAND;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoRegisterConfirmCommand implements ArduinoCommandPlugin {

    private final EntityContext entityContext;

    @Override
    public ArduinoCommandType getCommand() {
        return REGISTER_CONFIRM_COMMAND;
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        ArduinoDeviceEntity entity = message.getArduinoDeviceEntity();
        if (entity == null) {
            log.error("Got registration confirm command for unknown entity with target: <{}>", message.getTarget());
            return;
        }
        log.info("Arduino registration confirmation succedded for: <{}>", entity.getTitle());
        entity.setLiveStatus(Status.ONLINE);
        entity.setMissedPings(0);
        entityContext.saveDelayed(entity);
    }
}
