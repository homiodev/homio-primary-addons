package org.touchhome.bundle.cloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.ui.builder.BellNotificationBuilder;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

@Log4j2
@Component
@RequiredArgsConstructor
public class CloudEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  public void init() {

  }

  @Override
  public int order() {
    return 800;
  }


  public void assembleBellNotifications(BellNotificationBuilder bellNotificationBuilder) {
    entityContext.setting().getValue(ConsoleCloudProviderSetting.class).assembleBellNotifications(bellNotificationBuilder);
  }

  @Override
  public BundleImageColorIndex getBundleImageColorIndex() {
    return BundleImageColorIndex.THREE;
  }
}
