package org.touchhome.bundle.arduino.provider.communication;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
public class SendCommand {
    public static SendCommand SEND_ERROR = new SendCommand(true, null, ArduinoCommandType.FAILED_EXECUTED, (short) 0, null);
    private final boolean isError;
    private final byte[] payload;
    private final ArduinoCommandType arduinoCommandType;
    private final short target;
    private Byte messageID;

    private SendCommand(boolean isError, byte[] payload, ArduinoCommandType arduinoCommandType, short target, Byte messageID) {
        this.isError = isError;
        this.payload = payload;
        this.arduinoCommandType = arduinoCommandType;
        this.target = target;
        this.messageID = messageID;
    }

    public static SendCommand sendPayload(ArduinoCommandType arduinoCommandType, ByteBuffer buffer, short target) {
        return sendPayload(arduinoCommandType, buffer, target, null);
    }

    public static SendCommand sendPayload(ArduinoCommandType arduinoCommandType, ByteBuffer buffer, short target, Byte messageID) {
        return new SendCommand(false, buffer.array(), arduinoCommandType, target, messageID);
    }

    void setMessageID(byte messageID) {
        this.messageID = messageID;
    }

    @Override
    public String toString() {
        return "SendCommand{" +
                "isError=" + isError +
                ", payload=" + Arrays.toString(payload) +
                ", command=" + arduinoCommandType.name() +
                '}';
    }
}
