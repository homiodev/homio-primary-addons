package org.homio.addon.bluetooth;

import lombok.extern.log4j.Log4j2;
import org.homio.api.AddonEntrypoint;
import org.springframework.stereotype.Controller;

/**
 * Only one ble device is support for now
 */
@Log4j2
@Controller
public class BluetoothEntrypoint implements AddonEntrypoint {

    @Override
    public void init() {

    }
}
