package org.touchhome.bundle.arduino.provider.command;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.SET_PIN_VALUE_COMMAND;

@Log4j2
@Component
@AllArgsConstructor
public class ArduinoSetPinValueCommand implements ArduinoCommandPlugin {

    private final LockManager<Void> lockManager = new LockManager<>();

    @Override
    public ArduinoCommandType getCommand() {
        return SET_PIN_VALUE_COMMAND;
    }

    public void waitForValue(byte messageID) {
        lockManager.await(String.valueOf(messageID), 10000);
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        lockManager.signalAll(String.valueOf(message.getMessageID()), null);
    }
}
