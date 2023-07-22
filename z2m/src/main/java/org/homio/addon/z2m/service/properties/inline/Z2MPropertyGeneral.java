package org.homio.addon.z2m.service.properties.inline;

import org.homio.api.model.Icon;

public class Z2MPropertyGeneral extends Z2MPropertyInline {

    public Z2MPropertyGeneral(String icon, String color) {
        super(new Icon("fa fa-fw " + icon, color));
    }
}
