package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MEndpointState extends Z2MEndpoint {

    public Z2MEndpointState() {
        super(new Icon("fas fa-fw fa-star-half-alt", "#B3EF57"));
    }

    @Override
    public @NotNull String getEndpointDefinition() {
        return "state";
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }
}
