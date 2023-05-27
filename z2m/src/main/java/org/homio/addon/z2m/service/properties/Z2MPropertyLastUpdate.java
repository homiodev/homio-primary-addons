package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.properties.dynamic.Z2MDynamicProperty;
import org.homio.addon.z2m.util.Z2MDeviceDTO;
import org.homio.addon.z2m.util.Z2MDeviceDTO.Z2MDeviceDefinition.Options;
import org.homio.api.state.DecimalType;
import org.homio.addon.z2m.service.Z2MDeviceService;

/**
 * Extra property for every device to allow to create variable to store last device received event
 */
public class Z2MPropertyLastUpdate extends Z2MDynamicProperty {

    public static final String UPDATED = "updated";

    public Z2MPropertyLastUpdate(Z2MDeviceService deviceService) {
        super("#BA5623", "fa fa-fw fa-clock");
        setValue(new DecimalType(System.currentTimeMillis()));
        init(deviceService, Options.dynamicExpose(UPDATED, Z2MDeviceDTO.NUMBER_TYPE));
        dataReader = jsonObject -> new DecimalType(System.currentTimeMillis());
    }
}
