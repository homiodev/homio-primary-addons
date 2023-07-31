package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.model.Icon;
import org.homio.api.model.device.ConfigDeviceEndpoint;
import org.homio.api.state.DecimalType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 'action' endpoint contains another event(usually enum) and we want to create separate endpoint for it
 */
public class Z2MDeviceEndpointActionEvent extends Z2MDeviceEndpointInline {

    public Z2MDeviceEndpointActionEvent(
            @NotNull Z2MDeviceService deviceService,
            @NotNull String endpoint,
            @Nullable ConfigDeviceEndpoint configDeviceEndpoint) {
        super(new Icon(
                "fas fa-fw " + (configDeviceEndpoint == null ? "fa-square-check" : configDeviceEndpoint.getIcon()),
                configDeviceEndpoint == null ? "#B72AD4" : configDeviceEndpoint.getIconColor()
        ));
        init(deviceService, Options.dynamicEndpoint(endpoint, ApplianceModel.NUMBER_TYPE), true);
        setValue(new DecimalType(0));
        setDataReader(jsonObject -> new DecimalType(getValue().intValue() + 1));
    }

    @Override
    public String getEndpointDefinition() {
        return "action_event";
    }
}
