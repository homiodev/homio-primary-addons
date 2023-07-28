package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MEndpointOperationMode extends Z2MEndpoint {

    public Z2MEndpointOperationMode() {
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
