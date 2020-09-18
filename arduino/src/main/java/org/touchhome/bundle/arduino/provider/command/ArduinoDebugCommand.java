package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.DEBUG;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoDebugCommand implements ArduinoCommandPlugin {

    @Override
    public ArduinoCommandType getCommand() {
        return DEBUG;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        byte executedCommand = message.getPayloadBuffer().get();
        log.warn("Arduino DEBUG value: " + executedCommand);
        return null;
    }
}
