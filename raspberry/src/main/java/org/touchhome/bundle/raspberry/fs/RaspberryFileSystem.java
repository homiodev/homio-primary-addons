package org.touchhome.bundle.raspberry.fs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.CachedFileSystem;
import org.touchhome.bundle.api.entity.storage.VendorFileSystem;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;
import org.touchhome.common.model.FileSystemItem;
import org.touchhome.common.util.ArchiveUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class RaspberryFileSystem
        extends VendorFileSystem<RaspberryDeviceEntity, RaspberryFileSystem.RaspberryCacheFileSystem, RaspberryDeviceEntity> {

    public RaspberryFileSystem(RaspberryDeviceEntity entity, EntityContext entityContext) {
        super(entity, entityContext);
        this.setDrive(entity);
        this.dispose();
    }

    @Override
    protected void onEntityUpdated() {
        if (!this.getRoot().getSource().file.getPath().equals(getEntity().getFileSystemRoot())) {
            this.dispose();
        }
    }

    @Override
    public void dispose() {
        setRoot(new RaspberryCacheFileSystem(new RaspberryFile(new File(getEntity().getFileSystemRoot())), null));
    }

    @Override
    public FileSystemItem getArchiveEntries(String[] archivePath, String password) {
        Path path = buildPath(archivePath);
        List<File> files = ArchiveUtil.getArchiveEntries(path, password);
        return buildRoot(path, files);
    }

    @Override
    @SneakyThrows
    public Collection<FileSystemItem> getChild(String[] filePath) {
        List<FileSystemItem> fmPaths = new ArrayList<>();
        try (Stream<Path> stream = Files.list(buildPath(filePath))) {
            for (Path path : stream.collect(Collectors.toList())) {
                try {
                    if (!Files.isHidden(path)) {
                        fmPaths.add(buildFileSystemItem(path, path.toFile()));
                    }
                } catch (AccessDeniedException ex) {
                    fmPaths.add(buildFileSystemItem(path, path.toFile()));
                }
            }
        }
        return fmPaths;
    }

    @Override
    public long getTotalSpace() {
        return new File(getEntity().getFileSystemRoot()).getTotalSpace();
    }

    @Override
    public long getUsedSpace() {
        File file = new File(getEntity().getFileSystemRoot());
        return file.getTotalSpace() - file.getUsableSpace();
    }

    @SneakyThrows
    @Override
    public FileSystemItem upload(String[] parentPath, String fileName, byte[] content, boolean append, boolean replace) {
        Path path = buildPath(parentPath).resolve(fileName);
        if (!replace && Files.exists(path)) {
            throw new FileAlreadyExistsException("File '" + path.getFileName() + "' already exists");
        }
        TouchHomeUtils.writeToFile(path, content, append);
        return buildRoot(Collections.singletonList(path));
    }

    @Override
    public FileSystemItem upload(String[] parentPath, MultipartFile[] files, boolean replace) throws Exception {
        Path parent = buildPath(parentPath);
        List<Path> result = new ArrayList<>();
        for (MultipartFile file : files) {
            Path nodePath = parent.resolve(defaultString(file.getOriginalFilename(), ""));
            if (!replace && Files.exists(nodePath)) {
                throw new FileAlreadyExistsException("File '" + nodePath.getFileName() + "' already exists");
            }
            TouchHomeUtils.writeToFile(nodePath, file.getInputStream(), false);
            result.add(nodePath);
        }
        return buildRoot(result);
    }

    @SneakyThrows
    @Override
    public void delete(List<String[]> sourceFilePathList) {
        for (String[] filePath : sourceFilePathList) {
            Path path = buildPath(filePath);
            if (Files.isDirectory(path)) {
                FileUtils.deleteDirectory(path.toFile());

            } else {
                Files.deleteIfExists(path);
            }
        }
    }

    @NotNull
    private Path buildPath(String[] filePath) {
        Path path = Paths.get("", filePath);
        if (!path.toString().startsWith(getEntity().getFileSystemRoot())) {
            path = Paths.get(getEntity().getFileSystemRoot()).resolve(path);
        }
        return path;
    }

    @Override
    public FileSystemItem createFolder(String[] parentPath, String name) throws Exception {
        Path path = buildPath(parentPath).resolve(name);
        if (Files.exists(path)) {
            throw new FileAlreadyExistsException("Folder " + name + " already exists");
        }
        Path dir = Files.createDirectories(path);
        return buildRoot(Collections.singletonList(dir));
    }

    @Override
    public FileSystemItem rename(String[] filePath, String newName) throws Exception {
        Path path = buildPath(filePath);
        Path created = Files.move(path, path.resolveSibling(newName), StandardCopyOption.REPLACE_EXISTING);
        return buildRoot(Collections.singletonList(created));
    }

    @Override
    public FileSystemItem copy(List<String[]> sourceFilePathList, String[] targetFilePath, boolean removeSource,
                               boolean replaceExisting)
            throws Exception {
        List<Path> result = new ArrayList<>();
        CopyOption[] options = replaceExisting ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
        for (String[] sourceFilePath : sourceFilePathList) {
            Path sourcePath = buildPath(sourceFilePath);
            Path targetPath = buildPath(targetFilePath);
            if (Files.isDirectory(sourcePath)) {
                Files.walk(sourcePath).forEach(source -> {
                    Path destination = targetPath.resolve(source.toString().substring(sourcePath.toString().length()));
                    try {
                        Files.copy(source, destination, options);
                        result.add(destination);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                FileUtils.deleteDirectory(sourcePath.toFile());
            } else {
                try {
                    Path path = Files.copy(sourcePath, targetPath.resolve(sourcePath.getFileName()), options);
                    result.add(path);
                } catch (Exception ignore) {} // ignore if not replace
                if (removeSource) {
                    Files.delete(sourcePath);
                }
            }
        }

        return buildRoot(result);
    }

    @Override
    public List<ArchiveFormat> getSupportArchiveFormat() {
        return Stream.of(ArchiveUtil.ZipFormat.values()).map(format ->
                        new ArchiveFormat(format.name(), format.getName(), Arrays.asList(format.getExtensions())))
                .collect(Collectors.toList());
    }

    @Override
    public FileSystemItem archive(List<String[]> sourceFilePath, String[] targetFilePath, String format, String level,
                                  String password, boolean removeSource) throws IOException {
        Path target = buildPath(targetFilePath);
        List<Path> paths = sourceFilePath.stream().map(this::buildPath).collect(Collectors.toList());
        Path zipFile = ArchiveUtil.zip(paths, target,
                ArchiveUtil.ZipFormat.valueOf(format), level, password, null);
        if (removeSource) {
            for (Path path : paths) {
                FileUtils.forceDelete(path.toFile());
            }
        }
        return buildRoot(Collections.singletonList(zipFile));
    }

    @Override
    public FileSystemItem unArchive(String[] sourceFilePath, String[] targetFilePath, String password,
                                    boolean removeSource, String fileHandler)
            throws Exception {
        Path sourcePath = buildPath(sourceFilePath);
        ArchiveUtil.UnzipFileIssueHandler issueHandler = ArchiveUtil.UnzipFileIssueHandler.valueOf(fileHandler);
        List<Path> paths = ArchiveUtil.unzip(sourcePath, buildPath(targetFilePath), password, null, issueHandler);
        if (removeSource) {
            FileUtils.forceDelete(sourcePath.toFile());
        }
        return buildRoot(paths);
    }

    private FileSystemItem buildRoot(Path basePath, List<File> files) {
        Path root = Paths.get(getEntity().getFileSystemRoot()).resolve(basePath);
        FileSystemItem rootPath = this.buildFileSystemItem(root, root.toFile());
        for (File file : files) {
            Path pathCursor = root;
            FileSystemItem cursor = rootPath;
            for (Path pathItem : file.toPath()) {
                pathCursor = pathCursor.resolve(pathItem);
                cursor = cursor.addChild(false, buildFileSystemItem(pathCursor, file));
            }
        }
        return rootPath;
    }

    private FileSystemItem buildRoot(List<Path> paths) {
        Path root = Paths.get(getEntity().getFileSystemRoot());
        FileSystemItem rootPath = this.buildFileSystemItem(root, root.toFile());
        for (Path path : paths) {
            Path pathCursor = root;
            FileSystemItem cursor = rootPath;
            for (Path pathItem : root.relativize(path)) {
                pathCursor = pathCursor.resolve(pathItem);
                cursor = cursor.addChild(false, buildFileSystemItem(pathCursor, pathCursor.toFile()));
            }
        }
        return rootPath;
    }

    @Override
    public DownloadData download(String[] filePath, boolean tryUpdateCache, String password) throws Exception {
        Path path = buildPath(filePath);
        InputStream stream = null;
        if (!Files.exists(path)) {
            // try check if path is archive;
            Path cursor = path;
            while ((cursor = cursor.getParent()) != null) {
                if (Files.exists(cursor) && ArchiveUtil.isArchive(cursor)) {
                    stream = ArchiveUtil.downloadArchiveEntry(cursor, cursor.relativize(path).toString(), password);
                    break;
                }
            }
        } else {
            stream = Files.newInputStream(path);
        }
        return new DownloadData(path.getFileName().toString(), Files.probeContentType(path), null, stream);
    }

    private FileSystemItem buildFileSystemItem(Path path, File file) {
        String fullPath = path.toAbsolutePath().toString().substring(getEntity().getFileSystemRoot().length());
        if (!SystemUtils.IS_OS_LINUX) {
            fullPath = fullPath.replaceAll("\\\\", "/");
        }
        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }
        return new FileSystemItem(file.isDirectory(),
                file.isDirectory() && Objects.requireNonNull(file.list()).length == 0,
                file.getName(), fullPath, file.length(), file.lastModified(), null);
    }

    public static class RaspberryCacheFileSystem
            extends CachedFileSystem<RaspberryCacheFileSystem, RaspberryFile, RaspberryDeviceEntity> {

        public RaspberryCacheFileSystem(RaspberryFile source, RaspberryCacheFileSystem parent) {
            super(source, parent, false);
        }

        @SneakyThrows
        @Override
        protected RaspberryFile readFileFromServer(RaspberryDeviceEntity driver) {
            return new RaspberryFile(new File(getSource().getId()));
        }

        @Override
        protected int getFSMaxLevel() {
            return 5;
        }

        @Override
        protected RaspberryCacheFileSystem newInstance(RaspberryFile source, RaspberryCacheFileSystem parent) {
            return new RaspberryCacheFileSystem(source, parent);
        }

        @SneakyThrows
        @Override
        protected Collection<RaspberryFile> searchForChildren(RaspberryFile serverSource, RaspberryDeviceEntity driver) {
            File[] files = serverSource.file.listFiles();
            return files == null ? Collections.emptySet() : Stream.of(files).map(RaspberryFile::new).collect(Collectors.toList());
        }

        @Override
        public DownloadData download(RaspberryDeviceEntity drive) throws Exception {
            Path path = getSource().file.toPath();
            return new VendorFileSystem.DownloadData(this.getSource().getName(), Files.probeContentType(path), null,
                    Files.newInputStream(path));
        }

        @Override
        @SneakyThrows
        protected byte[] downloadContent(RaspberryDeviceEntity drive) {
            return Files.readAllBytes(getSource().file.toPath());
        }
    }

    @RequiredArgsConstructor
    private static class RaspberryFile implements CachedFileSystem.SourceFileCapability {
        private final File file;

        @Override
        public String getId() {
            return file.getPath();
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public Long getLastModifiedTime() {
            return file.lastModified();
        }

        @Override
        public boolean setLastModifiedTime(long time) {
            return file.setLastModified(time);
        }

        @Override
        public boolean isFolder() {
            return file.isDirectory();
        }

        @Override
        public Long size() {
            return file.length();
        }


    }
}
