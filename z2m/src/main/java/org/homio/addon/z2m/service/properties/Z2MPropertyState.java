package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MProperty;

public class Z2MPropertyState extends Z2MProperty {

    public Z2MPropertyState() {
        super("#B3EF57", "fas fa-fw fa-star-half-alt");
    }

    @Override
    public String getPropertyDefinition() {
        return "state";
    }

    @Override
    protected String getJsonKey() {
        return getExpose().getProperty();
    }
}
