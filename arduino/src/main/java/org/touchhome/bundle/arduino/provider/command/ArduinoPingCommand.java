package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.PING;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoPingCommand implements ArduinoCommandPlugin {

    @Override
    public ArduinoBaseCommand getCommand() {
        return PING;
    }
}
