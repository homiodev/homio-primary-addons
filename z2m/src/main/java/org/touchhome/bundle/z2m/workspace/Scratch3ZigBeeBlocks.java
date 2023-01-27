package org.touchhome.bundle.z2m.workspace;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.z2m.Z2MEntrypoint;

@Getter
@Component
public class Scratch3ZigBeeBlocks extends Scratch3ZigBeeExtensionBlocks {

    public static final String ZIGBEE__BASE_URL = "rest/zigbee/option/";

    public static final String ZIGBEE_CLUSTER_ID_URL = ZIGBEE__BASE_URL + "zcl/";
    public static final String ZIGBEE_CLUSTER_NAME_URL = ZIGBEE__BASE_URL + "clusterName/";
    public static final String ZIGBEE_MODEL_URL = ZIGBEE__BASE_URL + "model/";
    public static final String ZIGBEE_ALARM_URL = ZIGBEE__BASE_URL + "alarm";

    public Scratch3ZigBeeBlocks(EntityContext entityContext, Z2MEntrypoint z2MEntrypoint) {
        super("#6d4747", entityContext, z2MEntrypoint, null);

        // Items
       /* blockHat(10, "when_event_received", "when got [EVENT] event", this::whenEventReceivedHandler, block -> {
            block.addArgument(EVENT, ArgumentType.reference);
        });

        blockReporter(20, "time_since_last_event", "time since last event [EVENT]", this::timeSinceLastEventEvaluate, block -> {
            block.addArgument(EVENT, ArgumentType.reference);
            block.appendSpace();
        });*/
    }

    /*public static void handleCommand(WorkspaceBlock workspaceBlock, ZigBeeDeviceEntity zigBeeDeviceEntity,
        ZigBeeBaseChannelConverter zigBeeBaseChannelConverter, ZclCommand zclCommand) {
        try {
            Future<CommandResult> result = zigBeeBaseChannelConverter.handleCommand(zclCommand);
            if (result != null) {
                CommandResult commandResult = result.get(10, TimeUnit.SECONDS);
                if (!commandResult.isSuccess()) {
                    workspaceBlock.logWarn("Send button command: <{}> to device: <{}> not success", zclCommand,
                        zigBeeDeviceEntity.getIeeeAddress());
                }
            }
        } catch (Exception ex) {
            workspaceBlock.logError("Unable to execute command <{}>", zclCommand, ex);
        }
    }*/

    /*static State fetchState(List<ZigBeeEndpointEntity> endpoints) {
        return endpoints.isEmpty() ? new DecimalType(0F) : endpoints.get(0).getLastState();
    }*/

    /*static List<ZigBeeEndpointEntity> getZigBeeEndpoints(WorkspaceBlock workspaceBlock, String key, ServerMenuBlock menuBlock, Integer[] clusterIds) {
        return getZigBeeDevice(workspaceBlock, workspaceBlock.getMenuValue(key, menuBlock)).filterEndpoints(clusterIds[0]);
    }*/

    /*static ZigBeeDeviceBaseEntity getZigBeeDevice(WorkspaceBlock workspaceBlock, String key, MenuBlock.ServerMenuBlock menuBlock) {
        return getZigBeeDevice(workspaceBlock, workspaceBlock.getMenuValue(key, menuBlock));
    }

    static ZigBeeDeviceBaseEntity getZigBeeDevice(WorkspaceBlock workspaceBlock, String ieeeAddress) {
        if (ieeeAddress == null) {
            workspaceBlock.logErrorAndThrow("Unable to find ieeeAddress");
        }
        ZigBeeDeviceBaseEntity entity = workspaceBlock.getEntityContext().getEntity(ZigBeeDeviceBaseEntity.PREFIX + ieeeAddress);
        if (entity == null) {
            workspaceBlock.logErrorAndThrow("Unable to find ZigBee node with IEEEAddress: <{}>", ieeeAddress);
        }
        return entity;
    }

    private void whenEventReceivedHandler(WorkspaceBlock workspaceBlock) {
        workspaceBlock.handleNext(
            next -> {
                WorkspaceBlock workspaceEventBlock = workspaceBlock.getInputWorkspaceBlock("EVENT");

                Scratch3Block scratch3Block = this.getBlocksMap().get(workspaceEventBlock.getOpcode());
                Pair<String, MenuBlock> sensorMenuBlock = scratch3Block.findMenuBlock(k -> k.endsWith("_SENSOR"));
                Pair<String, MenuBlock> endpointMenuBlock = scratch3Block.findMenuBlock(s -> s.endsWith("_ENDPOINT"));

                WorkspaceBlock sensorMenuRef = workspaceEventBlock.getInputWorkspaceBlock(sensorMenuBlock.getKey());
                String ieeeAddress = sensorMenuRef.getField(sensorMenuBlock.getValue().getName());
                String endpointRef = null;

                if (endpointMenuBlock != null) {
                    WorkspaceBlock endpointMenuRef = workspaceEventBlock.getInputWorkspaceBlock(endpointMenuBlock.getKey());
                    endpointRef = endpointMenuRef.getField(endpointMenuBlock.getValue().getName());
                }

                ZigBeeDeviceEntity zigBeeDeviceEntity = getZigBeeDevice(workspaceBlock, ieeeAddress);
                if (zigBeeDeviceEntity == null) {
                    throw new IllegalStateException("Unable to find ZigBee device entity <" + ieeeAddress + ">");
                }

                BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock);
                boolean availableReceiveEvents = false;

    *//*if (scratch3Block instanceof Scratch3ZigBeeBlock) {
      TODO: for (Scratch3ZigBeeBlock.ZigBeeEventHandler eventConsumer : ((Scratch3ZigBeeBlock) scratch3Block).getEventConsumers()) {
        availableReceiveEvents = true;
        eventConsumer.handle(ieeeAddress, endpointRef, state -> lock.signalAll());
      }
    }*//*

                Integer[] clusters = ((MenuBlock.ServerMenuBlock) sensorMenuBlock.getValue()).getClusters();
                if (clusters != null) {
                    availableReceiveEvents = true;
                    for (Integer cluster : clusters) {
                        entityContext.event().addEventListener(ieeeAddress + "_" + cluster, lock::signalAll);
                    }
                }

                if (!availableReceiveEvents) {
                    throw new IllegalStateException("Unable to find event listener");
                }

                while (!Thread.currentThread().isInterrupted()) {
                    if (lock.await(workspaceBlock)) {
                        next.handle();
                    }
                }
            });
    }

    private State timeSinceLastEventEvaluate(WorkspaceBlock workspaceBlock) {
        WorkspaceBlock workspaceEventBlock = workspaceBlock.getInputWorkspaceBlock("EVENT");

        Scratch3Block scratch3Block = this.getBlocksMap().get(workspaceEventBlock.getOpcode());
        Pair<String, MenuBlock> menuBlock = scratch3Block.findMenuBlock(k -> k.endsWith("_SENSOR"));

        Integer[] clusters = ((MenuBlock.ServerMenuBlock) menuBlock.getValue()).getClusters();

        List<ZigBeeEndpointEntity> endpoints = getZigBeeEndpoints(workspaceEventBlock, menuBlock.getKey(), (ServerMenuBlock) menuBlock.getValue(), clusters);
        if (!endpoints.isEmpty()) {
            long timestamp = endpoints.get(0).getLastAnswerFromEndpoint();
            return new DecimalType((System.currentTimeMillis() - timestamp) / 1000);
        }
        return new DecimalType(Long.MAX_VALUE);
    }*/
}