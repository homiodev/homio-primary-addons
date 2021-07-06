package org.touchhome.bundle.camera.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.dependency.DependencyExecutableInstaller;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.setting.FFMPEGInstallPathSetting;
import org.touchhome.bundle.camera.setting.FFMPEGInstallSetting;

import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Component
public class FFMPEGDependencyExecutableInstaller extends DependencyExecutableInstaller {

    @Override
    public String getName() {
        return "ffmpeg";
    }

    @Override
    protected Path installDependencyInternal(EntityContext entityContext, ProgressBar progressBar) {
        if (TouchHomeUtils.OS.isLinux()) {
            entityContext.getBean(MachineHardwareRepository.class).installSoftware("ffmpeg");
        } else {
            Path targetFolder;
            if (Files.isRegularFile(TouchHomeUtils.getInstallPath().resolve("ffmpeg").resolve("ffmpeg.exe"))) {
                targetFolder = TouchHomeUtils.getInstallPath().resolve("ffmpeg");
            } else {
                targetFolder = downloadAndExtract(entityContext.getEnv("artifactoryFilesURL") + "/ffmpeg.7z",
                        "7z", "ffmpeg", progressBar, log);
            }
            return targetFolder.resolve("ffmpeg.exe");
        }
        return null;
    }

    @Override
    public Class<? extends SettingPluginOptionsFileExplorer> getDependencyPluginSettingClass() {
        return FFMPEGInstallPathSetting.class;
    }

    @Override
    public Class<? extends SettingPluginButton> getInstallButton() {
        return FFMPEGInstallSetting.class;
    }
}
