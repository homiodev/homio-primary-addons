package org.touchhome.bundle.zigbee.workspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;

public class Scratch3ZigBeeBlock extends Scratch3Block {

  @Getter
  @JsonIgnore
  private final List<ZigBeeEventHandler> eventConsumers = new ArrayList<>();

  public Scratch3ZigBeeBlock(int order, String opcode, BlockType blockType, String text, Scratch3BlockHandler handler, Scratch3BlockEvaluateHandler evaluateHandler) {
    super(order, opcode, blockType, text, handler, evaluateHandler);
  }

  void addZigBeeEventHandler(ZigBeeEventHandler zigBeeEventHandler) {
    this.eventConsumers.add(zigBeeEventHandler);
  }

  public interface ZigBeeEventHandler {

    void handle(String ieeeAddress, String endpointRef, Consumer<ScratchDeviceState> consumer);
  }
}
