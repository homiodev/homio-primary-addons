package org.touchhome.bundle.cloud.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.exception.NotFoundException;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.console.BundleConsoleSettingPlugin;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.providers.SshCloudProvider;

import java.util.List;

public class ConsoleCloudProviderSetting implements BundleConsoleSettingPlugin<CloudProvider> {

    @Override
    public Class<CloudProvider> getType() {
        return CloudProvider.class;
    }

    @Override
    public String getDefaultValue() {
        return SshCloudProvider.class.getSimpleName();
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.SelectBoxDynamic;
    }

    @Override
    public int order() {
        return 600;
    }

    @Override
    public String[] pages() {
        return new String[]{"ssh"};
    }

    @Override
    public List<Option> loadAvailableValues(EntityContext entityContext) {
        return Option.simpleNamelist(entityContext.getBeansOfType(CloudProvider.class));
    }

    @Override
    public CloudProvider parseValue(EntityContext entityContext, String value) {
        return entityContext.getBeansOfType(CloudProvider.class).stream().filter(p -> p.getClass().getSimpleName().equals(value)).findAny()
                .orElseThrow(() -> new NotFoundException("Unable to find cloud provider with name: " + value));
    }
}
