package org.touchhome.bundle.arduino.provider.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.arduino.provider.ArduinoCommandPlugins;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.EXECUTED;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArduinoExecutedCommand implements ArduinoCommandPlugin {

    private final ArduinoCommandPlugins arduinoCommandPlugins;

    @Override
    public ArduinoBaseCommand getCommand() {
        return EXECUTED;
    }

    @Override
    public SendCommand messageReceived(ArduinoMessage message) {
        byte executedCommand = message.getPayloadBuffer().get();
        ArduinoCommandPlugin commandPlugin = arduinoCommandPlugins.getArduinoCommandPlugin(executedCommand);

        log.info("Previous command <{}> with message id <{}> executed successfully on target device <{}>", commandPlugin.getName(), message.getMessageID(), message.getTarget());
        commandPlugin.onRemoteExecuted(message);
        return null;
    }

    @Override
    public boolean canReceiveGeneral() {
        return true;
    }
}
