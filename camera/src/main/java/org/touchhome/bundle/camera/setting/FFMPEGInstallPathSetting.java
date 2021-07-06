package org.touchhome.bundle.camera.setting;

import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FFMPEGInstallPathSetting implements SettingPluginOptionsFileExplorer {

    @Override
    public int order() {
        return 80;
    }

    @Override
    public Path rootPath() {
        return Paths.get("/");
    }

    @Override
    public String getDefaultValue() {
        return "ffmpeg";
    }

    @Override
    public boolean removableOption(OptionModel optionModel) {
        return false;
    }

    @Override
    public boolean lazyLoading() {
        return true;
    }

    @Override
    public boolean allowSelectDirs() {
        return false;
    }
}
