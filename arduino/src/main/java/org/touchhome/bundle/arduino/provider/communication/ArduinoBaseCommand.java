package org.touchhome.bundle.arduino.provider.communication;

import lombok.Getter;

@Getter
public enum ArduinoBaseCommand {
    EXECUTED(0),
    FAILED_EXECUTED(1),

    REGISTER_COMMAND(2),
    REGISTER_SUCCESS_COMMAND(3),
    READY_COMMAND(21),

    GET_ID_COMMAND(4),
    GET_TIME_COMMAND(5),

    SET_PIN_VALUE_ON_HANDLER_REQUEST_COMMAND(6),
    GET_PIN_VALUE_COMMAND(7),

    SET_PIN_DIGITAL_VALUE_COMMAND(8),
    SET_PIN_ANALOG_VALUE_COMMAND(9),

    RESPONSE_COMMAND(10),
    PING(11),

    GET_PIN_VALUE_REQUEST_COMMAND(14),
    REMOVE_GET_PIN_VALUE_REQUEST_COMMAND(15),

    HANDLER_REQUEST_WHEN_PIN_VALUE_OP_THAN(29),
    REMOVE_HANDLER_REQUEST_WHEN_PIN_VALUE_OP_THAN(30);

    private final byte value;

    ArduinoBaseCommand(int value) {
        this.value = (byte) value;
    }
}
