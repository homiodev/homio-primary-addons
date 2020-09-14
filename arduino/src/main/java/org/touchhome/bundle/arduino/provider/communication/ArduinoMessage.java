package org.touchhome.bundle.arduino.provider.communication;

import lombok.Getter;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.command.ArduinoCommandPlugin;

import java.nio.ByteBuffer;

@Getter
public class ArduinoMessage {
    private final ByteBuffer payloadBuffer;
    private final byte messageID; // 2 bytes messageID
    private final ArduinoDeviceEntity arduinoDeviceEntity; // 2 byte target
    private final ArduinoCommunicationProvider provider;
    private short target;
    private final ArduinoCommandPlugin commandPlugin; //2 bytes command

    public ArduinoMessage(byte messageID, ArduinoCommandPlugin commandPlugin, ByteBuffer payloadBuffer, ArduinoDeviceEntity arduinoDeviceEntity, ArduinoCommunicationProvider provider, short target) {
        this.messageID = messageID;
        this.arduinoDeviceEntity = arduinoDeviceEntity;
        this.commandPlugin = commandPlugin;
        this.payloadBuffer = payloadBuffer;
        this.provider = provider;
        this.target = target;
    }

    public static ByteBuffer asLongAndByte(long value, byte value2) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Byte.BYTES);
        buffer.putLong(value);
        buffer.put(value2);
        return buffer;
    }

    public static ByteBuffer asLong(long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value);
    }

    public static ByteBuffer empty() {
        return ByteBuffer.allocate(0);
    }

    @Override
    public String toString() {
        return "RF24Message{" +
                "messageID=" + messageID +
                ", command=" + (commandPlugin == null ? "" : commandPlugin.getName()) +
                '}';
    }
}
