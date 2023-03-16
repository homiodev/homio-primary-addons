package org.touchhome.bundle.cloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.cloud.setting.ConsoleCloudProviderSetting;

@Log4j2
@Component
@RequiredArgsConstructor
public class CloudEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;
    private CloudProvider cloudProvider;

    public void init() {
        entityContext.setting().listenValueAndGet(ConsoleCloudProviderSetting.class, "cloud", provider -> {
            if (cloudProvider == null || (cloudProvider.getClass() != provider.getClass())) {
                cloudProvider = provider;
                cloudProvider.stop();
                cloudProvider = provider;
                cloudProvider.start();
            }
        });
    }

    @Override
    public int order() {
        return 800;
    }

    @Override
    public BundleImageColorIndex getBundleImageColorIndex() {
        return BundleImageColorIndex.THREE;
    }
}
