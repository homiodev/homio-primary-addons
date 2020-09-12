package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.GET_PIN_VALUE_COMMAND;

@Component
@RequiredArgsConstructor
public class ArduinoGetPinValueCommand implements ArduinoCommandPlugin {

    private final LockManager<Integer> lockManager = new LockManager<>();

    @Override
    public ArduinoBaseCommand getCommand() {
        return GET_PIN_VALUE_COMMAND;
    }

    public Integer waitForValue(ArduinoMessage arduinoMessage) {
        return lockManager.await(String.valueOf(arduinoMessage.getMessageID()), 10000);
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        lockManager.signalAll(String.valueOf(message.getMessageID()), (int) message.getPayloadBuffer().get());
        return null;
    }

    @Override
    public boolean canReceiveGeneral() {
        return true;
    }
}
