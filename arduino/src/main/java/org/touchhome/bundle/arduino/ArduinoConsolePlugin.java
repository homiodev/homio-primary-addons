package org.touchhome.bundle.arduino;

import cc.arduino.Constants;
import cc.arduino.contributions.*;
import cc.arduino.contributions.libraries.LibraryInstaller;
import cc.arduino.contributions.packages.ContributedPlatform;
import cc.arduino.contributions.packages.ContributionInstaller;
import cc.arduino.contributions.packages.ContributionsIndexer;
import cc.arduino.packages.BoardPort;
import cc.arduino.utils.Progress;
import com.fazecast.jSerialComm.SerialPort;
import com.github.zafarkhaja.semver.Version;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginEditor;
import org.touchhome.bundle.api.json.ActionResponse;
import org.touchhome.bundle.api.setting.BundleSettingPlugin;
import org.touchhome.bundle.arduino.setting.ArduinoGetBoardInfoSetting;
import org.touchhome.bundle.arduino.setting.ArduinoGetBoardsSetting;
import org.touchhome.bundle.arduino.setting.ArduinoPortSetting;
import org.touchhome.bundle.arduino.setting.ConsoleArduinoFetchEsp8266PackagesSetting;
import processing.app.BaseNoGui;
import processing.app.PreferencesData;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ArduinoConsolePlugin implements ConsolePluginEditor {

    private final EntityContext entityContext;
    private EditorContent content;

    private final ContributionInstaller contributionInstaller;
    private final LibraryInstaller libraryInstaller;
    private final GPGDetachedSignatureVerifier gpgDetachedSignatureVerifier;
    private final ContributionsIndexer contributionsIndexer;

    public void init() {
        entityContext.setting().listenValue(ConsoleArduinoFetchEsp8266PackagesSetting.class, "avr-download-esp8266", () -> {
            installBoard(ConsoleArduinoFetchEsp8266PackagesSetting.URL, "esp8266", "esp8266", "2.7.4");
        });

        entityContext.setting().listenValue(ArduinoGetBoardInfoSetting.class, "avr-get-board-info", () -> {
            SerialPort serialPort = entityContext.setting().getValue(ArduinoPortSetting.class);
            if (serialPort != null) {
                List<BoardPort> ports = BaseNoGui.getDiscoveryManager().discovery();
                BoardPort boardPort = ports.stream().filter(p -> p.getAddress().equals(serialPort.getSystemPortName())).findAny().orElse(null);
                if (boardPort != null) {
                    entityContext.ui().sendJsonMessage("ARDUINO.BOARD_INFO", boardPort);
                }
            } else {
                entityContext.ui().sendErrorMessage("ARDUINO.PORT_NOT_SELECTED");
            }
        });
    }

    @SneakyThrows
    private void installBoard(String url, String platformArch, String packageName, String boardVersion) {
        String progressKey = "install-" + packageName + "/" + boardVersion;

        try {
            Set<String> packageIndexURLs = new HashSet<>(PreferencesData.getCollection(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS));
            packageIndexURLs.add(url);
            PreferencesData.setCollection(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS, packageIndexURLs);

            entityContext.ui().progress(progressKey, 0, "");
            ProgressListener progressListener = new ConsoleProgressListener() {
                @Override
                public boolean onProgress(Progress progress) {
                    if (super.onProgress(progress)) {
                        entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
                    }
                    return true;
                }
            };

            List<String> downloadedPackageIndexFiles = contributionInstaller.updateIndex(progressListener);
            contributionInstaller.deleteUnknownFiles(downloadedPackageIndexFiles);

            ContributedPlatform selected = null;
            if (boardVersion != null) {
                Optional<Version> version = VersionHelper.valueOf(boardVersion);
                selected = contributionsIndexer.getIndex().findPlatform(packageName, platformArch, version.get().toString());
            } else if (packageName != null) {
                List<ContributedPlatform> platformsByName = contributionsIndexer.getIndex().findPlatforms(packageName, platformArch);
                Collections.sort(platformsByName, new DownloadableContributionVersionComparator());
                if (!platformsByName.isEmpty()) {
                    selected = platformsByName.get(platformsByName.size() - 1);
                }
            }
            if (selected == null) {
                throw new RuntimeException("Selected board is not available");
            }

            ContributedPlatform installed = contributionsIndexer.getInstalled(packageName, platformArch);

            if (!selected.isBuiltIn()) {
                contributionInstaller.install(selected, progressListener);
            }

            if (installed != null && !installed.isBuiltIn()) {
                contributionInstaller.remove(installed);
            }
            entityContext.ui().sendInfoMessage("INSTALL_COMPLETED");
        } catch (Exception ex) {
            entityContext.ui().sendErrorMessage("INSTALL_ERROR", ex);
        } finally {
            entityContext.ui().progress(progressKey, 100, "");
        }
    }

    @Override
    public Map<String, Class<? extends BundleSettingPlugin<?>>> getHeaderActions() {
        Map<String, Class<? extends BundleSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
        headerActions.put("getBoardInfo", ArduinoGetBoardInfoSetting.class);
        headerActions.put("arduinoPort", ArduinoPortSetting.class);
        headerActions.put("boards", ArduinoGetBoardsSetting.class);
        return headerActions;
    }

    @Override
    public ActionResponse save(EditorContent content) {
        this.content = content;
        return new ActionResponse("SAVED", ActionResponse.ResponseAction.ShowSuccessMsg);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.cpp;
    }

    @Override
    public String accept() {
        return ".ino, .cpp";
    }

    @Override
    public EditorContent getValue() {
        return content;
    }
}
