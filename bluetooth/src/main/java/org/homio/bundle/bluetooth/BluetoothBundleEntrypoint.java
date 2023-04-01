package org.homio.bundle.bluetooth;

import lombok.extern.log4j.Log4j2;
import org.homio.bundle.api.BundleEntrypoint;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.model.Status;
import org.homio.bundle.hquery.hardware.network.NetworkHardwareRepository;
import org.homio.bundle.hquery.hardware.other.MachineHardwareRepository;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class BluetoothBundleEntrypoint extends BaseBluetoothCharacteristicService implements BundleEntrypoint {

    private final EntityContext entityContext;
    private Status status = Status.UNKNOWN;
    private String errorMessage;

    public BluetoothBundleEntrypoint(EntityContext entityContext, MachineHardwareRepository machineHardwareRepository,
        NetworkHardwareRepository networkHardwareRepository) {
        super(machineHardwareRepository, networkHardwareRepository);
        this.entityContext = entityContext;
        updateNotificationBlock();
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    /**
     * We may set password only once. If user wants update password, he needs pass old password hash
     *//*
     TODO: need rewrite logic
    @Override
    public void writePwd(String loginUser, String pwd, String prevPwd) {
        UserEntity user = getUser();
        if (user.isPasswordNotSet(passwordEncoder)) {
            log.warn("Set primary admin password for user: <{}>", getLoginUser());
            entityContext.save(user.setUserId(getLoginUser()).setPassword(pwd, passwordEncoder));
            this.entityContext.setting().setValue(CloudServerRestartSetting.class, null);
        } else if (Objects.equals(user.getUserId(), getLoginUser()) &&
                user.matchPassword(prevPwd, passwordEncoder)) {
            log.warn("Reset primary admin password for user: <{}>", getLoginUser());
            entityContext.save(user.setPassword(pwd, passwordEncoder));
            this.entityContext.setting().setValue(CloudServerRestartSetting.class, null);
        }

        if (user.matchPassword(pwd, passwordEncoder)) {
            setTimeSinceLastCheckPassword(System.currentTimeMillis());
        }
    }*/
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
