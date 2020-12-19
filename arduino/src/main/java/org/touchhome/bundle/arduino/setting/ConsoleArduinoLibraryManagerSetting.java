package org.touchhome.bundle.arduino.setting;

import cc.arduino.contributions.ConsoleProgressListener;
import cc.arduino.contributions.ProgressListener;
import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.ContributedLibraryReleases;
import cc.arduino.contributions.libraries.LibraryInstaller;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePlugin;
import org.touchhome.bundle.api.setting.SettingPluginPackageInstall;
import org.touchhome.bundle.api.setting.console.ConsoleSettingPlugin;
import org.touchhome.bundle.arduino.ArduinoConsolePlugin;
import processing.app.BaseNoGui;
import processing.app.packages.UserLibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ConsoleArduinoLibraryManagerSetting implements SettingPluginPackageInstall, ConsoleSettingPlugin<JSONObject> {

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
    private synchronized Map<String, ContributedLibraryReleases> getReleases(EntityContext entityContext, String progressKey, AtomicReference<String> error) {
        if (releases == null) {
            releases = new HashMap<>();

            BaseNoGui.onBoardOrPortChange();

            ProgressListener progressListener = progressKey != null ? progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus()) :
                    new ConsoleProgressListener();

            try {
                LibraryInstaller libraryInstaller = entityContext.getBean(LibraryInstaller.class);
                libraryInstaller.updateIndex(progressListener);
            } catch (Exception ex) {
                if (error == null) {
                    throw ex;
                }
                error.set(ex.getMessage());
                BaseNoGui.librariesIndexer.parseIndex();
                BaseNoGui.librariesIndexer.rescanLibraries();
            }

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
    public PackageContext installedPackages(EntityContext entityContext) {
        Collection<PackageModel> bundleEntities = new ArrayList<>();
        for (UserLibrary library : BaseNoGui.librariesIndexer.getInstalledLibraries()) {
            bundleEntities.add(buildBundleEntity(library));
        }
        return new PackageContext(null, bundleEntities);
    }

    @Override
    public PackageContext allPackages(EntityContext entityContext) {
        releases = null;
        AtomicReference<String> error = new AtomicReference<>();
        Collection<PackageModel> bundleEntities = new ArrayList<>();
        for (ContributedLibraryReleases release : getReleases(entityContext, null, error).values()) {
            ContributedLibrary latest = release.getLatest();
            bundleEntities.add(buildBundleEntity(release.getReleases().stream().map(ContributedLibrary::getVersion).collect(Collectors.toList()), latest));
        }

        return new PackageContext(error.get(), bundleEntities);
    }

    @Override
    public void installPackage(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws Exception {
        LibraryInstaller installer = entityContext.getBean(LibraryInstaller.class);
        ContributedLibrary lib = searchLibrary(getReleases(entityContext, progressKey, null), packageRequest.getName(), packageRequest.getVersion());
        List<ContributedLibrary> deps = BaseNoGui.librariesIndexer.getIndex().resolveDependeciesOf(lib);
        boolean depsInstalled = deps.stream().allMatch(l -> l.getInstalledLibrary().isPresent() || l.getName().equals(lib != null ? lib.getName() : null));

        ProgressListener progressListener = progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
        if (!depsInstalled) {
            installer.install(deps, progressListener);
        }
        installer.install(lib, progressListener);
        reBuildLibraries(entityContext, progressKey);
    }

    @Override
    public void unInstallPackage(EntityContext entityContext, PackageRequest packageRequest, String progressKey) throws IOException {
        ContributedLibrary lib = getReleases(entityContext, progressKey, null).values().stream()
                .filter(r -> r.getInstalled().isPresent() && r.getInstalled().get().getName().equals(packageRequest.getName()))
                .map(r -> r.getInstalled().get()).findAny().orElse(null);

        if (lib == null) {
            entityContext.ui().sendErrorMessage("Library '" + packageRequest.getName() + "' not found");
        } else if (lib.isIDEBuiltIn()) {
            entityContext.ui().sendErrorMessage("Unable remove built-in library: '" + packageRequest.getName() + "'");
        } else {
            ProgressListener progressListener = progress -> entityContext.ui().progress(progressKey, progress.getProgress(), progress.getStatus());
            LibraryInstaller installer = entityContext.getBean(LibraryInstaller.class);
            installer.remove(lib, progressListener);
            reBuildLibraries(entityContext, progressKey);
        }
    }

    private ContributedLibrary searchLibrary(Map<String, ContributedLibraryReleases> releases, String name, String version) {
        ContributedLibraryReleases release = releases.get(name);
        if (release != null) {
            return release.getReleases().stream().filter(r -> r.getVersion().equals(version)).findAny().orElse(null);
        }
        return null;
    }

    @SneakyThrows
    private PackageModel buildBundleEntity(UserLibrary library) {
        PackageModel packageModel = new PackageModel()
                .setName(library.getName())
                .setTitle(library.getSentence())
                .setVersion(library.getVersion())
                .setWebsite(library.getWebsite())
                .setAuthor(library.getAuthor())
                .setCategory(library.getCategory())
                .setReadme(library.getParagraph());
        String[] readmeFiles = library.getInstalledFolder().list((dir, name) -> name.toLowerCase().startsWith("readme."));
        if (readmeFiles != null && readmeFiles.length > 0) {
            packageModel.setReadme(packageModel.getReadme() + "<br/><br/>" +
                    new String(Files.readAllBytes(library.getInstalledFolder().toPath().resolve(readmeFiles[0]))));
        }

        if (library.isIDEBuiltIn()) {
            packageModel.setTags(Collections.singleton("Built-In")).setRemovable(false);
        }

        return packageModel;
    }

    private PackageModel buildBundleEntity(List<String> versions, ContributedLibrary library) {
        PackageModel packageModel = new PackageModel()
                .setName(library.getName())
                .setTitle(library.getSentence())
                .setVersions(versions)
                .setVersion(library.getVersion())
                .setSize(library.getSize())
                .setWebsite(library.getWebsite())
                .setAuthor(library.getAuthor())
                .setCategory(library.getCategory())
                .setReadme(library.getParagraph());
        if (library.isIDEBuiltIn()) {
            packageModel.setTags(Collections.singleton("Built-In")).setRemovable(false);
        }

        return packageModel;
    }

    private void reBuildLibraries(EntityContext entityContext, String progressKey) {
        ConsoleArduinoLibraryManagerSetting.releases = null;
        getReleases(entityContext, progressKey, null);
    }
}
