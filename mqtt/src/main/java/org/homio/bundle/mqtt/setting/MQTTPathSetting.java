package org.homio.bundle.mqtt.setting;

import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.setting.SettingPluginText;

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
