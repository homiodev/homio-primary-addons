package org.touchhome.bundle.arduino.setting;

import cc.arduino.contributions.ProgressListener;
import cc.arduino.contributions.packages.ContributedBoard;
import cc.arduino.contributions.packages.ContributedPackage;
import cc.arduino.contributions.packages.ContributedPlatform;
import cc.arduino.contributions.packages.ContributionInstaller;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.BundlePackageInstallSettingPlugin;
import org.touchhome.bundle.api.setting.console.BundleConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;
import processing.app.BaseNoGui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleArduinoBoardManagerSetting implements BundlePackageInstallSettingPlugin, BundleConsoleSettingPlugin<JSONObject> {

    private static List<ContributedPlatformReleases> contributions;

    @Override
    public String getIcon() {
        return "fas fa-tasks";
    }

    @Override
    public int order() {
        return 80;
    }

    @Override
    public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
        return consolePlugin instanceof ArduinoConsolePlugin;
    }

    @Override
    public Collection<PackageEntity> installedBundles(EntityContext entityContext) throws Exception {
        Collection<PackageEntity> bundleEntities = new ArrayList<>();
        for (ContributedPlatformReleases release : getContributions()) {
            if (release.getInstalled() != null) {
                bundleEntities.add(buildBundleEntity(null, release.getInstalled()));
            }
        }

        return bundleEntities;
    }

    @SneakyThrows
    @Override
    public Collection<PackageEntity> allBundles(EntityContext entityContext) {
        Collection<PackageEntity> bundleEntities = new ArrayList<>();
        for (ContributedPlatformReleases release : getContributions()) {
            bundleEntities.add(buildBundleEntity(release.getReleases().stream().map(ContributedPlatform::getVersion).collect(Collectors.toList()), release.getLatest()));
        }

        return bundleEntities;
    }

    private PackageEntity buildBundleEntity(List<String> versions, ContributedPlatform latest) {
        String desc = versions == null ? "" : "<pre>Boards included in this package:<br/><br/>" +
                latest.getBoards().stream().map(ContributedBoard::getName).collect(Collectors.joining("<br/>")) + "" +
                "</pre>";
        return new PackageEntity()
                .setName(latest.getName())
                .setTitle(latest.getName())
                .setVersions(versions)
                .setVersion(latest.getVersion())
                .setSize(latest.getSize())
                .setWebsite(latest.getParentPackage().getWebsiteURL())
                .setAuthor(latest.getParentPackage().getMaintainer())
                .setCategory(latest.getCategory())
                .setReadme(desc);
    }

    @Override
    public void install(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws Exception {
        ContributedPlatform platform = searchContributedPlatform(packageRequest.getName(), packageRequest.getVersion());
        ProgressListener progressListener = progress ->
                entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
        entityContext.getBean(ContributionInstaller.class).install(platform, progressListener);
        onIndexesUpdated();
    }

    @Override
    public void unInstall(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws Exception {
        ContributedPlatformReleases release = getContributedPlatformReleases(packageRequest.getName());
        entityContext.getBean(ContributionInstaller.class).remove(release.getInstalled());
        onIndexesUpdated();
    }

    private void onIndexesUpdated() throws Exception {
        BaseNoGui.initPackages();
        contributions = null;
    }

    private ContributedPlatform searchContributedPlatform(String name, String version) {
        ContributedPlatformReleases release = getContributedPlatformReleases(name);
        for (ContributedPlatform contributedPlatform : release.getReleases()) {
            if (contributedPlatform.getVersion().equals(version)) {
                return contributedPlatform;
            }
        }
        throw new RuntimeException("Unable to find board: " + name + " with version: " + version);
    }

    private ContributedPlatformReleases getContributedPlatformReleases(String name) {
        ContributedPlatformReleases release = getContributions().stream().filter(c -> c.getLatest().getName().equals(name)).findFirst().orElse(null);
        if (release == null) {
            throw new RuntimeException("Unable to find board with name: " + name);
        }
        return release;
    }

    public static List<ContributedPlatformReleases> getContributions() {
        if (contributions == null) {
            contributions = new ArrayList<>();
            for (ContributedPackage pack : BaseNoGui.indexer.getPackages()) {
                for (ContributedPlatform platform : pack.getPlatforms()) {
                    addContribution(platform);
                }
            }
        }
        return contributions;
    }

    private static void addContribution(ContributedPlatform platform) {
        for (ContributedPlatformReleases contribution : contributions) {
            if (!contribution.shouldContain(platform))
                continue;
            contribution.add(platform);
            return;
        }

        contributions.add(new ContributedPlatformReleases(platform));
    }
}
