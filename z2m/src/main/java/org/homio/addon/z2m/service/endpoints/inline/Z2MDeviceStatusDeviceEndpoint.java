package org.homio.addon.z2m.service.endpoints.inline;

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
 * Extra endpoint for every device to store device status
 */
public class Z2MDeviceStatusDeviceEndpoint extends Z2MDeviceEndpointInline {

    public Z2MDeviceStatusDeviceEndpoint(Z2MDeviceService deviceService) {
        super(new Icon("fa fa-fw fa-globe", "#42B52D"));
        setValue(new StringType(Status.UNKNOWN.name()), false);
        Options options = Options.dynamicEndpoint(ENDPOINT_DEVICE_STATUS, ApplianceModel.ENUM_TYPE);
        options.setValues(Stream.of(Status.values()).map(Enum::name).toList());
        init(deviceService, options);

        entityContext.event().addEventListener("zigbee-%s".formatted(deviceService.getIeeeAddress()),
            "z2m-endpoint", value -> setValue(new StringType(value.toString()), true));
    }

    @Override
    public void assembleUIAction(UIInputBuilder uiInputBuilder) {
        Status status = Status.valueOf(getValue().stringValue());
        uiInputBuilder.addInfo(status.name(), InfoType.Text).setColor(status.getColor());
        super.assembleUIAction(uiInputBuilder);
    }
}
