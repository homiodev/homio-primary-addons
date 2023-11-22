package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MDeviceEndpointOperationMode extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointOperationMode(@NotNull Context context) {
        super(new Icon("fab fa-monero", "#2387B6"), context);
    }

    @Override
    public @NotNull String getEndpointDefinition() {
        return "operation_mode";
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }
}
