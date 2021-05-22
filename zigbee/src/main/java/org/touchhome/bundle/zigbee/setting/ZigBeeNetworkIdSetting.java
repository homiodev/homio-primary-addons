package org.touchhome.bundle.zigbee.setting;

import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
import org.touchhome.bundle.api.util.TouchHomeUtils;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Log4j2
public class ZigBeeNetworkIdSetting implements SettingPluginOptionsFileExplorer {

    @Override
    public Path rootPath() {
        return TouchHomeUtils.resolvePath("zigbee");
    }

    @Override
    public boolean writeFile(Path path, BasicFileAttributes attrs) {
        return false;
    }

    @Override
    public boolean visitDirectory(Path dir, BasicFileAttributes attrs) {
        return true;
    }

    @Override
    public boolean writeDirectory(Path dir) {
        return !dir.getFileName().toString().equals("zigbee");
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public boolean removableOption(OptionModel optionModel) {
        return false;
    }

    @Override
    public boolean allowSelectDirs() {
        return false;
    }

    @Override
    public String buildTitle(Path path) {
        return path.getFileName() == null ? path.toString() : path.getFileName().toString();
    }
}
