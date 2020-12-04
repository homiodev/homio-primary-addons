package org.touchhome.bundle.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.io.serial.DataBits;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.CommunicatorConsolePlugin;
import org.touchhome.bundle.api.console.ComplexLinesConsolePlugin;
import org.touchhome.bundle.api.port.BaseSerialPort;
import org.touchhome.bundle.api.port.PortFlowControl;

import java.util.Collection;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_NONBLOCKING;

@Log4j2
public class RawSerialPortCommunicator extends BaseSerialPort {
    private final CommunicatorConsolePlugin communicatorConsolePlugin;
    @Getter
    private final CircularFifoQueue<ComplexLinesConsolePlugin.ComplexString> buffer = new CircularFifoQueue<>(1000);

    public RawSerialPortCommunicator(SerialPort serialPort, EntityContext entityContext, CommunicatorConsolePlugin communicatorConsolePlugin) {
        super("", entityContext, serialPort, 9600, PortFlowControl.FLOWCONTROL_OUT_NONE, () ->
                entityContext.ui().sendErrorMessage("SERIAL_PORT.EXCEPTION"), null);
        this.communicatorConsolePlugin = communicatorConsolePlugin;
    }

    @Override
    public boolean open(int baudRate, PortFlowControl flowControl) {
        serialPort.setComPortTimeouts(TIMEOUT_NONBLOCKING, 100, 0);
        serialPort.setComPortParameters(baudRate, DataBits._8.getValue(), SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        return super.open(baudRate, flowControl);
    }

    @Override
    protected void handleSerialEvent(byte[] buf) {
        ComplexLinesConsolePlugin.ComplexString data = ComplexLinesConsolePlugin.ComplexString.of(new String(buf), System.currentTimeMillis());
        buffer.add(data);
        communicatorConsolePlugin.dataReceived(data);
    }

    public Collection<ComplexLinesConsolePlugin.ComplexString> getValues() {
        return buffer;
    }
}
