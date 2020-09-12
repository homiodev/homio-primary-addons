package org.touchhome.bundle.arduino.provider.usb;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.arduino.provider.communication.ArduinoInputStream;
import org.touchhome.bundle.arduino.provider.communication.ArduinoOutputStream;

import java.nio.ByteBuffer;

@Log4j2
@Component
public class ArduinoUsbInputOutputStream implements ArduinoInputStream, ArduinoOutputStream<Void> {

    private SerialPort serialPort;

    @Override
    public boolean available() {
        return serialPort.bytesAvailable() > 0;
    }

    @Override
    public int read(ByteBuffer readBuffer) {
        if (!serialPort.isOpen()) {
            throw new SerialPortInvalidPortException();
        }
        // to avoid NotMethodFoundException
        return serialPort.readBytes(readBuffer.array(), readBuffer.capacity());
    }

    @Override
    public void prepareForRead() {

    }

    @SneakyThrows
    @Override
    public boolean write(Void param, byte[] array) {
        serialPort.getOutputStream().write(array);
        return true;
    }

    @SneakyThrows
    @Override
    public void close() {
        serialPort.closePort();
    }

    public boolean initialize(SerialPort port) {
        this.serialPort = port;
        // port.setComPortParameters(9600, 8, 1, 0);
        // this.portStream.getSerialPort().setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        // sleep 2 sec for full initialization
        if (!this.serialPort.openPort(2000)) {
            this.updateStatus(Status.ERROR, "UNABLE_OPEN_PORT");
            return false;
        }
        // read func wait until at least one byte available, but release after 10 sec.
        this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 10000, 0);

        // clear old data from port
        clearOldPortData();

        return true;
    }

    private void clearOldPortData() {
        byte[] array = new byte[1024];
        for (int i = 0; i < this.serialPort.bytesAvailable() / array.length; i++) {
            serialPort.readBytes(array, array.length);
        }
        serialPort.readBytes(array, array.length);
    }

    private void updateStatus(Status status, String error) {
        if (status == Status.ERROR) {
            this.serialPort = null;
        }
    }
}
