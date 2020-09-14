package org.touchhome.bundle.arduino.provider.communication;

import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.pivovarit.function.ThrowingRunnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.ArduinoCommandPlugins;
import org.touchhome.bundle.arduino.provider.command.ArduinoCommandPlugin;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
@RequiredArgsConstructor
public final class ArduinoCommunicationProtocol<T> {

    public static final int BUFFER_SIZE = 32;
    private static final byte RECEIVE_BYTE = 36;

    protected final EntityContext entityContext;
    protected final ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    protected final ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    final ArduinoCommunicationProvider<?> provider;
    @Getter
    final ArduinoCommandPlugins arduinoCommandPlugins;
    private final ArduinoInputStream inputStream;
    private final ArduinoOutputStream<T> outputStream;
    private final boolean supportAsyncReadWrite;
    private final Runnable onCommunicationError;
    private final Object waitForAllowGlobalReading = new Object();
    private final Object closeLock = new Object();
    private final BlockingQueue<SendDescriptor> sendQueue = new LinkedBlockingQueue<>();
    private final Map<ReadListener, Long> readingSubscriptions = new ConcurrentHashMap<>();
    private Thread globalReadingThread;
    private Thread globalWritingThread;
    private volatile boolean isReadingDone = false;
    private volatile boolean isAllowGlobalReading = true;

    void subscribeForReading(ReadListener readListener) {
        readingSubscriptions.put(readListener, System.currentTimeMillis());
    }

    void send(SendDescriptor sendDescriptor) {
        SendCommand sendCommand = sendDescriptor.getSendCommand();
        send(sendCommand.getArduinoCommandType().getValue(),
                sendCommand.getTarget(),
                sendCommand.getMessageID(),
                sendCommand.getPayload(),
                sendDescriptor.getParam());
    }

    void close() {
        log.warn("Close arduino communicator: <{}>", getClass().getSimpleName());
        readingSubscriptions.clear();
        inputStream.close();
        outputStream.close();
        try {
            waitThreadToFinish(globalReadingThread);
            waitThreadToFinish(globalWritingThread);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to stop communication read/write threads", ex);
        }
    }

    void sendToQueue(SendCommand sendCommand, T param) {
        sendQueue.offer(new SendDescriptor(sendCommand, param));
    }

    void startReadWriteThreads() {
        globalReadingThread = new Thread(() -> {
            inputStream.prepareForRead();
            executeIO(this::tryReadFromPipe);
        }, "Global reading thread");
        globalReadingThread.start();
        globalWritingThread = new Thread(() -> executeIO(this::writeToPipe), "Global writing thread");
        globalWritingThread.start();
        this.entityContext.run("arduino-close-thread", () -> {
            synchronized (closeLock) {
                try {
                    closeLock.wait();
                    onCommunicationError.run();
                } catch (InterruptedException e) {
                    log.error("Got interrupt while await close event");
                }
            }
        }, false);
    }

    private void send(byte commandId, short target, byte messageId, byte[] payload, T param) {
        // prepare buffer
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.put((byte) 0x25);
        buffer.put((byte) 0x25);
        buffer.put((byte) (payload.length));
        short crc = calcCRC(messageId, target, commandId, payload);
        buffer.putShort(crc);
        buffer.put(messageId);
        buffer.putShort(target);
        buffer.put(commandId);
        buffer.put(payload);
        for (int i = buffer.position(); i < buffer.capacity(); i++) {
            buffer.put((byte) 0);
        }

        // actual send
        clearBuffer(sendBuffer);
        sendBuffer.put(buffer.array());
        try {
            if (!outputStream.write(param, sendBuffer.array())) {
                log.error("Failed sending!. Command <{}>. MessageId <{}>. Payload <{}>", commandId, messageId, payload);
            } else {
                log.debug("Success send message");
            }
        } catch (Exception ex) {
            log.error("Failed sending.", ex);
        }
        String cmd = String.valueOf(commandId);
        for (ArduinoCommandType arduinoCommandType : ArduinoCommandType.values()) {
            if (arduinoCommandType.getValue() == commandId) {
                cmd = arduinoCommandType.name();
            }
        }
        log.debug("Send: Cmd <{}>. Target: <{}>. MessageID: <{}>. Payload <{}>", cmd, target, messageId, payload);
    }

    private void clearBuffer(ByteBuffer byteBuffer) {
        // need cast to Buffer
        ((Buffer) byteBuffer).clear();
    }

    private void executeIO(ThrowingRunnable<Exception> runnable) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                runnable.run();
            } catch (SerialPortInvalidPortException se) {
                synchronized (closeLock) {
                    closeLock.notify();
                }
                return;
            } catch (InterruptedException ie) {
                return;
            } catch (Exception ex) {
                log.error("Error while execute thread <{}>: {}", Thread.currentThread().getName(), ex.getMessage());
            }
        }
    }

    private void writeToPipe() throws InterruptedException {
        SendDescriptor sendDescriptor = sendQueue.take();
        isAllowGlobalReading = false;
        while (!isReadingDone) {
            Thread.yield();
        }

        send(sendDescriptor);
        while (null != (sendDescriptor = sendQueue.poll())) {
            send(sendDescriptor);
        }

        isAllowGlobalReading = true;
        isReadingDone = false;
        synchronized (waitForAllowGlobalReading) {
            waitForAllowGlobalReading.notify();
        }
        log.debug("Sending done.");
    }

    private void tryReadFromPipe() throws Exception {
        if (isAllowGlobalReading) {
            // in case of async reading support we may skip available and call blocking reading
            if (supportAsyncReadWrite || inputStream.available()) {
                readFromPipe();
            }
            removeOldReadListeners();
        } else {
            log.debug("Reading suspended");
            isReadingDone = true;
            synchronized (waitForAllowGlobalReading) {
                waitForAllowGlobalReading.wait();
            }
            log.debug("Reading resumed");
            inputStream.prepareForRead();
        }
    }

    private void removeOldReadListeners() {
        long currentTimeMillis = System.currentTimeMillis();

        for (Map.Entry<ReadListener, Long> readingSubscription : readingSubscriptions.entrySet()) {
            ReadListener subscription = readingSubscription.getKey();

            if (subscription.maxTimeout() != null
                    && currentTimeMillis - readingSubscription.getValue() > subscription.maxTimeout()) {

                readingSubscriptions.remove(subscription);
                log.info(" ReadListener <{}> not received any message", subscription.getId());
                subscription.notReceived();
            }
        }
    }

    private void readFromPipe() throws Exception {
        clearBuffer(readBuffer);
        int readCount = inputStream.read(readBuffer);
        if (readCount == 0) {
            return;
        }
        byte firstByte = readBuffer.get();
        byte secondByte = readBuffer.get();

        if (firstByte == RECEIVE_BYTE && secondByte == RECEIVE_BYTE) {
            ArduinoRawMessage rawMessage = readRawMessage(readBuffer);
            if (rawMessage.isValidCRC()) {
                ArduinoMessage arduinoMessage = rawMessage.toParsedMessage();
                boolean anyReceived = false;
                for (ReadListener readListener : readingSubscriptions.keySet()) {
                    if (readListener.canReceive(arduinoMessage)) {
                        readListener.received(arduinoMessage);

                        // remove after receiving only if timeout not null
                        if (readListener.maxTimeout() != null) {
                            readingSubscriptions.remove(readListener);
                        }
                        anyReceived = true;
                    }
                }
                if (!anyReceived) {
                    log.error("No one subscription for message: <{}>", arduinoMessage);
                }
            } else {
                log.error("Received CRC value isn't correct");
            }
        } else {
            log.error("First or value inti bytes not match with 36. 1 - <{}>; 2 - <{}>", firstByte, secondByte);
        }
    }

    private ArduinoRawMessage readRawMessage(ByteBuffer readBuffer) {
        ArduinoRawMessage message = new ArduinoRawMessage();
        message.payloadLength = readBuffer.get();
        message.crc = readBuffer.getShort();

        message.messageID = readBuffer.get();
        message.target = readBuffer.getShort();
        message.commandID = readBuffer.get();

        // TODO: something wrong here!!!
        message.payloadBuffer = ByteBuffer.wrap(readBuffer.array(), readBuffer.position(), message.payloadLength).asReadOnlyBuffer();
        return message;
    }

    private void waitThreadToFinish(Thread thread) {
        if (thread != null) {
            thread.interrupt();
            if (thread.isAlive()) {
                try {
                    thread.join(60000);
                } catch (InterruptedException ignore) {
                }
            }
            if (thread.isAlive()) {
                throw new IllegalStateException("Unable to stop arduino thread: " + thread.getName());
            }
        }
    }

    private class ArduinoRawMessage {
        private ByteBuffer payloadBuffer;
        private byte messageID; // 2 bytes messageID
        private short target; // 2 byte target
        private byte commandID;
        private byte payloadLength;
        private short crc;

        public boolean isValidCRC() {
            ByteBuffer tmpBuffer = payloadBuffer.duplicate();
            byte[] payload = new byte[payloadLength];
            tmpBuffer.get(payload);
            return crc == ArduinoCommunicationProtocol.calcCRC(messageID, target, commandID, payload);
        }

        public ArduinoMessage toParsedMessage() {
            ArduinoDeviceEntity deviceEntity = entityContext.findAll(ArduinoDeviceEntity.class).stream()
                    .filter(a -> a.getTarget() == this.target).findAny().orElse(null);

            ArduinoCommandPlugin commandPlugin = ArduinoCommunicationProtocol.this.arduinoCommandPlugins.getArduinoCommandPlugin(commandID);
            return new ArduinoMessage(messageID, commandPlugin, payloadBuffer, deviceEntity, provider, target);
        }
    }

    @Data
    @AllArgsConstructor
    private class SendDescriptor {
        SendCommand sendCommand;
        T param;
    }

    private static short calcCRC(byte messageID, short target, byte commandID, byte[] payload) {
        int calcCRC = messageID + target + commandID;
        for (byte value : payload) {
            calcCRC += Math.abs(value);
        }
        return (short) ((0xbeaf + calcCRC) & 0x0FFFF);
    }
}
