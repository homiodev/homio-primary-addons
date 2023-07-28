package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.api.model.Icon;

public class Z2MEndpointGeneral extends Z2MEndpointInline {

    public Z2MEndpointGeneral(String icon, String color) {
        super(new Icon("fa fa-fw " + icon, color));
    }
}
