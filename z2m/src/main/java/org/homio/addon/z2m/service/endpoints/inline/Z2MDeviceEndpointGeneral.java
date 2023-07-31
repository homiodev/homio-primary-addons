package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.api.model.Icon;

public class Z2MDeviceEndpointGeneral extends Z2MDeviceEndpointInline {

    public Z2MDeviceEndpointGeneral(String icon, String color) {
        super(new Icon("fa fa-fw " + icon, color));
    }
}
