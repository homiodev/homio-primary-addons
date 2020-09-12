package org.touchhome.bundle.arduino.provider.communication;

import java.nio.ByteBuffer;

public interface ArduinoInputStream {
    boolean available();

    int read(ByteBuffer readBuffer);

    void prepareForRead();

    void close();
}
