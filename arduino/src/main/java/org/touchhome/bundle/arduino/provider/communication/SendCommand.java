package org.touchhome.bundle.arduino.provider.communication;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SendCommand {
    public static SendCommand SEND_ERROR = new SendCommand(true, null, ArduinoBaseCommand.FAILED_EXECUTED);
    private final boolean isError;
    private final byte[] payload;
    private final byte commandID;

    private SendCommand(boolean isError, byte[] payload, ArduinoBaseCommand arduinoBaseCommand) {
        this.isError = isError;
        this.payload = payload;
        this.commandID = (byte) arduinoBaseCommand.getValue();
    }

    public static SendCommand sendPayload(ArduinoBaseCommand arduinoBaseCommand, ByteBuffer buffer) {
        return new SendCommand(false, buffer.array(), arduinoBaseCommand);
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte getCommandID() {
        return commandID;
    }

    public boolean isError() {
        return isError;
    }

    @Override
    public String toString() {
        return "SendCommand{" +
                "isError=" + isError +
                ", payload=" + Arrays.toString(payload) +
                ", command=" + commandID +
                '}';
    }
}
