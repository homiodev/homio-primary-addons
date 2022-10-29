package org.touchhome.bundle.xaomi.workspace;

import static org.touchhome.bundle.xaomi.workspace.MagicCubeHandler.CubeValueDescriptor;
import static org.touchhome.bundle.xaomi.workspace.MagicCubeHandler.MagicCubeEvent;
import static org.touchhome.bundle.xaomi.workspace.MagicCubeHandler.MoveSide;
import static org.touchhome.bundle.xaomi.workspace.MagicCubeHandler.TapSide;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.ZIGBEE_MODEL_URL;

import com.zsmartsystems.zigbee.zcl.clusters.ZclAnalogInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMultistateInputBasicCluster;
import java.util.function.Consumer;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.xaomi.XaomiEntryPoint;
import org.touchhome.bundle.zigbee.ZigBeeDeviceStateUUID;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.workspace.ScratchDeviceState;
import org.touchhome.bundle.zigbee.workspace.ZigBeeDeviceUpdateValueListener;

@Getter
@Component
public class Scratch3XaomiBlocks extends Scratch3ExtensionBlocks {

  private static final String CUBE_MODE_IDENTIFIER = "lumi.sensor_cube";

  private static final String CUBE_SENSOR = "CUBE_SENSOR";

  private final MenuBlock.StaticMenuBlock cubeEventMenu;
  private final MenuBlock.ServerMenuBlock cubeSensorMenu;
  private final ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener;

  public Scratch3XaomiBlocks(EntityContext entityContext,
      ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener,
      XaomiEntryPoint xaomiEntryPoint) {
    super("#856d21", entityContext, xaomiEntryPoint);
    setParent("zigbee");
    this.zigBeeDeviceUpdateValueListener = zigBeeDeviceUpdateValueListener;

    this.cubeEventMenu = menuStatic("cubeEventMenu", MagicCubeEvent.class, MagicCubeEvent.ANY_EVENT);
    this.cubeEventMenu.subMenu(MagicCubeEvent.MOVE, MoveSide.class);
    this.cubeEventMenu.subMenu(MagicCubeEvent.TAP_TWICE, TapSide.class);

    this.cubeSensorMenu = menuServer("cubeSensorMenu", ZIGBEE_MODEL_URL + CUBE_MODE_IDENTIFIER, "Magic Cube");

    blockHat(1, "when_cube_event", "Cube [CUBE_SENSOR] event [EVENT]",
        this::magicCubeEventHandler, block -> {
          block.addArgument(CUBE_SENSOR, this.cubeSensorMenu);
          block.addArgument(EVENT, this.cubeEventMenu);
        });

    blockReporter(2, "cube_value", "Cube [CUBE_SENSOR] last value [EVENT]", this::cubeLastValueEvaluate, block -> {
      block.addArgument(CUBE_SENSOR, this.cubeSensorMenu);
      block.addArgument(EVENT, this.cubeEventMenu);
    });

    zigBeeDeviceUpdateValueListener.addDescribeHandlerByModel(CUBE_MODE_IDENTIFIER,
        (state) -> "Magic cube <" + new CubeValueDescriptor(state) + ">", false);
  }

  private State cubeLastValueEvaluate(WorkspaceBlock workspaceBlock) {
    String ieeeAddress = fetchIEEEAddress(workspaceBlock);
    ScratchDeviceState deviceState = this.zigBeeDeviceUpdateValueListener.getDeviceState(
        ZigBeeDeviceStateUUID.require(ieeeAddress, ZclMultistateInputBasicCluster.CLUSTER_ID, null, null),
        ZigBeeDeviceStateUUID.require(ieeeAddress, ZclAnalogInputBasicCluster.CLUSTER_ID, null, null));
    if (deviceState != null) {
      return deviceState.getState();
    }
    return null;
  }

  private void magicCubeEventHandler(WorkspaceBlock workspaceBlock) {
    workspaceBlock.handleNext(next -> {
      String expectedMenuValueStr = workspaceBlock.getMenuValue(EVENT, this.cubeEventMenu, String.class);
      MagicCubeEvent expectedMenuValue = MagicCubeEvent.getEvent(expectedMenuValueStr);
      final TapSide tapSide = expectedMenuValue == MagicCubeEvent.TAP_TWICE ? TapSide.valueOf(expectedMenuValueStr) : null;
      final MoveSide moveSide = expectedMenuValue == MagicCubeEvent.MOVE ? MoveSide.valueOf(expectedMenuValueStr) : null;

      String ieeeAddress = fetchIEEEAddress(workspaceBlock);
      if (ieeeAddress == null) {
        return;
      }

      BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock);
      Consumer<ScratchDeviceState> consumer = sds -> {
        CubeValueDescriptor cubeValueDescriptor = new CubeValueDescriptor(sds);
        if (cubeValueDescriptor.match(expectedMenuValue, tapSide, moveSide)) {
          lock.signalAll();
        }
      };

      addZigBeeEventListener(ieeeAddress, ZclMultistateInputBasicCluster.CLUSTER_ID, consumer);
      addZigBeeEventListener(ieeeAddress, ZclAnalogInputBasicCluster.CLUSTER_ID, consumer);

      workspaceBlock.subscribeToLock(lock, next::handle);
    });
  }

  private String fetchIEEEAddress(WorkspaceBlock workspaceBlock) {
    String ieeeAddress = workspaceBlock.getMenuValue(CUBE_SENSOR, this.cubeSensorMenu);
    ZigBeeDeviceEntity device = entityContext.getEntity(ZigBeeDeviceEntity.PREFIX + ieeeAddress);
    if (device == null) {
      workspaceBlock.logErrorAndThrow("Unable to find Magic cube entity: <{}>", ieeeAddress);
    }
    return ieeeAddress;
  }

  private void addZigBeeEventListener(String nodeIEEEAddress, int clusterId, Consumer<ScratchDeviceState> consumer) {
    ZigBeeDeviceStateUUID zigBeeDeviceStateUUID = ZigBeeDeviceStateUUID.require(nodeIEEEAddress, clusterId, null, null);
    this.zigBeeDeviceUpdateValueListener.addListener(zigBeeDeviceStateUUID, consumer);
  }
}
