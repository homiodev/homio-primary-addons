package org.touchhome.bundle.camera.setting.onvif;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class ScanOnvifHttpDefaultPasswordAuthSetting implements SettingPluginText {

  @Override
  public String getDefaultValue() {
    return "";
  }

  @Override
  public int order() {
    return 320;
  }

  @Override
  public boolean isAdvanced() {
    return true;
  }

  @Override
  public String group() {
    return "scan_onvif_http";
  }
}
