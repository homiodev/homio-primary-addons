package org.touchhome.bundle.camera.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.dependency.DependencyExecutableInstaller;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.setting.FFMPEGInstallPathSetting;
import org.touchhome.common.model.ProgressBar;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @UISidebarButton(buttonIcon = "fab fa-instalod", buttonIconColor = "#39B84E",
 *         buttonTitle = "TITLE.INSTALL_FFMPEG",
 *         handlerClass = FFMPEGDependencyExecutableInstaller.class)
 */
@Log4j2
@Component
@Deprecated // TODO: Just as example
public class FFMPEGDependencyExecutableInstaller extends DependencyExecutableInstaller {

    @Override
    public String getName() {
        return "ffmpeg";
    }

    @Override
    protected Path installDependencyInternal(EntityContext entityContext, ProgressBar progressBar) {
        if (SystemUtils.IS_OS_LINUX) {
            entityContext.getBean(MachineHardwareRepository.class).installSoftware("ffmpeg", 600);
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
        return null; // return FFMPEGInstallSetting.class;
    }
}

/**
 * package org.touchhome.bundle.camera.setting;
 *
 * import org.touchhome.bundle.api.EntityContext;
 * import org.touchhome.bundle.api.model.OptionModel;
 * import org.touchhome.bundle.api.setting.SettingPluginButton;
 * import org.touchhome.bundle.api.setting.SettingPluginOptionsFileExplorer;
 * import org.touchhome.bundle.camera.util.FFMPEGDependencyExecutableInstaller;
 *
 * import java.nio.file.Path;
 * import java.nio.file.Paths;
 *
 * public class FFMPEGInstallSetting implements SettingPluginButton {
 *
 *     @Override
 *     public int order() {
 *         return 100;
 *     }
 *
 *     @Override
 *     public String getIcon() {
 *         return "fas fa-play";
 *     }
 *
 *     @Override
 *     public boolean isVisible(EntityContext entityContext) {
 *         return entityContext.getBean(FFMPEGDependencyExecutableInstaller.class).isRequireInstallDependencies(entityContext, true);
 *     }
 * }
 */
