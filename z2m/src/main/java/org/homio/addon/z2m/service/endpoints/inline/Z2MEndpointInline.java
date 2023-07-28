package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.addon.z2m.service.Z2MEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.Nullable;

/**
 * Endpoints that creates in code programmatically. Endpoints that implement Z2MEndpoint must hav NoArgConstructor
 */
public abstract class Z2MEndpointInline extends Z2MEndpoint {

    public Z2MEndpointInline(Icon icon) {
        super(icon);
    }

    @Override
    public @Nullable String getEndpointDefinition() {
        return null;
    }
}
