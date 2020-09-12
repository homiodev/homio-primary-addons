package org.touchhome.bundle.arduino.provider.command;

import org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand;
import org.touchhome.bundle.arduino.provider.communication.ArduinoMessage;
import org.touchhome.bundle.arduino.provider.communication.SendCommand;

public interface ArduinoCommandPlugin {

    ArduinoBaseCommand getCommand();

    default String getName() {
        return getCommand().name();
    }

    default SendCommand messageReceived(ArduinoMessage message) {
        throw new IllegalStateException("Unable execute command " + getName() + " on master");
    }

    // calls when 'execute' method was executed remotely and 'ack' was received
    default void onRemoteExecuted(ArduinoMessage message) {
        throw new IllegalStateException("onRemoteExecuted not implemented for command" + getName());
    }

    default boolean canReceiveGeneral() {
        return false;
    }
}
