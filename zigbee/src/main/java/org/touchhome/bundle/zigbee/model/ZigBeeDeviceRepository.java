package org.touchhome.bundle.zigbee.model;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.api.workspace.HasWorkspaceVariableLinkAbility;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlock;
import org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeExtensionBlocks;

@Repository
public class ZigBeeDeviceRepository extends AbstractRepository<ZigBeeDeviceEntity> implements HasWorkspaceVariableLinkAbility {

  private final EntityContext entityContext;
  private final List<Scratch3Block> zigbeeBlocks;

  public ZigBeeDeviceRepository(EntityContext entityContext,
      List<Scratch3ZigBeeExtensionBlocks> scratch3ZigBeeExtensionBlocks) {
    super(ZigBeeDeviceEntity.class);
    this.entityContext = entityContext;
    this.zigbeeBlocks = scratch3ZigBeeExtensionBlocks.stream().flatMap(map -> map.getBlocksMap().values().stream())
        .collect(Collectors.toList());
  }

  @Override
  public void createVariable(String entityID, String varGroup, String varName, String key) {
    ZigBeeDeviceEntity zigBeeDeviceEntity = entityContext.getEntity(entityID);
    for (ZigBeeDeviceEndpoint availableLink : zigBeeDeviceEntity.gatherAvailableLinks()) {
      if (availableLink.getEndpointUUID().asKey().equals(key)) {
        this.createVariableLink(availableLink, zigBeeDeviceEntity.getZigBeeDevice(), varGroup, varName);
      }
    }
  }

  private void createVariableLink(ZigBeeDeviceEndpoint endpoint, ZigBeeDevice zigBeeDevice, String varGroup, String varName) {
    for (Scratch3Block scratch3Block : this.zigbeeBlocks) {
      if (scratch3Block instanceof Scratch3ZigBeeBlock) {
        Scratch3ZigBeeBlock scratch3ZigBeeBlock = (Scratch3ZigBeeBlock) scratch3Block;
        if (scratch3ZigBeeBlock.matchLink(endpoint, zigBeeDevice)) {
          scratch3ZigBeeBlock.getZigBeeLinkGenerator().handle(endpoint, zigBeeDevice, varGroup, varName);
          return;
        }
      }
    }
    throw new RuntimeException("Unable to create variable link. Cluster not match.");
  }
}
