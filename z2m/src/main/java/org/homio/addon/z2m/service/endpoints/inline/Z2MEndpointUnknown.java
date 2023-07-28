package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.api.model.Icon;

/**
 * This is a endpoint that wasn't found in org.homio.addon.zigbee.service.z2m.endpoints.xxx and in file zigbee-devices.json...endpoints
 */
public class Z2MEndpointUnknown extends Z2MEndpointInline {

    public Z2MEndpointUnknown() {
        super(new Icon("fa-question", "#B72AD4"));
    }
}
