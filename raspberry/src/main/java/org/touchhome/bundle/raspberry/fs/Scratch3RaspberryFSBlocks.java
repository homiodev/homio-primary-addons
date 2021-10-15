package org.touchhome.bundle.raspberry.fs;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.Scratch3BaseFileSystemExtensionBlocks;
import org.touchhome.bundle.raspberry.RaspberryEntryPoint;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;

@Getter
@Component
public class Scratch3RaspberryFSBlocks extends Scratch3BaseFileSystemExtensionBlocks<RaspberryEntryPoint, RaspberryDeviceEntity> {

    public Scratch3RaspberryFSBlocks(EntityContext entityContext, RaspberryEntryPoint raspberryEntryPoint) {
        super("RaspbFS", "#B04828", entityContext, raspberryEntryPoint, RaspberryDeviceEntity.class);
    }
}
