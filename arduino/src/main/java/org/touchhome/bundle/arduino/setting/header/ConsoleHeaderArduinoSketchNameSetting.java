package org.touchhome.bundle.arduino.setting.header;

import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.setting.BundleSettingPluginFileExplorer;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;
import processing.app.BaseNoGui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

@Log4j2
public class ConsoleHeaderArduinoSketchNameSetting implements BundleHeaderSettingPlugin<String>, BundleSettingPluginFileExplorer {

    @Override
    public Path rootPath() {
        return BaseNoGui.getSketchbookFolder().toPath();
    }

    @Override
    public int levels() {
        return 2;
    }

    @Override
    public Predicate<Path> filterPath() {
        return path -> !Files.isDirectory(path) && path.getFileName().toString().endsWith(".ino");
    }

    @Override
    public int order() {
        return 200;
    }
}
