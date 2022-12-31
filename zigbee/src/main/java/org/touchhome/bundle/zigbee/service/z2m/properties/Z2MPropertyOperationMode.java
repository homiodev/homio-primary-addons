package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyOperationMode extends Z2MProperty {

    public Z2MPropertyOperationMode() {
        super("#2387B6", "fab fa-fw fa-monero");
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }

    @Override
    public String getPropertyDefinition() {
        return "operation_mode";
    }
}
