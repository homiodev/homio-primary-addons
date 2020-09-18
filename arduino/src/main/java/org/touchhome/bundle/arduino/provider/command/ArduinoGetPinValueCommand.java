package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.GET_PIN_VALUE_COMMAND;

@Component
@RequiredArgsConstructor
public class ArduinoGetPinValueCommand implements ArduinoCommandPlugin {

    private final LockManager<Integer> lockManager = new LockManager<>();

    @Override
    public ArduinoCommandType getCommand() {
        return GET_PIN_VALUE_COMMAND;
    }

    public Integer waitForValue(byte messageID) {
        return lockManager.await(String.valueOf(messageID), 10000);
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        lockManager.signalAll(String.valueOf(message.getMessageID()), (int) message.getPayloadBuffer().get());
    }
}