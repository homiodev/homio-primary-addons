package org.homio.addon.z2m.service.properties.inline;

import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.addon.z2m.util.Z2MPropertyModel;
import org.homio.api.model.Icon;
import org.homio.api.state.DecimalType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 'action' property contains another event(usually enum) and we want to create separate property for it
 */
public class Z2MEndpointActionEvent extends Z2MEndpointInline {

    public Z2MEndpointActionEvent(
        @NotNull Z2MDeviceService deviceService,
        @NotNull String property,
        @Nullable Z2MPropertyModel z2MPropertyModel) {
        super(new Icon(
            "fas fa-fw " + (z2MPropertyModel == null ? "fa-square-check" : z2MPropertyModel.getIcon()),
            z2MPropertyModel == null ? "#B72AD4" : z2MPropertyModel.getIconColor()
        ));
        init(deviceService, Options.dynamicExpose(property, ApplianceModel.NUMBER_TYPE), true);
        setValue(new DecimalType(0));
        setDataReader(jsonObject -> new DecimalType(getValue().intValue() + 1));
    }

    @Override
    public String getPropertyDefinition() {
        return "action_event";
    }
}
