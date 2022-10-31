package org.touchhome.bundle.zigbee.workspace;

import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.ZIGBEE__BASE_URL;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.getZigBeeDevice;
import static org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlocks.handleCommand;

import com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OffCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.OnCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.ToggleCommand;
import com.zsmartsystems.zigbee.zcl.clusters.onoff.ZclOnOffCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.zigbee.ZigBeeBundleEntryPoint;
import org.touchhome.bundle.zigbee.ZigBeeDeviceStateUUID;
import org.touchhome.bundle.zigbee.ZigBeeNodeDescription;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEndpoint;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.common.model.UpdatableValue;
import org.touchhome.common.util.CommonUtils;

@Getter
@Component
final class Scratch3ZigBeeButtonsBlocks extends Scratch3ZigBeeExtensionBlocks {

  private static final String BUTTON_ENDPOINT = "BUTTON_ENDPOINT";
  private static final String DOUBLE_BUTTON_SENSOR = "DOUBLE_BUTTON_SENSOR";
  private static final String BUTTON_SENSOR = "BUTTON_SENSOR";
  private static final String BUTTON_SIGNAL = "BUTTON_SIGNAL";

  private final MenuBlock.ServerMenuBlock buttonSensorMenu;
  private final MenuBlock.ServerMenuBlock doubleButtonSensorMenu;
  private final MenuBlock.StaticMenuBlock<ButtonFireSignal> buttonSendSignalValueMenu;
  private final MenuBlock.StaticMenuBlock<ButtonEndpoint> buttonEndpointValueMenu;
  private final MenuBlock.StaticMenuBlock<ButtonEndpointGetter> buttonEndpointGetterValueMenu;


  private final ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener;
  private final EntityContext entityContext;

  private final Map<String, UpdatableValue<Boolean>> statelessButtonStates = new HashMap<>();

  Scratch3ZigBeeButtonsBlocks(EntityContext entityContext, ZigBeeBundleEntryPoint zigBeeBundleEntryPoint,
      ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
    super("#A3623C", entityContext, zigBeeBundleEntryPoint, "btn");
    this.entityContext = entityContext;
    this.zigBeeDeviceUpdateValueListener = zigBeeDeviceUpdateValueListener;

    this.buttonSensorMenu = menuServer("buttonSensorMenu", ZIGBEE__BASE_URL + "buttons", "Button Sensor", "-",
        ZclOnOffCluster.CLUSTER_ID);
    this.doubleButtonSensorMenu = menuServer("doubleButtonSensorMenu", ZIGBEE__BASE_URL + "doubleButtons", "Button Sensor");

    this.buttonSendSignalValueMenu = menuStatic("buttonSendSignalValueMenu", ButtonFireSignal.class, ButtonFireSignal.on);
    this.buttonEndpointValueMenu = menuStatic("buttonEndpointValueMenu", ButtonEndpoint.class, ButtonEndpoint.Left);
    this.buttonEndpointGetterValueMenu = menuStatic("buttonEndpointGetterValueMenu", ButtonEndpointGetter.class, ButtonEndpointGetter.Left);

    blockTargetReporter(70, "value", "button value [BUTTON_SENSOR]", this::buttonStatusEvaluate,
        Scratch3ZigBeeBlock.class, block -> {
          block.addArgument(BUTTON_SENSOR, this.buttonSensorMenu);
          block.overrideColor("#853139");
          block.addZigBeeEventHandler(
              (ieeeAddress, endpointRef, consumer) -> zigBeeDeviceUpdateValueListener.addModelIdentifierListener("lumi.remote",
                  consumer));
          block.allowLinkBoolean((varId, workspaceBlock) -> {
            ZigBeeDeviceEntity zigBeeDevice = getZigBeeDevice(workspaceBlock, BUTTON_SENSOR, buttonSensorMenu);
            allowButtonLinkBoolean(zigBeeDeviceUpdateValueListener, varId, workspaceBlock, zigBeeDevice.getIeeeAddress(), null,
                null);
          });
          block.setZigBeeLinkGenerator(
              (endpoint, zigBeeDevice, varGroup, varName) -> block.codeGenerator("zigbee")
                  .setMenu(this.buttonSensorMenu, zigBeeDevice.getNodeIeeeAddress())
                  .generateBooleanLink(varGroup, varName, entityContext), ZclOnOffCluster.CLUSTER_ID, 1);
        });

    blockCommand(90, "turn_on_off", "turn [BUTTON_SIGNAL] button [BUTTON_SENSOR]", this::turnOnOffButtonHandler, block -> {
      block.addArgument(BUTTON_SENSOR, this.buttonSensorMenu);
      block.addArgument(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
      block.appendSpace();
      block.overrideColor("#853139");
    });

    blockTargetReporter(100, "double_value", "button [BUTTON_ENDPOINT] value [DOUBLE_BUTTON_SENSOR]",
        this::doubleButtonStatusEvaluate, Scratch3ZigBeeBlock.class, block -> {
          block.addArgument(DOUBLE_BUTTON_SENSOR, this.doubleButtonSensorMenu);
          block.addArgument(BUTTON_ENDPOINT, this.buttonEndpointGetterValueMenu);
          block.overrideColor("#A70F1D");
          block.addZigBeeEventHandler((ieeeAddress, endpointRef, consumer) -> {
            ButtonEndpointGetter buttonEndpoint = ButtonEndpointGetter.valueOf(endpointRef);
            zigBeeDeviceUpdateValueListener.addListener(
                ZigBeeDeviceStateUUID.require(ieeeAddress, ZclOnOffCluster.CLUSTER_ID, buttonEndpoint.value, null), consumer);
          });
          // add link boolean value
          block.allowLinkBoolean((varId, workspaceBlock) -> {
            ZigBeeDeviceEntity zigBeeDevice = getZigBeeDevice(workspaceBlock, DOUBLE_BUTTON_SENSOR, doubleButtonSensorMenu);
            ButtonEndpointGetter buttonEndpointGetter =
                workspaceBlock.getMenuValue(BUTTON_ENDPOINT, this.buttonEndpointGetterValueMenu);
            allowButtonLinkBoolean(zigBeeDeviceUpdateValueListener, varId, workspaceBlock, zigBeeDevice.getIeeeAddress(),
                buttonEndpointGetter.value, buttonEndpointGetter.name());
          });
          block.setZigBeeLinkGenerator(
              (endpoint, zigBeeDevice, varGroup, varName) -> {
                block.codeGenerator("zigbee")
                    .setMenu(this.buttonEndpointGetterValueMenu, ButtonEndpoint.of(endpoint.getEndpointId()))
                    .setMenu(this.doubleButtonSensorMenu, zigBeeDevice.getNodeIeeeAddress())
                    .generateBooleanLink(varGroup, varName, entityContext);
              }, ZclOnOffCluster.CLUSTER_ID, 2);
        });

    blockCommand(110, "double_turn_on_off", "turn [BUTTON_SIGNAL] [BUTTON_ENDPOINT] button [DOUBLE_BUTTON_SENSOR]",
        this::turnOnOffDoubleButtonHandler, block -> {
          block.addArgument(DOUBLE_BUTTON_SENSOR, this.doubleButtonSensorMenu);
          block.addArgument(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
          block.addArgument(BUTTON_ENDPOINT, this.buttonEndpointValueMenu);
          block.appendSpace();
          block.overrideColor("#A70F1D");
        });

    // add descriptions
    this.addDescriptions(zigBeeDeviceUpdateValueListener);
  }

  private void turnOnOffButtonHandler(WorkspaceBlock workspaceBlock) {
    ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, BUTTON_SENSOR, buttonSensorMenu);
    ButtonFireSignal buttonSignal = getButtonFireSignal(workspaceBlock);
    switchButton(workspaceBlock, buttonSignal, zigBeeDeviceEntity, null);
  }

  private void turnOnOffDoubleButtonHandler(WorkspaceBlock workspaceBlock) {
    ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, DOUBLE_BUTTON_SENSOR, doubleButtonSensorMenu);
    ButtonFireSignal buttonSignal = getButtonFireSignal(workspaceBlock);
    ButtonEndpoint buttonEndpoint = workspaceBlock.getMenuValue(BUTTON_ENDPOINT, this.buttonEndpointValueMenu);
    switchButton(workspaceBlock, buttonSignal, zigBeeDeviceEntity, buttonEndpoint.value);
  }

  private ButtonFireSignal getButtonFireSignal(WorkspaceBlock workspaceBlock) {
    ButtonFireSignal buttonSignal;
    try {
      buttonSignal = workspaceBlock.getMenuValue(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
    } catch (Exception ex) {
      buttonSignal = workspaceBlock.getInputBoolean(BUTTON_SIGNAL) ? ButtonFireSignal.on : ButtonFireSignal.off;
    }
    return buttonSignal;
  }

  @SneakyThrows
  private void switchButton(WorkspaceBlock workspaceBlock, ButtonFireSignal buttonFireSignal,
      ZigBeeDeviceEntity zigBeeDeviceEntity, Integer buttonEndpointValue) {
    ZclOnOffCommand zclOnOffCommand = CommonUtils.newInstance(buttonFireSignal.zclOnOffCommand);
    workspaceBlock.logInfo("Switch button {}", zclOnOffCommand.getClass().getSimpleName());
    ZigBeeNodeDescription zigBeeNodeDescription = zigBeeDeviceEntity.getZigBeeNodeDescription();

    if (zigBeeNodeDescription == null) {
      workspaceBlock.logErrorAndThrow("Unable to switch button. Node not discovered");
      return;
    }

    List<ZigBeeDeviceEndpoint> onOffEndpoints = zigBeeDeviceEntity.getEndpoints(ZclOnOffCluster.CLUSTER_ID);

    if (onOffEndpoints.isEmpty()) {
      workspaceBlock.logErrorAndThrow("Unable to find endpoints with On/Off ability for device: " + zigBeeDeviceEntity);
    } else if (buttonEndpointValue != null) {
      onOffEndpoints = onOffEndpoints.stream().filter(c -> c.getEndpointId() == buttonEndpointValue).collect(Collectors.toList());
    }

    if (onOffEndpoints.isEmpty()) {
      workspaceBlock.logErrorAndThrow("Unable to find channel with On/Off ability for device: " + zigBeeDeviceEntity);
    }

    for (ZigBeeDeviceEndpoint onOffChannel : onOffEndpoints) {
      ZigBeeBaseChannelConverter beeBaseChannelConverter =
          zigBeeDeviceEntity.getEndpointRequired(onOffChannel.getIeeeAddress(), onOffChannel.getClusterId(), onOffChannel.getEndpointId(),
              onOffChannel.getName()).getService().getChannel();
      handleCommand(workspaceBlock, zigBeeDeviceEntity, beeBaseChannelConverter, zclOnOffCommand);
    }
  }

  private State buttonStatusEvaluate(WorkspaceBlock workspaceBlock) {
    ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, BUTTON_SENSOR, buttonSensorMenu);
    if (statelessButtonStates.containsKey(zigBeeDeviceEntity.getIeeeAddress())) {
      return new DecimalType(statelessButtonStates.get(zigBeeDeviceEntity.getIeeeAddress()).getValue() == Boolean.TRUE ? 1 : 0);
    }
    return getEndpointState(zigBeeDeviceEntity.getEndpoints());
  }

  private State doubleButtonStatusEvaluate(WorkspaceBlock workspaceBlock) {
    ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, DOUBLE_BUTTON_SENSOR, doubleButtonSensorMenu);
    return getEndpointState(zigBeeDeviceEntity.getEndpoints());
  }

  private DecimalType getEndpointState(Set<ZigBeeDeviceEndpoint> onOffEndpoints) {
    for (ZigBeeDeviceEndpoint endpoint : onOffEndpoints) {
      ScratchDeviceState scratchDeviceState = this.zigBeeDeviceUpdateValueListener.getDeviceState(
          new ZigBeeDeviceStateUUID(endpoint.getIeeeAddress(), endpoint.getClusterId(), endpoint.getEndpointId(), endpoint.getName())
      );
      if (scratchDeviceState != null) {
        return new DecimalType(scratchDeviceState.getState().intValue());
      }
    }

    return new DecimalType(0);
  }

  private void addDescriptions(ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener) {
    zigBeeDeviceUpdateValueListener.addDescribeHandlerByModel("lumi.remote", state -> "Remote button", true);
    zigBeeDeviceUpdateValueListener.addModelIdentifierListener("lumi.remote", state -> {
      String ieeeAddress = state.getZigBeeDevice().getNodeIeeeAddress().toString();
      if (!statelessButtonStates.containsKey(ieeeAddress)) {
        statelessButtonStates.put(ieeeAddress, UpdatableValue.wrap(Boolean.FALSE, ieeeAddress + "_state"));
      }
      statelessButtonStates.get(ieeeAddress).update(!statelessButtonStates.get(ieeeAddress).getValue());
    });
    zigBeeDeviceUpdateValueListener.addDescribeHandlerByClusterId(ZclOnOffCluster.CLUSTER_ID, state -> {
      String btnIndex = "";
      if (state.getZigBeeDevice().getChannelCount(ZclOnOffCluster.CLUSTER_ID) == 2) {
        int endpointId = state.getUuid().getEndpointId();
        btnIndex = endpointId == 1 ? "Left " : endpointId == 2 ? "Right " : "Undefined ";
      }
      return "Button " + btnIndex + "click: " + state.getState();
    }, false);
  }

  private void allowButtonLinkBoolean(ZigBeeDeviceUpdateValueListener zigBeeDeviceUpdateValueListener, String varId,
      WorkspaceBlock workspaceBlock, String ieeeAddress, Integer buttonEndpointValue,
      String buttonEndpointName) {
    ZigBeeDeviceStateUUID zigBeeDeviceStateUUID =
        ZigBeeDeviceStateUUID.require(ieeeAddress, ZclOnOffCluster.CLUSTER_ID, buttonEndpointValue, null);
    // listen from device and write to variable
    zigBeeDeviceUpdateValueListener.addLinkListener(zigBeeDeviceStateUUID, varId, "On/Off" + buttonEndpointName, state -> {
      entityContext.var().setIfNotMatch(varId, state.getState().boolValue());
    });
    // listen boolean variable and fire events to device
    entityContext.event().addEventListener(varId, "workspace-zigbee-boolean-listen-" + varId, value -> {
      if (value instanceof Boolean) {
        boolean val = (Boolean) value;
        ScratchDeviceState deviceState =
            zigBeeDeviceUpdateValueListener.getDeviceState(zigBeeDeviceStateUUID);
        if (deviceState == null || deviceState.getState().boolValue() != val) {
          switchButton(workspaceBlock, ButtonFireSignal.of(val),
              getZigBeeDevice(workspaceBlock, ieeeAddress), buttonEndpointValue);
        }
      }
    });
  }

  @RequiredArgsConstructor
  private enum ButtonFireSignal {
    on(OnCommand.class), off(OffCommand.class), Toggle(ToggleCommand.class);

    private final Class<? extends ZclOnOffCommand> zclOnOffCommand;

    public static ButtonFireSignal of(boolean on) {
      return on ? ButtonFireSignal.on : ButtonFireSignal.off;
    }
  }

  @RequiredArgsConstructor
  private enum ButtonEndpoint {
    Left(1), Right(2), Both(null);
    private final Integer value;

    public static ButtonEndpoint of(int endpointId) {
      for (ButtonEndpoint value : ButtonEndpoint.values()) {
        if (value.value == endpointId) {
          return value;
        }
      }
      throw new RuntimeException("Unable to find Button endpoint");
    }
  }

  @RequiredArgsConstructor
  private enum ButtonEndpointGetter {
    Left(1), Right(2), Any(null);
    private final Integer value;
  }
}
