package org.homio.addon.bluetooth;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.homio.api.entity.HasStatusAndMsg;
import org.homio.api.entity.types.CommunicationEntity;
import org.homio.api.ui.UISidebarChildren;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@UISidebarChildren(icon = "fab fa-bluetooth", color = "#0088CC", allowCreateItem = false)
public class BluetoothEntity extends CommunicationEntity implements HasStatusAndMsg {

    @Override
    public String getDefaultName() {
        return "Bluetooth";
    }

    @Override
    protected void assembleMissingMandatoryFields(@NotNull Set<String> fields) {

    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "ble";
    }
}
