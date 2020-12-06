package org.touchhome.bundle.zigbee.workspace;

import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.scratch.Scratch3ExtensionBlocks;

public abstract class Scratch3ZigBeeExtensionBlocks extends Scratch3ExtensionBlocks {

    public Scratch3ZigBeeExtensionBlocks(String color, EntityContext entityContext, BundleEntryPoint bundleEntryPoint, String idSuffix) {
        super(color, entityContext, bundleEntryPoint, idSuffix);
    }

    Scratch3ZigBeeBlock of(Scratch3ZigBeeBlock scratch3ZigbeeBlock, String color) {
        scratch3ZigbeeBlock.overrideColor(color);
        return scratch3ZigbeeBlock;
    }
}
