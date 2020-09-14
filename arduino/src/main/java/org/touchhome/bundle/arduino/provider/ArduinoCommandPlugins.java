package org.touchhome.bundle.arduino.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.provider.command.ArduinoCommandPlugin;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArduinoCommandPlugins {

    private final EntityContext entityContext;

    private Map<Byte, ArduinoCommandPlugin> arduinoCommandPlugins;

    // deferred initialization
    private Map<Byte, ArduinoCommandPlugin> getArduinoCommandPlugins() {
        if (arduinoCommandPlugins == null) {
            this.arduinoCommandPlugins = entityContext.getBeansOfType(ArduinoCommandPlugin.class).stream().collect(Collectors.toMap(p -> p.getCommand().getValue(), p -> p));
        }
        return arduinoCommandPlugins;
    }

    // for 3-th parts
    public void addArduinoCommandPlugin(ArduinoCommandPlugin arduinoCommandPlugin) {
        arduinoCommandPlugins.putIfAbsent(arduinoCommandPlugin.getCommand().getValue(), arduinoCommandPlugin);
    }

    public ArduinoCommandPlugin getArduinoCommandPlugin(byte commandID) {
        ArduinoCommandPlugin commandPlugin = getArduinoCommandPlugins().get(commandID);
        if (commandPlugin == null) {
            throw new IllegalArgumentException("Unable to find RF24CommandPlugin for commandID: " + commandID);
        }
        return commandPlugin;
    }

    public ArduinoCommandPlugin getArduinoCommandPlugin(ArduinoCommandType arduinoCommandType) {
        return getArduinoCommandPlugin(arduinoCommandType.getValue());
    }
}
