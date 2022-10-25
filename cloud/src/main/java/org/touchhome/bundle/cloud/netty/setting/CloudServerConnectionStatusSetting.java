package org.touchhome.bundle.cloud.netty.setting;

import org.apache.commons.lang3.StringUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.setting.SettingPlugin;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;

public class CloudServerConnectionStatusSetting implements SettingPlugin<ServerConnectionStatus> {

  @Override
  public Class<ServerConnectionStatus> getType() {
    return ServerConnectionStatus.class;
  }

  @Override
  public UIFieldType getSettingType() {
    return UIFieldType.Info;
  }

  @Override
  public int order() {
    return 20;
  }

  @Override
  public ServerConnectionStatus parseValue(EntityContext entityContext, String value) {
    return StringUtils.isEmpty(value) ? null : ServerConnectionStatus.valueOf(value);
  }
}
