package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;

public class Z2MPropertyState extends Z2MProperty {

    public Z2MPropertyState() {
        super(new Icon("fas fa-fw fa-star-half-alt", "#B3EF57"));
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
