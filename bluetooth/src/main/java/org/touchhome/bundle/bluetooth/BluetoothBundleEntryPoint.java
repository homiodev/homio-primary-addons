package org.touchhome.bundle.bluetooth;

import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.setting.SettingPluginStatus;
import org.touchhome.bundle.bluetooth.setting.BluetoothStatusSetting;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

@Log4j2
@Controller
public class BluetoothBundleEntryPoint extends BaseBluetoothCharacteristicService implements BundleEntryPoint {

  private final EntityContext entityContext;

  public BluetoothBundleEntryPoint(EntityContext entityContext, MachineHardwareRepository machineHardwareRepository, NetworkHardwareRepository networkHardwareRepository) {
    super(machineHardwareRepository, networkHardwareRepository);
    this.entityContext = entityContext;
  }

  @Override
  public Class<? extends SettingPluginStatus> getBundleStatusSetting() {
    return BluetoothStatusSetting.class;
  }

  @Override
  public int order() {
    return Integer.MAX_VALUE;
  }

  @Override
  public String readServerConnected() {
    return entityContext.setting().getValue(ConsoleCloudProviderSetting.class).getStatus();
  }

  @Override
  public String getFeatures() {
    return entityContext.getDeviceFeatures().entrySet().stream().map(es -> es.getKey() + "~~~" + es.getValue()).collect(Collectors.joining(";"));
  }

  /**
   * We may set password only once. If user wants update password, he need pass old password hash
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
  public void updateBluetoothStatus(String status) {
    entityContext.setting().setValue(BluetoothStatusSetting.class, new SettingPluginStatus.BundleStatusInfo(Status.valueOf(status), null));
  }

  @Override
  public void setFeatureState(boolean status) {
    entityContext.setFeatureState("Bluetooth", status);
  }
}
