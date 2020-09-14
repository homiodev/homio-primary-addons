package org.touchhome.bundle.arduino.provider.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.NotificationEntityJSON;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import java.util.HashMap;
import java.util.Map;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.REGISTER_COMMAND;

@Log4j2
@Component
public class ArduinoRegisterCommand implements ArduinoCommandPlugin {

    private final EntityContext entityContext;

    @Getter
    private final Map<Short, RegistrationPendingContext> pendingRegistrations = new HashMap<>();

    public ArduinoRegisterCommand(EntityContext entityContext) {
        this.entityContext = entityContext;
        entityContext.addEntityUpdateListener(ArduinoDeviceEntity.class, entity -> {
            RegistrationPendingContext pendingContext = pendingRegistrations.remove(entity.getTarget());
            if (pendingContext != null) {
                SendCommand sendCommand = SendCommand.sendPayload(ArduinoCommandType.REGISTER_CONFIRM_COMMAND, ArduinoMessage.empty(), entity.getTarget());
                entity.getCommunicationProvider().sendCommand(entity, sendCommand);
            }
        });
    }

    @Override
    public ArduinoCommandType getCommand() {
        return REGISTER_COMMAND;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        log.info("Got registering slave device command.");
        byte deviceType = message.getPayloadBuffer().get();
        if (deviceType == ArduinoDeviceEntity.DEVICE_TYPE) {
            String target = String.valueOf(message.getTarget());
            ArduinoDeviceEntity entity = message.getArduinoDeviceEntity();
            long uniqueID;

            if (entity == null) {
                uniqueID = message.getProvider().getUniqueIDOnRegistrationSuccess(null);
                pendingRegistrations.put(message.getTarget(), new RegistrationPendingContext(uniqueID, message));
                entityContext.addHeaderNotification(NotificationEntityJSON.info("arduino-" + message.getTarget())
                        .setName("Arduino").setDescription("Arduino <" + message.getTarget() + "> pending for registration"));
                entityContext.sendInfoMessage("Got arduino registration message with target id: <" + message.getTarget() + ">");
            } else {
                log.warn("Found arduino device with target <{}>", target);
                entity.setLiveStatus(Status.WAITING);
                if (!message.getProvider().equals(entity.getCommunicationProvider())) {
                    log.info("Set communication provider for arduino device: {}. Provider: {}",
                            entity, message.getProvider().getClass().getSimpleName());
                    entity.setCommunicationProvider(message.getProvider());
                }
                uniqueID = message.getProvider().getUniqueIDOnRegistrationSuccess(entity);
                entityContext.saveDelayed(entity);
            }
            if (uniqueID == 0) {
                throw new IllegalArgumentException("UniqueID must be not 0");
            }
            // send pipe inl value is need and byte ( 1 - require registration confirmation / 0 - do not require)
            return SendCommand.sendPayload(REGISTER_COMMAND,
                    ArduinoMessage.asLongAndByte(uniqueID, (byte) (entity == null ? 1 : 0)), message.getTarget(), message.getMessageID());
        } else {
            log.error("Unable to register unknown device type: <{}>", deviceType);
        }
        return SendCommand.SEND_ERROR;
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        ArduinoDeviceEntity entity = message.getArduinoDeviceEntity();
        if (entity == null) {
            log.info("Arduino registration successes with pending user select target");
        } else {
            log.info("Arduino registration successes for: <{}>", entity.getTitle());
            entity.setLiveStatus(Status.ONLINE);
            entity.setMissedPings(0);
            entityContext.saveDelayed(entity);
        }
    }

    @Override
    public boolean canReceiveGeneral() {
        return true;
    }

    @Getter
    @AllArgsConstructor
    private static class RegistrationPendingContext {
        private final long value;
        private final ArduinoMessage arduinoMessage;

    }
}
