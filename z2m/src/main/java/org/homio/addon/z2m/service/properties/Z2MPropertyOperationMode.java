package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MPropertyOperationMode extends Z2MProperty {

    public Z2MPropertyOperationMode() {
        super(new Icon("fab fa-fw fa-monero", "#2387B6"));
    }

    @Override
    public @NotNull String getPropertyDefinition() {
        return "operation_mode";
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }
}
