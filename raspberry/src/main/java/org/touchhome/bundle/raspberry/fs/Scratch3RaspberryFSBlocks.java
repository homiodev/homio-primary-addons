package org.touchhome.bundle.raspberry.fs;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.Scratch3BaseFileSystemExtensionBlocks;
import org.touchhome.bundle.raspberry.RaspberryDeviceEntity;
import org.touchhome.bundle.raspberry.RaspberryEntrypoint;

@Getter
@Component
public class Scratch3RaspberryFSBlocks extends Scratch3BaseFileSystemExtensionBlocks<RaspberryEntrypoint, RaspberryDeviceEntity> {

  public Scratch3RaspberryFSBlocks(EntityContext entityContext, RaspberryEntrypoint raspberryEntrypoint) {
    super("#B04828", entityContext, raspberryEntrypoint, RaspberryDeviceEntity.class);
  }
}
