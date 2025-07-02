package org.homio.addon.z2m.setting;

import org.homio.api.Context;
import org.homio.api.entity.zigbee.ZigBeeDeviceBaseEntity;
import org.homio.api.model.Icon;
import org.homio.api.setting.SettingPluginToggle;
import org.jetbrains.annotations.NotNull;

public class ZigBeeEntityCompactModeSetting implements SettingPluginToggle {

  @Override
  public String availableForRoute() {
    return ZigBeeDeviceBaseEntity.class.getSimpleName();
  }

  @Override
  public int order() {
    return 20;
  }

  @Override
  public @NotNull Icon getIcon() {
    return new Icon("fas fa-minimize");
  }

  @Override
  public @NotNull Icon getToggleIcon() {
    return new Icon("fas fa-maximize");
  }

  @Override
  public boolean isVisible(Context context) {
    return false;
  }
}
