package org.touchhome.bundle.z2m.workspace;

import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;

public abstract class Scratch3ZigBeeExtensionBlocks extends Scratch3ExtensionBlocks {

    public Scratch3ZigBeeExtensionBlocks(String color, EntityContext entityContext, BundleEntrypoint bundleEntrypoint, String idSuffix) {
        super(color, entityContext, bundleEntrypoint, idSuffix);
    }

    Scratch3ZigBeeBlock of(Scratch3ZigBeeBlock scratch3ZigBeeBlock, String color) {
        scratch3ZigBeeBlock.overrideColor(color);
        return scratch3ZigBeeBlock;
    }
}
