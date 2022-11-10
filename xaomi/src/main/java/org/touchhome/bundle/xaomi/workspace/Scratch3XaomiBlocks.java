package org.touchhome.bundle.xaomi.workspace;

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
import org.touchhome.bundle.xaomi.XaomiEntrypoint;
import org.touchhome.bundle.zigbee.ZigBeeEndpointUUID;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;

@Getter
@Component
public class Scratch3XaomiBlocks extends Scratch3ExtensionBlocks {

  private static final String CUBE_MODE_IDENTIFIER = "lumi.sensor_cube";

  private static final String CUBE_SENSOR = "CUBE_SENSOR";

  private final MenuBlock.StaticMenuBlock cubeEventMenu;
  private final MenuBlock.ServerMenuBlock cubeSensorMenu;

  public Scratch3XaomiBlocks(EntityContext entityContext, XaomiEntrypoint xaomiEntrypoint) {
    super("#856d21", entityContext, xaomiEntrypoint);
    setParent("zigbee");

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
  }

  private State cubeLastValueEvaluate(WorkspaceBlock workspaceBlock) {
    String ieeeAddress = fetchIEEEAddress(workspaceBlock);
   /* TODO: ScratchDeviceState deviceState = this.zigBeeDeviceUpdateValueListener.getDeviceState(
        ZigBeeEndpointUUID.require(ieeeAddress, ZclMultistateInputBasicCluster.CLUSTER_ID, null, null),
        ZigBeeEndpointUUID.require(ieeeAddress, ZclAnalogInputBasicCluster.CLUSTER_ID, null, null));
    if (deviceState != null) {
      return deviceState.getState();
    }*/
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
      Consumer<Object> consumer = sds -> {
        /* TODO: CubeValueDescriptor cubeValueDescriptor = new CubeValueDescriptor(sds);
        if (cubeValueDescriptor.match(expectedMenuValue, tapSide, moveSide)) {
          lock.signalAll();
        }*/
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

  private void addZigBeeEventListener(String nodeIEEEAddress, int clusterId, Consumer<Object> consumer) {
    ZigBeeEndpointUUID zigBeeEndpointUUID = ZigBeeEndpointUUID.require(nodeIEEEAddress, clusterId, null, null);
    entityContext.event().addEventListener(zigBeeEndpointUUID.asKey(), consumer);
  }
}
