package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

/**
 * This is a endpoint that wasn't found in org.homio.addon.zigbee.service.z2m.endpoints.xxx and in file zigbee-devices.json...endpoints
 */
public class Z2MDeviceEndpointUnknown extends Z2MDeviceEndpointInline {

    public Z2MDeviceEndpointUnknown(@NotNull Context context) {
        super(new Icon("fa-question", "#B72AD4"), context);
    }
}
