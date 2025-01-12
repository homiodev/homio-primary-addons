package org.homio.addon.z2m.service.endpoints;

import org.homio.addon.z2m.service.Z2MDeviceEndpoint;
import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;

public class Z2MDeviceEndpointState extends Z2MDeviceEndpoint {

  public Z2MDeviceEndpointState(@NotNull Context context) {
    super(new Icon("fas fa-star-half-alt", "#B3EF57"), context);
  }

  @Override
  public @NotNull String getEndpointDefinition() {
    return "state";
  }

  @Override
  protected String getJsonKey() {
    return getExpose().getProperty();
  }
}
