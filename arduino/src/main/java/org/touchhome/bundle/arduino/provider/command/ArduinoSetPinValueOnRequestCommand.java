package org.touchhome.bundle.arduino.provider.command;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.SET_PIN_VALUE_ON_HANDLER_REQUEST_COMMAND;

@Log4j2
@Component
@AllArgsConstructor
public class ArduinoSetPinValueOnRequestCommand implements ArduinoCommandPlugin {

    @Override
    public ArduinoBaseCommand getCommand() {
        return SET_PIN_VALUE_ON_HANDLER_REQUEST_COMMAND;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
       /* TODO: log.info("Got <{}>", getName());

        ByteBuffer payloadBuffer = message.getPayloadBuffer();
        byte handlerID = payloadBuffer.get();

        ScratchEntity scratchEntity = manager.getScratchRepository().getScratchByHandlerID(handlerID);
        if (scratchEntity == null) {
            throw new IllegalArgumentException("Unable to find ScratchEntity by handlerID: " + handlerID);
        }

        DeviceBaseEntity device = manager.getEntityByAttachedScratch(scratchEntity);
        PinRepository.PinEntryBinding pinEntity = manager.getPinRepository().findByEntity(device, PinPool.INPUT);
        DeviceBaseEntity targetDevice = pinEntity.getTargetDevice();
        Set<ScratchUpdateValue> scratchUpdateValues = new HashSet<>();

        manager.getScratchPlugin(scratchEntity).handleRequestPinValueEvent(scratchEntity, targetDevice, new ArduinoRemoteCommand(payloadBuffer), scratchUpdateValues, null);
        manager.saveDelayed(scratchEntity);
        manager.sendNotification("-scratch-updates", scratchUpdateValues);*/
        return null;
    }

    @Override
    public boolean canReceiveGeneral() {
        return true;
    }
}
