package org.touchhome.bundle.arduino.provider;

import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.provider.command.ArduinoCommandPlugin;
import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArduinoCommandPlugins {

    private final Map<Byte, ArduinoCommandPlugin> arduinoCommandPlugins;

    public ArduinoCommandPlugins(EntityContext entityContext) {
        this.arduinoCommandPlugins = entityContext.getBeansOfType(ArduinoCommandPlugin.class).stream().collect(Collectors.toMap(p -> p.getCommand().getValue(), p -> p));
    }

    public void addArduinoCommandPlugin(ArduinoCommandPlugin arduinoCommandPlugin) {
        arduinoCommandPlugins.putIfAbsent(arduinoCommandPlugin.getCommand().getValue(), arduinoCommandPlugin);
    }

    public ArduinoCommandPlugin getArduinoCommandPlugin(byte commandID) {
        ArduinoCommandPlugin commandPlugin = arduinoCommandPlugins.get(commandID);
        if (commandPlugin == null) {
            throw new IllegalArgumentException("Unable to find RF24CommandPlugin for commandID: " + commandID);
        }
        return commandPlugin;
    }

    public ArduinoCommandPlugin getArduinoCommandPlugin(ArduinoBaseCommand arduinoBaseCommand) {
        return getArduinoCommandPlugin(arduinoBaseCommand.getValue());
    }
}
