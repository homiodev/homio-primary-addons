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
    private final ArduinoCommandPlugin commandPlugin; //2 bytes command

    public ArduinoMessage(byte messageID, ArduinoCommandPlugin commandPlugin, ByteBuffer payloadBuffer, ArduinoDeviceEntity arduinoDeviceEntity, ArduinoCommunicationProvider provider) {
        this.messageID = messageID;
        this.arduinoDeviceEntity = arduinoDeviceEntity;
        this.commandPlugin = commandPlugin;
        this.payloadBuffer = payloadBuffer;
        this.provider = provider;
    }

    public static ByteBuffer asLong(long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value);
    }

    @Override
    public String toString() {
        return "RF24Message{" +
                "messageID=" + messageID +
                ", command=" + (commandPlugin == null ? "" : commandPlugin.getName()) +
                '}';
    }

    public short getTarget() {
        return (short) arduinoDeviceEntity.getJsonData().getInt("target");
    }
}
