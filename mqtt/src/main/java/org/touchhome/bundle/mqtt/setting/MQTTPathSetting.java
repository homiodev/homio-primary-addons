package org.touchhome.bundle.mqtt.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.SettingPluginText;

public class MQTTPathSetting implements SettingPluginText {

  @Override
  public int order() {
    return 0;
  }

  @Override
  public boolean isDisabled(EntityContext entityContext) {
    return true;
  }

  @Override
  public boolean isVisible(EntityContext entityContext) {
    return false;
  }
}
