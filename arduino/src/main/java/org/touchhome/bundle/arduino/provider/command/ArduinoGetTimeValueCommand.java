package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.GET_TIME_COMMAND;

@Component
@RequiredArgsConstructor
public class ArduinoGetTimeValueCommand implements ArduinoCommandPlugin {

    private final LockManager<Long> lockManager = new LockManager<>();

    @Override
    public ArduinoCommandType getCommand() {
        return GET_TIME_COMMAND;
    }

    public Long waitForValue(byte messageID) {
        return lockManager.await(String.valueOf(messageID), 10000);
    }

    @Override
    public void onRemoteExecuted(ArduinoMessage message) {
        lockManager.signalAll(String.valueOf(message.getMessageID()), message.getPayloadBuffer().getLong());
    }
}
