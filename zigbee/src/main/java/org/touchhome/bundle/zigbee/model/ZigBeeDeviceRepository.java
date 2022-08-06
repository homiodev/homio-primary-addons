package org.touchhome.bundle.zigbee.model;

import com.zsmartsystems.zigbee.IeeeAddress;
import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.api.workspace.HasWorkspaceVariableLinkAbility;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterEndpoint;
import org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeBlock;
import org.touchhome.bundle.zigbee.workspace.Scratch3ZigBeeExtensionBlocks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter>> availableLinks =
                zigBeeDeviceEntity.gatherAvailableLinks();
        for (Map.Entry<ZigBeeConverterEndpoint, ZigBeeBaseChannelConverter> availableLink : availableLinks) {
            ZigBeeConverterEndpoint converterEndpoint = availableLink.getKey();
            if (converterEndpoint.toUUID().asKey().equals(key)) {
                this.createVariableLink(converterEndpoint, zigBeeDeviceEntity.getZigBeeDevice(), varGroup, varName);
            }
        }
    }

    private void createVariableLink(ZigBeeConverterEndpoint zigBeeConverterEndpoint, ZigBeeDevice zigBeeDevice, String varGroup,
                                    String varName) {
        for (Scratch3Block scratch3Block : this.zigbeeBlocks) {
            if (scratch3Block instanceof Scratch3ZigBeeBlock) {
                Scratch3ZigBeeBlock scratch3ZigBeeBlock = (Scratch3ZigBeeBlock) scratch3Block;
                if (scratch3ZigBeeBlock.matchLink(zigBeeConverterEndpoint, zigBeeDevice)) {
                    scratch3ZigBeeBlock.getZigBeeLinkGenerator().handle(zigBeeConverterEndpoint, zigBeeDevice, varGroup, varName);
                    return;
                }
            }
        }
        throw new RuntimeException("Unable to create variable link. Cluster not match.");
    }
}
