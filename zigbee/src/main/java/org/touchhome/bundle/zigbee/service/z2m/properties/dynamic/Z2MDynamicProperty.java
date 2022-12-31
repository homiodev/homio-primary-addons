package org.touchhome.bundle.zigbee.service.z2m.properties.dynamic;

import org.touchhome.bundle.api.exception.ProhibitedExecution;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MDynamicProperty extends Z2MProperty {

    public Z2MDynamicProperty(String color, String icon) {
        super(color, "fa fa-fw " + icon);
    }

    @Override
    public String getPropertyDefinition() {
        throw new ProhibitedExecution();
    }
}
