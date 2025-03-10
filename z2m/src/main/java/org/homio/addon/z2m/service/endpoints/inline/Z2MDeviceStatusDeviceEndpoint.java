package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.addon.z2m.service.Z2MDeviceService;
import org.homio.addon.z2m.util.ApplianceModel;
import org.homio.addon.z2m.util.ApplianceModel.Z2MDeviceDefinition.Options;
import org.homio.api.model.Icon;
import org.homio.api.model.Status;
import org.homio.api.state.StringType;

import static org.homio.api.model.Status.ERROR;
import static org.homio.api.model.Status.OFFLINE;
import static org.homio.api.model.Status.ONLINE;
import static org.homio.api.model.Status.UNKNOWN;

/**
 * Extra endpoint for every device to store device status
 */
public class Z2MDeviceStatusDeviceEndpoint extends Z2MDeviceEndpointInline {

  public Z2MDeviceStatusDeviceEndpoint(Z2MDeviceService deviceService) {
    super(new Icon("fa fa-globe", "#42B52D"), deviceService.context());
    Options options = Options.dynamicEndpoint(ENDPOINT_DEVICE_STATUS, ApplianceModel.ENUM_TYPE);
    options.setValues(Status.set(UNKNOWN, ONLINE, OFFLINE, UNKNOWN, ERROR));
    init(deviceService, options);

    context().event().addEventListener("zigbee-%s".formatted(deviceService.getIeeeAddress()),
      "z2m-endpoint", value -> setValue(new StringType(value.toString()), true));
  }
}
