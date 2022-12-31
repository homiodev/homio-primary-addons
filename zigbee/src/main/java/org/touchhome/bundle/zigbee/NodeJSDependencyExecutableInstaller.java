package org.touchhome.bundle.zigbee;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.dependency.DependencyExecutableInstaller;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.setting.SettingPluginButton;
import org.touchhome.bundle.api.setting.SettingPluginText;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.mqtt.setting.MQTTPathSetting;
import org.touchhome.common.model.ProgressBar;

@Log4j2
@Component
public class NodeJSDependencyExecutableInstaller extends DependencyExecutableInstaller {

    @Override
    public String getName() {
        return "node";
    }

    @Override
    public Path installDependencyInternal(
            @NotNull EntityContext entityContext, @NotNull ProgressBar progressBar) {
        if (SystemUtils.IS_OS_LINUX) {
            MachineHardwareRepository machineHardwareRepository =
                    entityContext.getBean(MachineHardwareRepository.class);
            machineHardwareRepository.execute(
                    "curl -fsSL https://deb.nodesource.com/setup_current.x | sudo -E bash -");
            machineHardwareRepository.installSoftware("nodejs", 600);
        } else {
            Path targetFolder;
            if (Files.isRegularFile(
                    TouchHomeUtils.getInstallPath().resolve("nodejs").resolve("node.exe"))) {
                targetFolder = TouchHomeUtils.getInstallPath().resolve("nodejs");
            } else {
                targetFolder =
                        downloadAndExtract(
                                "https://nodejs.org/dist/v18.12.1/node-v18.12.1-win-x64.zip",
                                "nodejs.zip",
                                progressBar,
                                log);
            }
            return targetFolder.resolve("node.exe");
        }
        return null;
    }

    @Override
    protected void afterDependencyInstalled(@NotNull EntityContext entityContext, Path path) {}

    @Override
    public boolean checkWinDependencyInstalled(
            MachineHardwareRepository repository, @NotNull Path targetPath) {
        return !repository.executeNoErrorThrow(targetPath + " -v", 60, null).startsWith("v");
    }

    @Override
    public @NotNull Class<? extends SettingPluginText> getDependencyPluginSettingClass() {
        return MQTTPathSetting.class;
    }

    @Override
    public Class<? extends SettingPluginButton> getInstallButton() {
        return null;
    }
}
