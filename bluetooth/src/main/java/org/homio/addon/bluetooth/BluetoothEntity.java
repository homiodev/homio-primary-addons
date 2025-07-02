package org.homio.addon.bluetooth;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.entity.HasStatusAndMsg;
import org.homio.api.entity.device.DeviceBaseEntity;
import org.homio.api.ui.route.UIRouteCommunication;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UIRouteCommunication(icon = "fab fa-bluetooth", color = "#0088CC", allowCreateItem = false)
public class BluetoothEntity extends DeviceBaseEntity implements HasStatusAndMsg {

  @Override
  public String getDefaultName() {
    return "Bluetooth";
  }

  @Override
  protected @NotNull String getDevicePrefix() {
    return "ble";
  }
}
