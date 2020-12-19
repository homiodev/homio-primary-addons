package org.touchhome.bundle.cloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.ui.BellNotification;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class CloudEntryPoint implements BundleEntryPoint {

    private final EntityContext entityContext;

    public void init() {

    }

    @Override
    public int order() {
        return 800;
    }


    @Override
    public Set<BellNotification> getBellNotifications() {
        return entityContext.setting().getValue(ConsoleCloudProviderSetting.class).getBellNotifications();
    }

    @Override
    public BundleImageColorIndex getBundleImageColorIndex() {
        return BundleImageColorIndex.THREE;
    }
}
