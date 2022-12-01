package org.touchhome.bundle.zigbee.setting;

import org.touchhome.bundle.api.setting.SettingPluginBoolean;

public class ZigBeeShowMissConfiguredClustersSetting implements SettingPluginBoolean {

  @Override
  public boolean defaultValue() {
    return true;
  }

  @Override
  public int order() {
    return 50;
  }
}
