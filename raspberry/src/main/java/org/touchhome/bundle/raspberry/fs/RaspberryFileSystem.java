package org.touchhome.bundle.raspberry.fs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.fs.CachedFileSystem;
import org.touchhome.bundle.api.fs.VendorFileSystem;
import org.touchhome.bundle.raspberry.model.RaspberryDeviceEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RaspberryFileSystem extends VendorFileSystem<RaspberryDeviceEntity, RaspberryFileSystem.RaspberryCacheFileSystem, RaspberryDeviceEntity> {

    public RaspberryFileSystem(RaspberryDeviceEntity entity, EntityContext entityContext) {
        super(entity, entityContext);
        this.setDrive(entity);
        this.dispose();
    }

    public void dispose() {
        setRoot(new RaspberryCacheFileSystem(new RaspberryFile(new File(getEntity().getFileSystemRoot())), null));
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
    public void upload(String[] parentPath, String fileName, byte[] content, boolean append) {
        Path path = Paths.get("", parentPath).resolve(fileName);
        if (!path.toString().startsWith(getEntity().getFileSystemRoot())) {
            path = Paths.get(getEntity().getFileSystemRoot()).resolve(path);
        }
        if (append) {
            Files.write(path, content, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } else {
            Files.write(path, content, StandardOpenOption.WRITE);
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(String[] filePath) {
        Path path = Paths.get("", filePath);
        if (!path.toString().startsWith(getEntity().getFileSystemRoot())) {
            path = Paths.get(getEntity().getFileSystemRoot()).resolve(path);
        }
        return Files.deleteIfExists(path);
    }

    public static class RaspberryCacheFileSystem extends CachedFileSystem<RaspberryCacheFileSystem, RaspberryFile, RaspberryDeviceEntity> {

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
        public long getLastModifiedTime() {
            return file.lastModified();
        }

        @Override
        public void setLastModifiedTime(long time) {
            file.setLastModified(time);
        }

        @Override
        public boolean isFolder() {
            return file.isDirectory();
        }
    }
}
