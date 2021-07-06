package org.touchhome.bundle.camera.setting;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
import org.touchhome.bundle.camera.util.FFMPEGDependencyExecutableInstaller;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FFMPEGInstallSetting implements SettingPluginButton {

    @Override
    public int order() {
        return 100;
    }

    @Override
    public String getIcon() {
        return "fas fa-play";
    }

    @Override
    public boolean isVisible(EntityContext entityContext) {
        return entityContext.getBean(FFMPEGDependencyExecutableInstaller.class).isRequireInstallDependencies(entityContext, true);
    }
}
