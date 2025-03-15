package org.homio.addon.mqtt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;
import org.homio.api.fs.FileSystemProvider;
import org.homio.api.fs.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
@AllArgsConstructor
public class MQTTFileSystem implements FileSystemProvider {

  private MQTTClientEntity entity;

  @Override
  @SneakyThrows
  public @NotNull Set<TreeNode> getChildren(@NotNull String id) {
    return Collections.emptySet();
  }

  @Override
  @SneakyThrows
  public Set<TreeNode> getChildrenRecursively(@NotNull String parentId) {
    throw new IllegalStateException();
  }

  @Override
  @SneakyThrows
  public @NotNull Set<TreeNode> toTreeNodes(@NotNull Set<String> ids) {
    throw new IllegalStateException();
  }

  @Override
  @SneakyThrows
  public @NotNull InputStream getEntryInputStream(@NotNull String id) {
    throw new NotImplementedException();
  }

  @Override
  public Long getTotalSpace() {
    return null;
  }

  @Override
  public Long getUsedSpace() {
    return null;
  }

  @Override
  public @NotNull String getFileSystemId() {
    return entity.getEntityID();
  }

  @Override
  public boolean restart(boolean force) {
    return true;
  }

  @Override
  public void setEntity(@NotNull Object entity) {
    this.entity = (MQTTClientEntity) entity;
  }

  @Override
  public boolean exists(@NotNull String id) {
    return true;
  }

  @Override
  @SneakyThrows
  public @NotNull TreeNode delete(@NotNull Set<String> ids) {
    throw new NotImplementedException();
  }

  @Override
  @SneakyThrows
  public TreeNode create(@NotNull String parentId, @NotNull String name, boolean isDir, UploadOption uploadOption) {
    throw new NotImplementedException();
  }

  @Override
  @SneakyThrows
  public TreeNode rename(@NotNull String id, @NotNull String newName, UploadOption uploadOption) {
    throw new NotImplementedException();
  }

  @Override
  @SneakyThrows
  public @NotNull TreeNode copy(@NotNull Collection<TreeNode> entries, @NotNull String targetId, @NotNull UploadOption uploadOption) {
    throw new NotImplementedException();
  }

  @Override
  public Set<TreeNode> loadTreeUpToChild(@Nullable String rootPath, @NotNull String id) {
    return null;
  }
}
