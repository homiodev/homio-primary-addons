package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import java.util.List;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.REGISTER_COMMAND;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoRegisterCommand implements ArduinoCommandPlugin {

    private final EntityContext entityContext;

    @Override
    public ArduinoBaseCommand getCommand() {
        return REGISTER_COMMAND;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        log.info("Got registering slave device command.");
        byte dt = message.getPayloadBuffer().get();
        if (dt == 17) { // Arduino
            String target = String.valueOf(message.getTarget());
            List<ArduinoDeviceEntity> devices = entityContext.findAll(ArduinoDeviceEntity.class);
            ArduinoDeviceEntity entity = devices.stream().filter(d -> target.equals(d.getJsonData().optString("target"))).findAny().orElse(null);
            if (entity == null) {
                entity = new ArduinoDeviceEntity();
                entity.setCommunicationProvider(message.getProvider());
                entity.getJsonData().put("target", target);
            } else {
                log.warn("Arduino device with id <{}> already registered", target);
                if (!message.getProvider().equals(entity.getCommunicationProvider())) {
                    log.info("Set communication provider for arduino device: {}. Provider: {}",
                            entity, message.getProvider().getClass().getSimpleName());
                    entity.setCommunicationProvider(message.getProvider());
                    entityContext.save(entity);
                }
            }
            long value = message.getProvider().onRegistrationSuccess(entity);
            return SendCommand.sendPayload(ArduinoBaseCommand.REGISTER_SUCCESS_COMMAND, ArduinoMessage.asLong(value));
        } else {
            log.error("Unable to register unknown device type: <{}>", dt);
        }
        return SendCommand.SEND_ERROR;
    }

    @Override
    public boolean canReceiveGeneral() {
        return true;
    }
}
