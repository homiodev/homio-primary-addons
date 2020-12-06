package org.touchhome.bundle.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.pi4j.io.serial.DataBits;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginCommunicator;
import org.touchhome.bundle.api.console.ConsolePluginComplexLines;
import org.touchhome.bundle.api.port.BaseSerialPort;
import org.touchhome.bundle.api.port.PortFlowControl;

import java.util.Collection;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_NONBLOCKING;

@Log4j2
public class RawSerialPortCommunicator extends BaseSerialPort {
    private final ConsolePluginCommunicator communicatorConsolePlugin;
    @Getter
    private final CircularFifoQueue<ConsolePluginComplexLines.ComplexString> buffer = new CircularFifoQueue<>(1000);

    public RawSerialPortCommunicator(SerialPort serialPort, EntityContext entityContext, ConsolePluginCommunicator consolePluginCommunicator) {
        super("", entityContext, serialPort, 9600, PortFlowControl.FLOWCONTROL_OUT_NONE, () ->
                entityContext.ui().sendErrorMessage("SERIAL_PORT.EXCEPTION"), null);
        this.communicatorConsolePlugin = consolePluginCommunicator;
    }

    @Override
    public boolean open(int baudRate, PortFlowControl flowControl) {
        serialPort.setComPortTimeouts(TIMEOUT_NONBLOCKING, 100, 0);
        serialPort.setComPortParameters(baudRate, DataBits._8.getValue(), SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        return super.open(baudRate, flowControl);
    }

    @Override
    protected void handleSerialEvent(byte[] buf) {
        ConsolePluginComplexLines.ComplexString data = ConsolePluginComplexLines.ComplexString.of(new String(buf), System.currentTimeMillis());
        buffer.add(data);
        communicatorConsolePlugin.dataReceived(data);
    }

    public Collection<ConsolePluginComplexLines.ComplexString> getValues() {
        return buffer;
    }
}
