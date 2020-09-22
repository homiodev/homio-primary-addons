package org.touchhome.bundle.zigbee.workspace;

import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.scratch.Scratch3ExtensionBlocks;

public abstract class Scratch3ZigbeeExtensionBlocks extends Scratch3ExtensionBlocks {

    public Scratch3ZigbeeExtensionBlocks(String color, EntityContext entityContext, BundleEntrypoint bundleEntrypoint, String idSuffix) {
        super(color, entityContext, bundleEntrypoint, idSuffix);
    }

    Scratch3ZigbeeBlock of(Scratch3ZigbeeBlock scratch3ZigbeeBlock, String color) {
        scratch3ZigbeeBlock.overrideColor(color);
        return scratch3ZigbeeBlock;
    }
}
