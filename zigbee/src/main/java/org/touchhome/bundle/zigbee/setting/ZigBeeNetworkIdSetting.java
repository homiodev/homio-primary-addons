package org.touchhome.bundle.zigbee.setting;

import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

@Log4j2
public class ZigBeeNetworkIdSetting implements SettingPluginOptionsFileExplorer {

    @Override
    public UIFieldType getSettingType() {
        return UIFieldType.TextSelectBoxDynamic;
    }

    @Override
    public Path rootPath() {
        return TouchHomeUtils.resolvePath("zigbee");
    }

    @Override
    public Predicate<Path> filterPath() {
        return path -> Files.isDirectory(path) && !path.getFileName().toString().equals("zigbee");
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public boolean removableOption(OptionModel optionModel) {
        return false;
    }
}
