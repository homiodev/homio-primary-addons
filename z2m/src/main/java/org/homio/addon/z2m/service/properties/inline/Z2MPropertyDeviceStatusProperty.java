package org.homio.addon.z2m.service.properties.inline;

import java.util.stream.Stream;
import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.state.StringType;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.field.action.v1.item.UIInfoItemBuilder.InfoType;

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

        deviceService.getEntityContext().event().addEventListener("zigbee-%s".formatted(deviceService.getIeeeAddress()),
            "z2m-prop", value -> {
                setValue(new StringType(value.toString()));
                updateUI();
                pushVariable();
            });
    }

    @Override
    public void buildZigbeeAction(UIInputBuilder uiInputBuilder, String entityID) {
        Status status = Status.valueOf(getValue().stringValue());
        uiInputBuilder.addInfo(status.name(), InfoType.Text).setColor(status.getColor());
        super.buildZigbeeAction(uiInputBuilder, entityID);
    }
}
