package org.homio.addon.z2m.service.properties.dynamic;

import org.homio.api.exception.ProhibitedExecution;
import org.homio.addon.z2m.service.Z2MProperty;
import org.homio.api.model.Icon;

public class Z2MDynamicProperty extends Z2MProperty {

    public Z2MDynamicProperty(String color, String icon) {
        super(new Icon("fa fa-fw " + icon, color));
    }

    @Override
    public String getPropertyDefinition() {
        throw new ProhibitedExecution();
    }
}
