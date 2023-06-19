package org.homio.addon.z2m.service.properties;

import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.service.properties.dynamic.Z2MDynamicProperty;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.field.action.v1.UIInputBuilder;

/**
 * Extra property for every device to allow to create variable to store last device received event
 */
public class Z2MPropertyLastUpdate extends Z2MDynamicProperty {

    public Z2MPropertyLastUpdate(Z2MDeviceService deviceService) {
        super("#BA5623", "fa fa-fw fa-clock");
        setValue(new DecimalType(System.currentTimeMillis()));
        init(deviceService, Options.dynamicExpose(PROPERTY_UPDATED, ApplianceModel.NUMBER_TYPE));
        dataReader = jsonObject -> new DecimalType(System.currentTimeMillis());
    }

    @Override
    public void buildZigbeeAction(UIInputBuilder uiInputBuilder, String entityID) {
        uiInputBuilder.addDuration(getValue().longValue(), null);
    }
}
