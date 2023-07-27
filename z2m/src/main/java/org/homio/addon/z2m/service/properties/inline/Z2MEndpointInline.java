package org.homio.addon.z2m.service.properties.inline;

import org.homio.addon.z2m.service.Z2MEndpoint;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.Nullable;

/**
 * Properties that creates in code programmatically. Properties that implement Z2MProperty must hav NoArgConstructor
 */
public abstract class Z2MEndpointInline extends Z2MEndpoint {

    public Z2MEndpointInline(Icon icon) {
        super(icon);
    }

    @Override
    public @Nullable String getPropertyDefinition() {
        return null;
    }
}
