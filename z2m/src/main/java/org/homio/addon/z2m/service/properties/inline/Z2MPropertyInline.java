package org.homio.addon.z2m.service.properties.inline;

import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.Nullable;

/**
 * Properties that creates in code programmatically. Properties that implement Z2MProperty must hav NoArgConstructor
 */
public abstract class Z2MPropertyInline extends Z2MProperty {

    public Z2MPropertyInline(Icon icon) {
        super(icon);
    }

    @Override
    public @Nullable String getPropertyDefinition() {
        return null;
    }
}
