package org.touchhome.bundle.zigbee.service.z2m.properties;

import static org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.NUMBER_TYPE;

import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;
import org.touchhome.bundle.zigbee.service.z2m.properties.dynamic.Z2MDynamicProperty;
import org.touchhome.bundle.zigbee.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;

/**
 * Extra property for every device to allow to create variable to store last device received event
 */
public class Z2MPropertyLastUpdate extends Z2MDynamicProperty {

    public static final String KEY = "updated";

    public Z2MPropertyLastUpdate(Z2MDeviceService deviceService) {
        super("#BA5623", "fa fa-fw fa-clock");
        setValue(new DecimalType(System.currentTimeMillis()));
        init(deviceService, Options.dynamicExpose(KEY, NUMBER_TYPE));
        dataReader = jsonObject -> new DecimalType(System.currentTimeMillis());
    }
}
