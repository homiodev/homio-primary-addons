package org.touchhome.bundle.bluetooth;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.bluetooth.setting.BluetoothStatusSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerRestartSetting;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.touchhome.bundle.api.entity.UserEntity.ADMIN_USER;

@Log4j2
@Controller
public class BluetoothBundleEntryPoint extends BaseBluetoothCharacteristicService implements BundleEntryPoint {

    private final EntityContext entityContext;
    private final PasswordEncoder passwordEncoder;

    public BluetoothBundleEntryPoint(EntityContext entityContext, PasswordEncoder passwordEncoder, MachineHardwareRepository machineHardwareRepository, NetworkHardwareRepository networkHardwareRepository) {
        super(machineHardwareRepository, networkHardwareRepository);
        this.entityContext = entityContext;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Class<? extends SettingPluginStatus> getBundleStatusSetting() {
        return BluetoothStatusSetting.class;
    }

    private UserEntity getUser() {
        return entityContext.getEntity(ADMIN_USER);
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String readPwdSet() {
        if (getUser().isPasswordNotSet(passwordEncoder)) {
            return "none";
        } else if (System.currentTimeMillis() - getTimeSinceLastCheckPassword() > TIME_REFRESH_PASSWORD) {
            return "required";
        }
        return "ok:" + readTimeToReleaseSession();
    }

    @Override
    public String readServerConnected() {
        return entityContext.setting().getValue(ConsoleCloudProviderSetting.class).getStatus();
    }

    @Override
    public String getFeatures() {
        return entityContext.getDeviceFeatures().entrySet().stream().map(es -> es.getKey() + "~~~" + es.getValue()).collect(Collectors.joining(";"));
    }

    @Override
    public String getKeystore() {
        UserEntity user = getUser();
        return String.valueOf(user.getKeystoreDate() == null ? "" : user.getKeystoreDate().getTime());
    }

    /**
     * We may set password only once. If user wants update password, he need pass old password hash
     */
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
    }

    @Override
    public boolean hasExtraAccess() {
        return false;
    }

    @Override
    public void updateBluetoothStatus(String status) {

    }

    @Override
    public void setFeatureState(boolean status) {

    }
}
