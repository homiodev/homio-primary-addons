package org.touchhome.bundle.arduino.provider.communication;

import lombok.Getter;

@Getter
public enum ArduinoCommandType {
    EXECUTED(0),
    FAILED_EXECUTED(1),
    DEBUG(2),

    REGISTER_COMMAND(10),
    REGISTER_CONFIRM_COMMAND(11),

    GET_ID_COMMAND(20),
    GET_TIME_COMMAND(21),
    PING(22),

    SET_PIN_VALUE_ON_HANDLER_REQUEST_COMMAND(30),
    GET_PIN_VALUE_COMMAND(31),

    SET_PIN_VALUE_COMMAND(40),

    GET_PIN_VALUE_REQUEST_COMMAND(60),
    REMOVE_GET_PIN_VALUE_REQUEST_COMMAND(61),

    HANDLER_REQUEST_WHEN_PIN_VALUE_OP_THAN(70),
    REMOVE_HANDLER_REQUEST_WHEN_PIN_VALUE_OP_THAN(71);

    private final byte value;

    ArduinoCommandType(int value) {
        this.value = (byte) value;
    }
}
