package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.Nullable;

/**
 * Endpoints that creates in code programmatically. Endpoints that implement Z2MDeviceEndpoint must hav NoArgConstructor
 */
public abstract class Z2MDeviceEndpointInline extends Z2MDeviceEndpoint {

    public Z2MDeviceEndpointInline(Icon icon) {
        super(icon);
    }

    @Override
    public @Nullable String getEndpointDefinition() {
        return null;
    }
}
