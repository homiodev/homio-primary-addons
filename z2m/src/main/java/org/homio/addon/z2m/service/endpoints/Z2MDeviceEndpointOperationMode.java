package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MDeviceEndpointOperationMode extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointOperationMode() {
        super(new Icon("fab fa-fw fa-monero", "#2387B6"));
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
