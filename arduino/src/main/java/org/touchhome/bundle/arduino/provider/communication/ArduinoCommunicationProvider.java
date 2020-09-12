package org.touchhome.bundle.arduino.provider.communication;

import com.pi4j.io.gpio.Pin;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.ArduinoCommandPlugins;
import org.touchhome.bundle.arduino.provider.command.ArduinoGetPinValueCommand;

import java.nio.ByteBuffer;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoBaseCommand.*;

@Log4j2
public abstract class ArduinoCommunicationProvider<T> {

    protected final EntityContext entityContext;
    private final ArduinoCommunicationProtocol<T> arduinoCommunicationProtocol;
    private byte messageID = 0;

    public ArduinoCommunicationProvider(EntityContext entityContext, ArduinoCommandPlugins arduinoCommandPlugins,
                                        ArduinoInputStream inputStream, ArduinoOutputStream<T> outputStream, boolean supportAsyncReadWrite) {
        this.entityContext = entityContext;
        this.arduinoCommunicationProtocol = new ArduinoCommunicationProtocol<>(entityContext, this,
                arduinoCommandPlugins, inputStream, outputStream, supportAsyncReadWrite, this::onCommunicationError);
    }

    protected abstract void onCommunicationError();

    protected abstract T createParameter(ArduinoDeviceEntity arduinoDeviceEntity);

    public abstract long onRegistrationSuccess(ArduinoDeviceEntity entity);

    protected abstract boolean beforeStart();

    public final void subscribeForReading(ReadListener readListener) {
        arduinoCommunicationProtocol.subscribeForReading(readListener);
    }

    public final void setValue(ArduinoDeviceEntity arduinoDeviceEntity, Pin pin, Byte value, boolean analog) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) pin.getAddress());
        buffer.put(value);
        sendCommand(arduinoDeviceEntity, SendCommand.sendPayload(analog ? SET_PIN_ANALOG_VALUE_COMMAND : SET_PIN_DIGITAL_VALUE_COMMAND, buffer));
    }

    public final Integer getValueSync(ArduinoDeviceEntity arduinoDeviceEntity, Pin pin, boolean analog) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) pin.getAddress());
        buffer.put((byte) (analog ? 1 : 0));
        ArduinoMessage arduinoMessage = sendCommand(arduinoDeviceEntity, SendCommand.sendPayload(GET_PIN_VALUE_COMMAND, buffer));

        return ((ArduinoGetPinValueCommand) this.arduinoCommunicationProtocol.getArduinoCommandPlugins().getArduinoCommandPlugin(GET_PIN_VALUE_COMMAND))
                .waitForValue(arduinoMessage);
    }

    private ArduinoMessage generateCommand(ArduinoDeviceEntity arduinoDeviceEntity) {
        if (messageID > 125) {
            messageID = 0;
        }
        messageID++;
        return new ArduinoMessage(messageID, null, null, arduinoDeviceEntity, this);
    }

    @Override
    public final boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    private ArduinoMessage sendCommand(ArduinoDeviceEntity arduinoDeviceEntity, SendCommand sendCommand) {
        ArduinoMessage arduinoMessage = generateCommand(arduinoDeviceEntity);
        arduinoCommunicationProtocol.sendToQueue(sendCommand, arduinoMessage, this.createParameter(arduinoDeviceEntity));
        return arduinoMessage;
    }

    public final boolean restart() {
        if (this.beforeStart()) {
            log.info("Restarting arduino communicator: <{}>", this.getClass().getSimpleName());
            this.start();
            return true;
        }
        return false;
    }

    private void start() {
        arduinoCommunicationProtocol.startReadWriteThreads();
        subscribeForReading(new ArduinoReadingListener());
    }

    public void destroy() {
        arduinoCommunicationProtocol.close();
    }

    private final class ArduinoReadingListener implements ReadListener {

        @Override
        public boolean canReceive(ArduinoMessage arduinoMessage) {
            return arduinoMessage.getCommandPlugin().canReceiveGeneral();
        }

        @Override
        public void received(ArduinoMessage arduinoMessage) {
            SendCommand sendCommand = arduinoMessage.getCommandPlugin().messageReceived(arduinoMessage);
            if (sendCommand != null) {
                arduinoCommunicationProtocol.sendToQueue(sendCommand, arduinoMessage, null);
                //  provider.scheduleSend(sendCommand, arduinoMessage, null); // TODO:???????????
                // TODO: rf24CommunicationProtocol.sendToQueue(sendCommand, arduinoMessage, GLOBAL_WRITE_PIPE);
            }
        }

        @Override
        public void notReceived() {
            throw new IllegalStateException("Must be not called!!!");
        }

        @Override
        public String getId() {
            return "Listener";
        }
    }
}
