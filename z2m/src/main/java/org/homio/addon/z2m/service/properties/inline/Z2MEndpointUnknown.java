package org.homio.addon.z2m.service.properties.inline;

import org.homio.api.model.Icon;

/**
 * This is a property that wasn't found in org.homio.addon.zigbee.service.z2m.properties.xxx and in file zigbee-devices.json...properties
 */
public class Z2MEndpointUnknown extends Z2MEndpointInline {

    public Z2MEndpointUnknown() {
        super(new Icon("fa-question", "#B72AD4"));
    }
}
