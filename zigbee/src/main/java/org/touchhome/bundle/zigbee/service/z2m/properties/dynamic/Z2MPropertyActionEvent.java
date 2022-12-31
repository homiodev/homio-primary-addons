package org.touchhome.bundle.zigbee.service.z2m.properties.dynamic;

import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.NUMBER_TYPE;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.touchhome.bundle.zigbee.util.Z2MPropertyDTO;

/**
 * 'action' property contains another event(usually enum) and we want to create separate property for it
 */
public class Z2MPropertyActionEvent extends Z2MDynamicProperty {

    public Z2MPropertyActionEvent(
            @NotNull Z2MDeviceService deviceService,
            @NotNull String property,
            @Nullable Z2MPropertyDTO z2MPropertyDTO) {
        super(
                z2MPropertyDTO == null ? "#B72AD4" : z2MPropertyDTO.getIconColor(),
                z2MPropertyDTO == null ? "fa-square-check" : z2MPropertyDTO.getIcon());
        init(deviceService, Options.dynamicExpose("action_" + property, NUMBER_TYPE));
        setValue(new DecimalType(0));
        dataReader = jsonObject -> new DecimalType(getValue().intValue() + 1);
    }
}
