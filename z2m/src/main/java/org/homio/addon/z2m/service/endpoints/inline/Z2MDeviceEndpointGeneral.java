package org.homio.addon.z2m.service.endpoints.inline;

import org.homio.api.Context;
import org.homio.api.model.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Z2MDeviceEndpointGeneral extends Z2MDeviceEndpointInline {

  public Z2MDeviceEndpointGeneral(@NotNull String icon, @Nullable String color, @NotNull Context context) {
    super(new Icon("fa " + icon, color), context);
  }
}
