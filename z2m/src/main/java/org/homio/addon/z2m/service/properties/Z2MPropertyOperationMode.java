package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MProperty;

public class Z2MPropertyOperationMode extends Z2MProperty {

    public Z2MPropertyOperationMode() {
        super("#2387B6", "fab fa-fw fa-monero");
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
