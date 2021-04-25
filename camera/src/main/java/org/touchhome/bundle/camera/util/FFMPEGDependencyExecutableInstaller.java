package org.touchhome.bundle.camera.util;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.dependency.DependencyExecutableInstaller;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.ui.action.UIActionHandler;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.setting.FFMPEGInstallPathOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

@Log4j2
public class FFMPEGDependencyExecutableInstaller implements DependencyExecutableInstaller {

    private static Boolean requireInstall;

    @Override
    public synchronized boolean isRequireInstallDependencies(EntityContext entityContext) {
        if (requireInstall == null) {
            requireInstall = true;
            MachineHardwareRepository repository = entityContext.getBean(MachineHardwareRepository.class);
            if (repository.isSoftwareInstalled("ffmpeg")) {
                requireInstall = false;
            } else {
                Path ffmpegPath = entityContext.setting().getValue(FFMPEGInstallPathOptions.class);
                if (Files.isRegularFile(ffmpegPath)) {
                    requireInstall = !repository.execute(ffmpegPath + " -version").startsWith("ffmpeg version 2");
                }
            }
        }
        return requireInstall;
    }

    @Override
    public void installDependencyInternal(EntityContext entityContext, ProgressBar progressBar) {
        requireInstall = null;
        if (TouchHomeUtils.OS_NAME.isLinux()) {
            entityContext.getBean(MachineHardwareRepository.class).installSoftware("ffmpeg");
        } else {
            Path targetFolder = downloadAndExtract(entityContext.getEnv("artifactoryFilesURL") + "/ffmpeg.7z",
                    "7z", "ffmpeg", progressBar, log);
            entityContext.setting().setValue(FFMPEGInstallPathOptions.class, targetFolder.resolve("ffmpeg.exe"));
        }
    }

    @Override
    public void afterDependencyInstalled() {

    }

    public static class InstallFFmpegHeaderAction implements UIActionHandler {
        @Override
        public ActionResponseModel apply(EntityContext entityContext, JSONObject jsonObject) {
            DependencyExecutableInstaller installer = new FFMPEGDependencyExecutableInstaller();
            if (installer.isRequireInstallDependencies(entityContext)) {
                entityContext.bgp().runWithProgress("install-deps-ffmpeg", false,
                        progressBar -> {
                            installer.installDependency(entityContext, progressBar);
                        }, null,
                        () -> new RuntimeException("INSTALL_DEPENDENCY_IN_PROGRESS"));
            }
            return null;
        }
    }

    public static class RequireInstallCondition implements Predicate<EntityContext> {
        @Override
        public boolean test(EntityContext entityContext) {
            return new FFMPEGDependencyExecutableInstaller().isRequireInstallDependencies(entityContext);
        }
    }
}
