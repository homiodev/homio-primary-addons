package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MDeviceEndpointOperationMode extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointOperationMode(@NotNull EntityContext entityContext) {
        super(new Icon("fab fa-monero", "#2387B6"), entityContext);
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
