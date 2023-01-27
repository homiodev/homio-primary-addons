package org.touchhome.bundle.z2m.workspace;

import static org.touchhome.bundle.z2m.workspace.Scratch3ZigBeeBlocks.ZIGBEE__BASE_URL;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.z2m.Z2MEntrypoint;

@Getter
@Component
final class Scratch3ZigBeeButtonsBlocks extends Scratch3ZigBeeExtensionBlocks {

    private final MenuBlock.ServerMenuBlock buttonSensorMenu;
    private final MenuBlock.ServerMenuBlock doubleButtonSensorMenu;
    //  private final MenuBlock.StaticMenuBlock<ButtonFireSignal> buttonSendSignalValueMenu;
    private final MenuBlock.StaticMenuBlock<ButtonEndpoint> buttonEndpointValueMenu;
    private final MenuBlock.StaticMenuBlock<ButtonEndpointGetter> buttonEndpointGetterValueMenu;
    private final EntityContext entityContext;

    Scratch3ZigBeeButtonsBlocks(EntityContext entityContext, Z2MEntrypoint z2MEntrypoint) {
        super("#A3623C", entityContext, z2MEntrypoint, "btn");
        this.entityContext = entityContext;

        this.buttonSensorMenu = menuServer("buttonSensorMenu", ZIGBEE__BASE_URL + "buttons", "Button Sensor", "-"/*,
            ZclOnOffCluster.CLUSTER_ID*/);
        this.doubleButtonSensorMenu = menuServer("doubleButtonSensorMenu", ZIGBEE__BASE_URL + "doubleButtons", "Button Sensor");

        //     this.buttonSendSignalValueMenu = menuStatic("buttonSendSignalValueMenu", ButtonFireSignal.class, ButtonFireSignal.on);
        this.buttonEndpointValueMenu = menuStatic("buttonEndpointValueMenu", ButtonEndpoint.class, ButtonEndpoint.Left);
        this.buttonEndpointGetterValueMenu = menuStatic("buttonEndpointGetterValueMenu", ButtonEndpointGetter.class, ButtonEndpointGetter.Left);

        blockTargetReporter(70, "value", "button value [BUTTON_SENSOR]", this::buttonStatusEvaluate,
            Scratch3ZigBeeBlock.class, block -> {
                block.addArgument(BUTTON_SENSOR, this.buttonSensorMenu);
                block.overrideColor("#853139");
            });

      /*  blockCommand(90, "turn_on_off", "turn [BUTTON_SIGNAL] button [BUTTON_SENSOR]", this::turnOnOffButtonHandler, block -> {
            block.addArgument(BUTTON_SENSOR, this.buttonSensorMenu);
            block.addArgument(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
            block.appendSpace();
            block.overrideColor("#853139");
        });*/

        blockTargetReporter(100, "double_value", "button [BUTTON_ENDPOINT] value [DOUBLE_BUTTON_SENSOR]",
            this::doubleButtonStatusEvaluate, Scratch3ZigBeeBlock.class, block -> {
                block.addArgument(DOUBLE_BUTTON_SENSOR, this.doubleButtonSensorMenu);
                block.addArgument(BUTTON_ENDPOINT, this.buttonEndpointGetterValueMenu);
                block.overrideColor("#A70F1D");
          /* block.addZigBeeEventHandler((ieeeAddress, endpointRef, consumer) -> {
            ButtonEndpointGetter buttonEndpoint = ButtonEndpointGetter.valueOf(endpointRef);
            zigBeeDeviceUpdateValueListener.addListener(
                ZigBeeEndpointUUID.require(ieeeAddress, ZclOnOffCluster.CLUSTER_ID, buttonEndpoint.value, null), consumer);
          });*/
            });

       /* blockCommand(110, "double_turn_on_off", "turn [BUTTON_SIGNAL] [BUTTON_ENDPOINT] button [DOUBLE_BUTTON_SENSOR]",
            this::turnOnOffDoubleButtonHandler, block -> {
                block.addArgument(DOUBLE_BUTTON_SENSOR, this.doubleButtonSensorMenu);
                block.addArgument(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
                block.addArgument(BUTTON_ENDPOINT, this.buttonEndpointValueMenu);
                block.appendSpace();
                block.overrideColor("#A70F1D");
            });*/
    }

    private void turnOnOffButtonHandler(WorkspaceBlock workspaceBlock) {
        /*ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, BUTTON_SENSOR, buttonSensorMenu);
        ButtonFireSignal buttonSignal = getButtonFireSignal(workspaceBlock);
        switchButton(workspaceBlock, buttonSignal, zigBeeDeviceEntity, null);*/
    }

    private void turnOnOffDoubleButtonHandler(WorkspaceBlock workspaceBlock) {
        /*ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, DOUBLE_BUTTON_SENSOR, doubleButtonSensorMenu);
        ButtonFireSignal buttonSignal = getButtonFireSignal(workspaceBlock);
        ButtonEndpoint buttonEndpoint = workspaceBlock.getMenuValue(BUTTON_ENDPOINT, this.buttonEndpointValueMenu);
        switchButton(workspaceBlock, buttonSignal, zigBeeDeviceEntity, buttonEndpoint.value);*/
    }

  /*  private ButtonFireSignal getButtonFireSignal(WorkspaceBlock workspaceBlock) {
        ButtonFireSignal buttonSignal;
        try {
            buttonSignal = workspaceBlock.getMenuValue(BUTTON_SIGNAL, this.buttonSendSignalValueMenu);
        } catch (Exception ex) {
            buttonSignal = workspaceBlock.getInputBoolean(BUTTON_SIGNAL) ? ButtonFireSignal.on : ButtonFireSignal.off;
        }
        return buttonSignal;
    }*/

    /*@SneakyThrows
    private void switchButton(WorkspaceBlock workspaceBlock, ButtonFireSignal buttonFireSignal,
        ZigBeeDeviceEntity zigBeeDeviceEntity, Integer buttonEndpointValue) {
        ZclOnOffCommand zclOnOffCommand = CommonUtils.newInstance(buttonFireSignal.zclOnOffCommand);
        workspaceBlock.logInfo("Switch button {}", zclOnOffCommand.getClass().getSimpleName());

        List<ZigBeeEndpointEntity> onOffEndpoints = zigBeeDeviceEntity.filterEndpoints(ZclOnOffCluster.CLUSTER_ID);

        if (onOffEndpoints.isEmpty()) {
            workspaceBlock.logErrorAndThrow("Unable to find endpoints with On/Off ability for device: " + zigBeeDeviceEntity);
        } else if (buttonEndpointValue != null) {
            onOffEndpoints = onOffEndpoints.stream().filter(c -> c.getAddress() == buttonEndpointValue).collect(Collectors.toList());
        }

        if (onOffEndpoints.isEmpty()) {
            workspaceBlock.logErrorAndThrow("Unable to find channel with On/Off ability for device: " + zigBeeDeviceEntity);
        }

        for (ZigBeeEndpointEntity onOffChannel : onOffEndpoints) {
            ZigBeeBaseChannelConverter beeBaseChannelConverter =
                zigBeeDeviceEntity.getEndpointRequired(onOffChannel.getAddress(), onOffChannel.getClusterName())
                                  .getService().getCluster();
            handleCommand(workspaceBlock, zigBeeDeviceEntity, beeBaseChannelConverter, zclOnOffCommand);
        }
    }*/

    private State buttonStatusEvaluate(WorkspaceBlock workspaceBlock) {
        /*ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, BUTTON_SENSOR, buttonSensorMenu);
        // TODO: if (statelessButtonStates.containsKey(zigBeeDeviceEntity.getIeeeAddress())) {
        //   return new DecimalType(statelessButtonStates.get(zigBeeDeviceEntity.getIeeeAddress()).getValue() == Boolean.TRUE ? 1 : 0);
        // }
        return getEndpointState(zigBeeDeviceEntity.getEndpoints());*/
        return null;
    }

    private State doubleButtonStatusEvaluate(WorkspaceBlock workspaceBlock) {
      /*  ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, DOUBLE_BUTTON_SENSOR, doubleButtonSensorMenu);
        return getEndpointState(zigBeeDeviceEntity.getEndpoints());*/
        return null;
    }

   /* private DecimalType getEndpointState(Set<ZigBeeEndpointEntity> onOffEndpoints) {
        for (ZigBeeEndpointEntity endpoint : onOffEndpoints) {
      *//* TODO: ScratchDeviceState scratchDeviceState = this.zigBeeDeviceUpdateValueListener.getDeviceState(
          new ZigBeeEndpointUUID(endpoint.getIeeeAddress(), endpoint.getClusterId(), endpoint.getEndpointId(), endpoint.getName())
      );
      if (scratchDeviceState != null) {
        return new DecimalType(scratchDeviceState.getState().intValue());
      }*//*
        }

        return new DecimalType(0);
    }*/

   /* @RequiredArgsConstructor
    private enum ButtonFireSignal {
        on(OnCommand.class), off(OffCommand.class), Toggle(ToggleCommand.class);

        private final Class<? extends ZclOnOffCommand> zclOnOffCommand;
    }*/

    @RequiredArgsConstructor
    private enum ButtonEndpoint {
        Left(1), Right(2), Both(null);
        private final Integer value;
    }

    @RequiredArgsConstructor
    private enum ButtonEndpointGetter {
        Left(1), Right(2), Any(null);
        private final Integer value;
    }

    private static final String BUTTON_ENDPOINT = "BUTTON_ENDPOINT";
    private static final String DOUBLE_BUTTON_SENSOR = "DOUBLE_BUTTON_SENSOR";
    private static final String BUTTON_SENSOR = "BUTTON_SENSOR";
    private static final String BUTTON_SIGNAL = "BUTTON_SIGNAL";
}
