package org.touchhome.bundle.serial;

import com.fazecast.jSerialComm.SerialPort;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.CommunicatorConsolePlugin;
import org.touchhome.bundle.api.setting.BundleSettingPlugin;
import org.touchhome.bundle.api.ui.ToastrException;
import org.touchhome.bundle.api.util.FlowMap;
import org.touchhome.bundle.serial.settings.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.touchhome.bundle.api.util.TouchHomeUtils.RED_COLOR;

@Component
@RequiredArgsConstructor
public class SerialPortConsolePlugin implements CommunicatorConsolePlugin {

    private final EntityContext entityContext;
    private RawSerialPortCommunicator rawSerialPortCommunicator;
    private SerialPortSendEndLine.EndLineType endLineType;

    @Override
    public Collection<ComplexString> getComplexValue() {
        return rawSerialPortCommunicator == null ? null : rawSerialPortCommunicator.getValues();
    }

    public void init() {
        entityContext.setting().listenValue(SerialPortBaudRateSetting.class, "serial-baud-rate", () -> {
            if (this.rawSerialPortCommunicator != null) {
                this.reopen(true);
            }
        });
        entityContext.setting().listenValue(SerialPortFlowControlSetting.class, "serial-flow-control", () -> {
            if (this.rawSerialPortCommunicator != null) {
                this.reopen(true);
            }
        });
        entityContext.setting().listenValueAndGet(SerialPortSendEndLine.class, "serial-end-line", endLineType -> this.endLineType = endLineType);
        entityContext.setting().listenValue(SerialOpenPortSetting.class, "serial-open-port", this::reopen);
    }

    private void reopen(Boolean open) {
        if (this.rawSerialPortCommunicator != null) {
            this.rawSerialPortCommunicator.close();
            entityContext.ui().sendInfoMessage("SERIAL.PORT_CLOSED", FlowMap.of("PORT", this.rawSerialPortCommunicator.getSerialPort().getSystemPortName()));
            this.rawSerialPortCommunicator = null;
        }

        if (open) {
            SerialPort commPort = entityContext.setting().getValue(SerialPortSetting.class);
            if (commPort == null) {
                entityContext.ui().sendErrorMessage("SERIAL.NO_PORT", FlowMap.of("PORT",
                        defaultIfEmpty(entityContext.setting().getRawValue(SerialPortSetting.class), "-")));
                entityContext.setting().setValue(SerialOpenPortSetting.class, false);
                return;
            }
            try {
                this.openPort(commPort);
            } catch (Exception ex) {
                rawSerialPortCommunicator = null;
                entityContext.ui().sendErrorMessage("SERIAL.UNABLE_OPEN", FlowMap.of("PORT", commPort.getSystemPortName()), ex);
                entityContext.setting().setValue(SerialOpenPortSetting.class, false);
            }
        }
    }

    private void openPort(SerialPort commPort) {
        Integer baudRate = entityContext.setting().getValue(SerialPortBaudRateSetting.class);
        SerialPortFlowControlSetting.FlowControl flowControl = entityContext.setting().getValue(SerialPortFlowControlSetting.class);

        this.rawSerialPortCommunicator = new RawSerialPortCommunicator(commPort, entityContext, this);
        boolean opened = this.rawSerialPortCommunicator.open(baudRate, flowControl.getPortFlowControl());
        if (!opened) {
            throw new RuntimeException("Unable open port");
        } else {
            entityContext.ui().sendSuccessMessage("SERIAL.OPEN_SUCCESS", FlowMap.of("PORT", commPort.getSystemPortName()));
        }
    }

    @Override
    public Map<String, Class<? extends BundleSettingPlugin<?>>> getHeaderActions() {
        Map<String, Class<? extends BundleSettingPlugin<?>>> headerActions = new LinkedHashMap<>();

        headerActions.put("openPort", SerialOpenPortSetting.class);
        headerActions.put("port", SerialPortSetting.class);
        headerActions.put("baudRate", SerialPortBaudRateSetting.class);
        headerActions.put("flowControl", SerialPortFlowControlSetting.class);
        headerActions.put("endLine", SerialPortSendEndLine.class);

        return headerActions;
    }

    @Override
    @SneakyThrows
    public void commandReceived(String value) {
        if (rawSerialPortCommunicator != null) {
            ComplexString data = ComplexString.of(value, System.currentTimeMillis(), "#81A986", true);
            try {
                rawSerialPortCommunicator.getOutputStream().write((value + this.endLineType.getValue()).getBytes());
                rawSerialPortCommunicator.getBuffer().add(data);
            } catch (Exception ex) {
                rawSerialPortCommunicator.getBuffer().add(data.setColor(RED_COLOR));
                throw ex;
            }
        } else {
            SerialPort commPort = entityContext.setting().getValue(SerialPortSetting.class);
            throw new ToastrException("SERIAL.NO_OPEN_PORT", "PORT", commPort == null ? "-" : commPort.getSystemPortName());
        }
    }

    @Override
    public void dataReceived(ComplexString data) {
        entityContext.ui().sendNotification("-lines-serial", data.toString());
    }
}
