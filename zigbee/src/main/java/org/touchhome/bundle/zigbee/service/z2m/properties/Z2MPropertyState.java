package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyState extends Z2MProperty {

    public Z2MPropertyState() {
        super("#B3EF57", "fas fa-fw fa-star-half-alt");
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }

    @Override
    public String getPropertyDefinition() {
        return "state";
    }
}
