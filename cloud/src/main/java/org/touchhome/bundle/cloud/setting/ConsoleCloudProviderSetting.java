package org.touchhome.bundle.cloud.setting;

import org.touchhome.bundle.api.setting.SettingPluginOptionsBean;
import org.touchhome.bundle.api.setting.console.ConsoleSettingPlugin;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.providers.SshCloudProvider;

public class ConsoleCloudProviderSetting implements ConsoleSettingPlugin<CloudProvider>,
        SettingPluginOptionsBean<CloudProvider> {

    @Override
    public Class<CloudProvider> getType() {
        return CloudProvider.class;
    }

    @Override
    public String getDefaultValue() {
        return SshCloudProvider.class.getSimpleName();
    }

    @Override
    public int order() {
        return 600;
    }

    @Override
    public String[] pages() {
        return new String[]{"ssh"};
    }
}
