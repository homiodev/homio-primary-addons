package org.homio.addon.z2m;

import lombok.Getter;
import org.homio.api.EntityContext;
import org.homio.api.workspace.scratch.Scratch3BaseDeviceBlocks;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Scratch3ZigBeeBlocks extends Scratch3BaseDeviceBlocks {

    public Scratch3ZigBeeBlocks(EntityContext entityContext, Z2MEntrypoint z2MEntrypoint) {
        super("#6d4747", entityContext, z2MEntrypoint);
    }
}
