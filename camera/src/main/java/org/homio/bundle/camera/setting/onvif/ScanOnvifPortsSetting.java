package org.homio.bundle.camera.setting.onvif;

import org.homio.bundle.api.setting.SettingPluginIntegerSet;

public class ScanOnvifPortsSetting implements SettingPluginIntegerSet {

  @Override
  public int order() {
    return 300;
  }

  @Override
  public int[] defaultValue() {
    return new int[]{8000};
  }

  @Override
  public String group() {
    return "scan_onvif_http";
  }
}
