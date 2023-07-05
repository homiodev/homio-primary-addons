package org.homio.addon.z2m.service.properties.inline;

import static java.lang.String.format;

import java.util.stream.Stream;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.state.StringType;

/**
 * Extra property for every device to store device status
 */
public class Z2MPropertyDeviceStatusProperty extends Z2MPropertyInline {

    public Z2MPropertyDeviceStatusProperty(Z2MDeviceService deviceService) {
        super(new Icon("fa fa-fw fa-globe", "#42B52D"));
        setValue(new StringType(Status.UNKNOWN.name()));
        Options options = Options.dynamicExpose(PROPERTY_DEVICE_STATUS, ApplianceModel.ENUM_TYPE);
        options.setValues(Stream.of(Status.values()).map(Enum::name).toList());
        init(deviceService, options, true);

        deviceService.getEntityContext().event().addEventListener(format("zigbee-%s", deviceService.getIeeeAddress()),
            "z2m-prop", value -> {
                setValue(new StringType(value.toString()));
                updateUI();
                pushVariable();
            });
    }
}
