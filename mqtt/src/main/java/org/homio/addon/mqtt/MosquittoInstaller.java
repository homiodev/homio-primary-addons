package org.homio.addon.mqtt;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.homio.api.util.CommonUtils.STATIC_FILES;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.api.Context;
import org.homio.api.ContextHardware;
import org.homio.api.repository.GitHubProject.VersionedFile;
import org.homio.api.service.DependencyExecutableInstaller;
import org.homio.api.util.CommonUtils;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class MosquittoInstaller extends DependencyExecutableInstaller {

    public MosquittoInstaller(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return "mosquitto";
    }

    @Override
    protected @Nullable String getInstalledVersion() {
        ContextHardware hardware = context.hardware();
        List<String> versionList = null;
        if (IS_OS_WINDOWS) {
            Path mosquittoPath = CommonUtils.getInstallPath().resolve("mosquitto").resolve("mosquitto.exe");
            executable = Files.isRegularFile(mosquittoPath) ? mosquittoPath.toString() :
                System.getProperty("MOSQUITTO_DIR", System.getenv("MOSQUITTO_DIR"));
            if (StringUtils.isNotEmpty(executable)) {
                versionList = hardware.executeNoErrorThrowList(executable + " -h", 10, null);
            }
        }
        if (versionList == null || versionList.isEmpty()) {
            executable = "mosquitto";
            versionList = hardware.executeNoErrorThrowList(executable + " -h", 60, null);
        }
        if (!versionList.isEmpty() && versionList.get(0).startsWith("mosquitto version")) {
            return versionList.get(0).substring("mosquitto version".length()).trim();
        }
        return null;
    }

    @Override
    protected void installDependencyInternal(@NotNull ProgressBar progressBar, String version) {
        if (IS_OS_LINUX) {
            context.hardware().installSoftware(getName(), 600);
        } else {
            String url = context.setting().getEnv("source-mosquitto");
            if (url == null) {
                url = STATIC_FILES.getContentFile("mosquitto").map(VersionedFile::getDownloadUrl).orElse(null);
            }
            if (url == null) {
                throw new IllegalStateException("Unable to find mosquitto download url");
            }
            CommonUtils.downloadAndExtract(url,
                "mosquitto.7z", (progress, message, error) -> {
                    progressBar.progress(progress, message);
                    log.info("Mosquitto: {}", message);
                });
        }
    }
}
