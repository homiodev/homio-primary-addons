package org.touchhome.bundle.zigbee.workspace;

import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.scratch.Scratch3ExtensionBlocks;

public abstract class Scratch3ZigbeeExtensionBlocks extends Scratch3ExtensionBlocks {

    public Scratch3ZigbeeExtensionBlocks(String color, EntityContext entityContext, BundleEntrypoint bundleEntrypoint, String idSuffix) {
        super(color, entityContext, bundleEntrypoint, idSuffix);
    }
}
