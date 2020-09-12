package org.touchhome.bundle.arduino.provider.usb;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;
import org.touchhome.bundle.arduino.provider.ArduinoCommandPlugins;
import org.touchhome.bundle.arduino.provider.communication.ArduinoCommunicationProvider;
import org.touchhome.bundle.arduino.setting.ArduinoUsbPortSetting;

@Log4j2
@Component
public class ArduinoUsbCommunicatorProvider extends ArduinoCommunicationProvider<Void> {

    private final ArduinoUsbInputOutputStream stream;
    private String systemPortName;

    public ArduinoUsbCommunicatorProvider(ArduinoCommandPlugins arduinoCommandPlugins, ArduinoUsbInputOutputStream stream, EntityContext entityContext) {
        super(entityContext, arduinoCommandPlugins, stream, stream, true);
        this.stream = stream;
    }

    @Override
    protected void onCommunicationError() {
        this.systemPortName = null;
    }

    @Override
    protected Void createParameter(ArduinoDeviceEntity arduinoDeviceEntity) {
        return null;
    }

    @Override
    public long onRegistrationSuccess(ArduinoDeviceEntity entity) {
        return 0;
    }

    @Override
    protected boolean beforeStart() {
        SerialPort port = this.entityContext.getSettingValue(ArduinoUsbPortSetting.class);
        if (port != null && !port.getSystemPortName().equals(this.systemPortName)) {
            this.systemPortName = port.getSystemPortName();
            return this.stream.initialize(port);
        }
        return false;
    }
}
