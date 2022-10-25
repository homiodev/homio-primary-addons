package org.touchhome.bundle.cloud.netty.setting;

import org.touchhome.bundle.api.setting.SettingPluginText;

public class CloudServerUrlSetting implements SettingPluginText {

  @Override
  public String getDefaultValue() {
    return "https://touchhome.org";
  }

  @Override
  public int order() {
    return 40;
  }
}
