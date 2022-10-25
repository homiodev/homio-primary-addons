package org.touchhome.bundle.camera.setting.rtsp;

import org.touchhome.bundle.api.setting.SettingPluginIntegerSet;

public class ScanRtspPortsSetting implements SettingPluginIntegerSet {

  @Override
  public int order() {
    return 110;
  }

  @Override
  public int[] defaultValue() {
    return new int[]{554, 8554};
  }

  @Override
  public boolean isAdvanced() {
    return true;
  }

  @Override
  public String group() {
    return "scan_rtsp";
  }
}
