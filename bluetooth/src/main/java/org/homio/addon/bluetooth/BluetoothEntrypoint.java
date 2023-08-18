package org.homio.addon.bluetooth;

import lombok.extern.log4j.Log4j2;
import org.homio.api.AddonEntrypoint;
import org.homio.api.EntityContext;
import org.homio.hquery.hardware.network.NetworkHardwareRepository;
import org.homio.hquery.hardware.other.MachineHardwareRepository;
import org.springframework.stereotype.Controller;

/**
 * Only one ble device is support for now
 */
@Log4j2
@Controller
public class BluetoothEntrypoint implements AddonEntrypoint {

    public BluetoothEntrypoint(EntityContext entityContext, MachineHardwareRepository machineHardwareRepository,
                               NetworkHardwareRepository networkHardwareRepository) {
        //  super(machineHardwareRepository, networkHardwareRepository, entityContext);
    }

    @Override
    public void init() {
        /*entityContext.ui().addNotificationBlockOptional("BLE", "Bluetooth", new Icon("fab fa-bluetooth", "#0088CC"));
        entity = entityContext.getEntity(DEFAULT_BLUETOOTH_ENTITY_ID);
        if (entity == null) {
            entity = entityContext.save(new BluetoothEntity().setEntityID(DEFAULT_BLUETOOTH_ENTITY_ID));
        }
        super.init();*/
    }
}
