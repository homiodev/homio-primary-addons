package org.homio.addon.z2m.service.properties.dynamic;

import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.Z2MDevicePropertiesModel;
import org.homio.api.state.DecimalType;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 'action' property contains another event(usually enum) and we want to create separate property for it
 */
public class Z2MPropertyActionEvent extends Z2MDynamicProperty {

    public Z2MPropertyActionEvent(
        @NotNull Z2MDeviceService deviceService,
        @NotNull String property,
        @Nullable Z2MDevicePropertiesModel z2MDevicePropertiesModel) {
        super(
            z2MDevicePropertiesModel == null ? "#B72AD4" : z2MDevicePropertiesModel.getIconColor(),
            z2MDevicePropertiesModel == null ? "fa-square-check" : z2MDevicePropertiesModel.getIcon());
        init(deviceService, Options.dynamicExpose(property, ApplianceModel.NUMBER_TYPE));
        setValue(new DecimalType(0));
        dataReader = jsonObject -> new DecimalType(getValue().intValue() + 1);
    }

    @Override
    public String getPropertyDefinition() {
        return "action_event";
    }
}
