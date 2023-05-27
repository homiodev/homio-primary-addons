package org.homio.addon.bluetooth;

import lombok.extern.log4j.Log4j2;
import org.homio.api.AddonEntrypoint;
import org.homio.api.EntityContext;
import org.homio.api.model.Status;
import org.homio.hquery.hardware.network.NetworkHardwareRepository;
import org.homio.hquery.hardware.other.MachineHardwareRepository;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class BluetoothEntrypoint extends BaseBluetoothCharacteristicService implements AddonEntrypoint {

    private final EntityContext entityContext;
    private Status status = Status.UNKNOWN;
    private String errorMessage;

    public BluetoothEntrypoint(EntityContext entityContext, MachineHardwareRepository machineHardwareRepository,
        NetworkHardwareRepository networkHardwareRepository) {
        super(machineHardwareRepository, networkHardwareRepository);
        this.entityContext = entityContext;
        updateNotificationBlock();
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void updateBluetoothStatus(String status, String message) {
        this.status = status.startsWith("ERROR") ? Status.ERROR : Status.valueOf(status);
        this.errorMessage = message;
        updateNotificationBlock();
    }

    public void updateNotificationBlock() {
        entityContext.ui().addNotificationBlock("ble", "Bluetooth", "fab fa-bluetooth", "#0088CC", builder -> {
            builder.setStatus(this.status);
            builder.setStatusMessage(this.errorMessage);
        });
    }
}
