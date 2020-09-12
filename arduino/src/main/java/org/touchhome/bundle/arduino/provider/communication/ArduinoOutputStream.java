package org.touchhome.bundle.arduino.provider.communication;

public interface ArduinoOutputStream<T> {

    boolean write(T param, byte[] array);

    void close();
}
