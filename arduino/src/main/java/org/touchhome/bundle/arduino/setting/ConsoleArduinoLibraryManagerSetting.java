package org.touchhome.bundle.arduino.setting;

import cc.arduino.contributions.ConsoleProgressListener;
import cc.arduino.contributions.ProgressListener;
import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.ContributedLibraryReleases;
import cc.arduino.contributions.libraries.LibrariesIndexer;
import cc.arduino.contributions.libraries.LibraryInstaller;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.BundlePackageInstallSettingPlugin;
import org.touchhome.bundle.api.setting.console.BundleConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;
import processing.app.BaseNoGui;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConsoleArduinoLibraryManagerSetting implements BundlePackageInstallSettingPlugin, BundleConsoleSettingPlugin<JSONObject> {

    private static Map<String, ContributedLibraryReleases> releases;

    @Override
    public int order() {
        return 90;
    }

    @Override
    public boolean acceptConsolePluginPage(ConsolePlugin consolePlugin) {
        return consolePlugin instanceof ArduinoConsolePlugin;
    }

    @SneakyThrows
    private synchronized Map<String, ContributedLibraryReleases> getReleases(EntityContext entityContext, String progressKey) {
        if (releases == null) {
            releases = new HashMap<>();

            BaseNoGui.onBoardOrPortChange();

            ProgressListener progressListener = progressKey != null ? progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus()) :
                    new ConsoleProgressListener();

            LibraryInstaller libraryInstaller = entityContext.getBean(LibraryInstaller.class);
            libraryInstaller.updateIndex(progressListener);

            LibrariesIndexer indexer = new LibrariesIndexer(BaseNoGui.getSettingsFolder());
            indexer.parseIndex();
            indexer.setLibrariesFolders(BaseNoGui.getLibrariesFolders());
            indexer.rescanLibraries();

            for (ContributedLibrary lib : BaseNoGui.librariesIndexer.getIndex().getLibraries()) {
                if (releases.containsKey(lib.getName())) {
                    releases.get(lib.getName()).add(lib);
                } else {
                    releases.put(lib.getName(), new ContributedLibraryReleases(lib));
                }
            }
        }
        return releases;
    }

    @Override
    public Collection<PackageEntity> installedBundles(EntityContext entityContext) {
        Map<String, ContributedLibraryReleases> releases = getReleases(entityContext, null);
        Collection<PackageEntity> bundleEntities = new ArrayList<>();
        for (ContributedLibraryReleases release : releases.values()) {
            if (release.getInstalled().isPresent()) {
                ContributedLibrary library = release.getInstalled().get();
                bundleEntities.add(buildBundleEntity(null, library));
            }
        }

        return bundleEntities;
    }

    @SneakyThrows
    @Override
    public Collection<PackageEntity> allBundles(EntityContext entityContext) {
        releases = null;
        Collection<PackageEntity> bundleEntities = new ArrayList<>();
        for (ContributedLibraryReleases release : getReleases(entityContext, null).values()) {
            ContributedLibrary latest = release.getLatest();
            bundleEntities.add(buildBundleEntity(release.getReleases().stream().map(ContributedLibrary::getVersion).collect(Collectors.toList()), latest));
        }

        return bundleEntities;
    }

    private PackageEntity buildBundleEntity(List<String> versions, ContributedLibrary latest) {
        return new PackageEntity()
                .setName(latest.getName())
                .setTitle(latest.getSentence())
                .setVersions(versions)
                .setVersion(latest.getVersion())
                .setSize(latest.getSize())
                .setWebsite(latest.getWebsite())
                .setAuthor(latest.getAuthor())
                .setCategory(latest.getCategory())
                .setReadme(latest.getParagraph());
    }

    @Override
    public void install(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws Exception {
        LibraryInstaller installer = entityContext.getBean(LibraryInstaller.class);
        ContributedLibrary lib = searchLibrary(getReleases(entityContext, progressKey), packageRequest.getName(), packageRequest.getVersion());
        List<ContributedLibrary> deps = BaseNoGui.librariesIndexer.getIndex().resolveDependeciesOf(lib);
        boolean depsInstalled = deps.stream().allMatch(l -> l.getInstalledLibrary().isPresent() || l.getName().equals(lib != null ? lib.getName() : null));

        ProgressListener progressListener = progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
        if (!depsInstalled) {
            installer.install(deps, progressListener);
        }
        installer.install(lib, progressListener);
        reBuildLibraries(entityContext, progressKey);
    }

    private ContributedLibrary searchLibrary(Map<String, ContributedLibraryReleases> releases, String name, String version) {
        ContributedLibraryReleases release = releases.get(name);
        if (release != null) {
            return release.getReleases().stream().filter(r -> r.getVersion().equals(version)).findAny().orElse(null);
        }
        return null;
    }

    @Override
    public void unInstall(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws IOException {
        ContributedLibrary lib = getReleases(entityContext, progressKey).values().stream()
                .filter(r -> r.getInstalled().isPresent() && r.getInstalled().get().getName().equals(packageRequest.getName()))
                .map(r -> r.getInstalled().get()).findAny().orElse(null);

        if (lib == null) {
            entityContext.ui().sendErrorMessage("Library " + packageRequest.getName() + " not found");
            return;
        }

        ProgressListener progressListener = progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
        LibraryInstaller installer = entityContext.getBean(LibraryInstaller.class);
        installer.remove(lib, progressListener);
        reBuildLibraries(entityContext, progressKey);
    }

    private void reBuildLibraries(EntityContext entityContext, String progressKey) {
        ConsoleArduinoLibraryManagerSetting.releases = null;
        getReleases(entityContext, progressKey);
    }
}
