package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.REGISTER_SUCCESS_COMMAND;

@Component
@RequiredArgsConstructor
public class ArduinoReadyCommand implements ArduinoCommandPlugin {

    private final EntityContext entityContext;

    @Override
    public ArduinoBaseCommand getCommand() {
        return REGISTER_SUCCESS_COMMAND;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        return null;
    }

    /**
     * When we received autoacknolegde that register success received
     *
     * @param message
     */
    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        String sensorID = String.valueOf(message.getTarget());
        /*for (ArduinoDeviceEntity arduinoDeviceEntity : entityContext.findAll(ArduinoDeviceEntity.class)) {
            if(arduinoDeviceEntity.getIeeeAddress().equals(sensorID)) {

            }
        }

        ArduinoDeviceEntity entity = entityContext.getEntity(ArduinoDeviceRepository.PREFIX + sensorID);*/

        // such we paired devices we may send handles, ...
       /* TODO: for (AbstractRepository repository : manager.getEntityManager().getRepositories()) {
            if (repository instanceof AbstractDeviceRepository) {
                List<BaseEntity> list = manager.listAllByRepository(repository);
                for (BaseEntity baseEntity : list) {
                    DeviceBaseEntity device = (DeviceBaseEntity) baseEntity;
                    ((AbstractDeviceRepository) repository).notifyUpdate(device, null);
                }
            }
        }*/
    }
}
