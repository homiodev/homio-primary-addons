package org.touchhome.bundle.zigbee.workspace;

import org.touchhome.bundle.api.BundleEntryPoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;

public abstract class Scratch3ZigBeeExtensionBlocks extends Scratch3ExtensionBlocks {

  public Scratch3ZigBeeExtensionBlocks(String color, EntityContext entityContext, BundleEntryPoint bundleEntryPoint, String idSuffix) {
    super(color, entityContext, bundleEntryPoint, idSuffix);
  }

  Scratch3ZigBeeBlock of(Scratch3ZigBeeBlock scratch3ZigBeeBlock, String color) {
    scratch3ZigBeeBlock.overrideColor(color);
    return scratch3ZigBeeBlock;
  }
}
