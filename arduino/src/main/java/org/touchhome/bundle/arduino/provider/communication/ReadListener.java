package org.touchhome.bundle.arduino.provider.communication;

public interface ReadListener {
    boolean canReceive(ArduinoMessage arduinoMessage);

    void received(ArduinoMessage arduinoMessage) throws Exception;

    void notReceived();

    default Integer maxTimeout() {
        return null; // if null - not delete from listener
    }

    String getId();
}
