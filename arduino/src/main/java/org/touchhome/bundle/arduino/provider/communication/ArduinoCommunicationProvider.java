package org.touchhome.bundle.arduino.provider.communication;

import com.pi4j.io.gpio.Pin;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.json.NotificationEntityJSON;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.ArduinoCommandPlugins;
import org.touchhome.bundle.arduino.provider.command.ArduinoGetPinValueCommand;

import java.nio.ByteBuffer;

import static org.touchhome.bundle.arduino.provider.communication.ArduinoCommandType.*;

@Log4j2
public abstract class ArduinoCommunicationProvider<T> {

    protected final EntityContext entityContext;
    private final ArduinoCommunicationProtocol<T> arduinoCommunicationProtocol;
    private byte messageID = 0;
    @Getter
    private String error;

    public ArduinoCommunicationProvider(EntityContext entityContext, ArduinoCommandPlugins arduinoCommandPlugins,
                                        ArduinoInputStream inputStream, ArduinoOutputStream<T> outputStream, boolean supportAsyncReadWrite) {
        this.entityContext = entityContext;
        this.arduinoCommunicationProtocol = new ArduinoCommunicationProtocol<>(entityContext, this,
                arduinoCommandPlugins, inputStream, outputStream, supportAsyncReadWrite, this::onCommunicationError);
    }

    protected abstract void onCommunicationError();

    protected abstract T createParameter(ArduinoDeviceEntity arduinoDeviceEntity);

    public abstract long getUniqueIDOnRegistrationSuccess(ArduinoDeviceEntity entity);

    protected abstract boolean beforeStart() throws Exception;

    public final void subscribeForReading(ReadListener readListener) {
        arduinoCommunicationProtocol.subscribeForReading(readListener);
    }

    public final void setValue(ArduinoDeviceEntity arduinoDeviceEntity, Pin pin, Byte value, boolean analog) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) pin.getAddress());
        buffer.put(value);
        sendCommand(arduinoDeviceEntity, SendCommand.sendPayload(analog ? SET_PIN_ANALOG_VALUE_COMMAND : SET_PIN_DIGITAL_VALUE_COMMAND, buffer, arduinoDeviceEntity.getTarget()));
    }

    public final Integer getValueSync(ArduinoDeviceEntity arduinoDeviceEntity, Pin pin, boolean analog) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) pin.getAddress());
        buffer.put((byte) (analog ? 1 : 0));
        SendCommand sendCommand = SendCommand.sendPayload(GET_PIN_VALUE_COMMAND, buffer, arduinoDeviceEntity.getTarget());
        sendCommand(arduinoDeviceEntity, sendCommand);

        return ((ArduinoGetPinValueCommand) this.arduinoCommunicationProtocol.getArduinoCommandPlugins().getArduinoCommandPlugin(GET_PIN_VALUE_COMMAND))
                .waitForValue(sendCommand.getMessageID());
    }

    public byte nextMessageId() {
        if (messageID > 125) {
            messageID = 0;
        }
        messageID++;
        return messageID;
    }

    @Override
    public final boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    public void sendCommand(ArduinoDeviceEntity arduinoDeviceEntity, SendCommand sendCommand) {
        if (sendCommand.getMessageID() == null) {
            sendCommand.setMessageID(nextMessageId());
        }
        arduinoCommunicationProtocol.sendToQueue(sendCommand, this.createParameter(arduinoDeviceEntity));
    }

    public final boolean restart(ArduinoDeviceEntity arduinoDeviceEntity) {
        try {
            if (this.beforeStart()) {
                log.info("Restarting arduino communicator: <{}>", this.getClass().getSimpleName());
                this.start();
            }
            updateDeviceStatus(arduinoDeviceEntity, Status.ONLINE);
            return true;
        } catch (Exception ex) {
            this.error = ex.getMessage();
        }
        updateDeviceStatus(arduinoDeviceEntity, Status.ERROR);
        return false;
    }

    private void updateDeviceStatus(ArduinoDeviceEntity arduinoDeviceEntity, Status status) {
        if (arduinoDeviceEntity.getStatus() != status) {
            arduinoDeviceEntity.setStatus(status);
            entityContext.saveDelayed(arduinoDeviceEntity);

            entityContext.addHeaderNotification(NotificationEntityJSON.warn(arduinoDeviceEntity.getEntityID())
                    .setName("Arduino-" + arduinoDeviceEntity.getIeeeAddress()).setDescription("Communicator status: " + status));

        }
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
                arduinoCommunicationProtocol.sendToQueue(sendCommand, null);
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
