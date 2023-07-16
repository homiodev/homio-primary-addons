package org.homio.addon.bluetooth;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.entity.HasStatusAndMsg;
import org.homio.api.entity.types.CommunicationEntity;
import org.homio.api.ui.UISidebarChildren;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarChildren(icon = "fab fa-bluetooth", color = "#0088CC", allowCreateItem = false)
public class BluetoothEntity extends CommunicationEntity<BluetoothEntity> implements HasStatusAndMsg<BluetoothEntity> {

    public static final String PREFIX = "ble_";
    public static final String DEFAULT_BLUETOOTH_ENTITY_ID = PREFIX + "primary";

    @Override
    public String getDefaultName() {
        return "Bluetooth";
    }

    @Override
    public @NotNull String getEntityPrefix() {
        return PREFIX;
    }
}
