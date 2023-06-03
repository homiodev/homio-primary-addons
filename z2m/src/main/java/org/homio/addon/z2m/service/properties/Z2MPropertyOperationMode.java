package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;

public class Z2MPropertyOperationMode extends Z2MProperty {

    public Z2MPropertyOperationMode() {
        super(new Icon("fab fa-fw fa-monero", "#2387B6"));
    }

    @Override
    public String getPropertyDefinition() {
        return "operation_mode";
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }
}
