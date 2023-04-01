package org.homio.bundle.z2m.service.properties.dynamic;

import org.homio.bundle.api.state.DecimalType;
import org.homio.bundle.z2m.service.Z2MDeviceService;
import org.homio.bundle.z2m.util.Z2MDeviceDTO;
import org.homio.bundle.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.homio.bundle.z2m.util.Z2MDevicePropertiesDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 'action' property contains another event(usually enum) and we want to create separate property for it
 */
public class Z2MPropertyActionEvent extends Z2MDynamicProperty {

    public Z2MPropertyActionEvent(
        @NotNull Z2MDeviceService deviceService,
        @NotNull String property,
        @Nullable Z2MDevicePropertiesDTO z2MDevicePropertiesDTO) {
        super(
            z2MDevicePropertiesDTO == null ? "#B72AD4" : z2MDevicePropertiesDTO.getIconColor(),
            z2MDevicePropertiesDTO == null ? "fa-square-check" : z2MDevicePropertiesDTO.getIcon());
        init(deviceService, Options.dynamicExpose(property, Z2MDeviceDTO.NUMBER_TYPE));
        setValue(new DecimalType(0));
        dataReader = jsonObject -> new DecimalType(getValue().intValue() + 1);
    }

    @Override
    public String getPropertyDefinition() {
        return "action_event";
    }
}
