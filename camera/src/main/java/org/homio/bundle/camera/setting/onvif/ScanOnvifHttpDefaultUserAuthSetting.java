package org.homio.bundle.camera.setting.onvif;

import org.homio.bundle.api.setting.SettingPluginText;

public class ScanOnvifHttpDefaultUserAuthSetting implements SettingPluginText {

  @Override
  public String getDefaultValue() {
    return "admin";
  }

  @Override
  public int order() {
    return 310;
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
