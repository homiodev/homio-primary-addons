package org.touchhome.bundle.zigbee.workspace;

import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;

public class Scratch3ZigBeeBlock extends Scratch3Block {

  public Scratch3ZigBeeBlock(int order, String opcode, BlockType blockType, String text, Scratch3BlockHandler handler, Scratch3BlockEvaluateHandler evaluateHandler) {
    super(order, opcode, blockType, text, handler, evaluateHandler);
  }
}
