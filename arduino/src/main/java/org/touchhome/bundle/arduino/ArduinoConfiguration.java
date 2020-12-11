package org.touchhome.bundle.arduino;

import cc.arduino.Constants;
import cc.arduino.contributions.GPGDetachedSignatureVerifier;
import cc.arduino.contributions.libraries.LibraryInstaller;
import cc.arduino.contributions.packages.ContributionInstaller;
import cc.arduino.contributions.packages.ContributionsIndexer;
import cc.arduino.files.DeleteFilesOnShutdown;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import processing.app.BaseNoGui;
import processing.app.Platform;
import processing.app.PreferencesData;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class ArduinoConfiguration {

    public static final String ESP_8266_URL = "http://arduino.esp8266.com/stable/package_esp8266com_index.json";

    @Bean
    public Platform getPlatform() throws Exception {
        Thread deleteFilesOnShutdownThread = new Thread(DeleteFilesOnShutdown.INSTANCE);
        deleteFilesOnShutdownThread.setName("DeleteFilesOnShutdown");
        Runtime.getRuntime().addShutdownHook(deleteFilesOnShutdownThread);

        Path arduinoPath = TouchHomeUtils.getFilesPath().resolve("arduino");
        System.setProperty("APP_DIR", arduinoPath.toString());
        BaseNoGui.initPlatform();

        BaseNoGui.getPlatform().init();

        BaseNoGui.initPortableFolder();

        File hardwareFolder = BaseNoGui.getHardwareFolder();
        PreferencesData.set("runtime.ide.path", hardwareFolder.getParentFile().getAbsolutePath());
        PreferencesData.set("runtime.ide.version", "" + BaseNoGui.REVISION);
        PreferencesData.set("build.path", arduinoPath.resolve("build").toString());
        PreferencesData.set("sketchbook.path", "sketchbook");

        Set<String> packageIndexURLs = new HashSet<>(PreferencesData.getCollection(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS));
        packageIndexURLs.add(ESP_8266_URL);
        PreferencesData.setCollection(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS, packageIndexURLs);

        BaseNoGui.checkInstallationFolder();

        BaseNoGui.initVersion();

        BaseNoGui.initPackages();
        BaseNoGui.onBoardOrPortChange();

        return BaseNoGui.getPlatform();
    }

    @Bean
    public ContributionsIndexer contributionsIndexer() throws Exception {
        Platform platform = getPlatform();
        ContributionsIndexer contributionsIndexer = new ContributionsIndexer(BaseNoGui.getSettingsFolder(), BaseNoGui.getHardwareFolder(),
                platform, new GPGDetachedSignatureVerifier());
        contributionsIndexer.parseIndex();
        contributionsIndexer.syncWithFilesystem();

        return contributionsIndexer;
    }

    @Bean
    public GPGDetachedSignatureVerifier gpgDetachedSignatureVerifier() {
        return new GPGDetachedSignatureVerifier();
    }

    @Bean
    public ContributionInstaller contributionInstaller() throws Exception {
        return new ContributionInstaller(getPlatform(), gpgDetachedSignatureVerifier());
    }

    @Bean
    public LibraryInstaller libraryInstaller() throws Exception {
        return new LibraryInstaller(getPlatform(), gpgDetachedSignatureVerifier());
    }
}
