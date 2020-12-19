package org.touchhome.bundle.zigbee;

import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;

import java.util.Collection;
import java.util.stream.Collectors;

public class SelectModelIdentifierDynamicLoader implements DynamicOptionLoader {

    @Override
    public Collection<OptionModel> loadOptions(Object parameter, BaseEntity baseEntity, EntityContext entityContext) {
        return ZigBeeRequireEndpoints.get().getZigBeeRequireEndpoints().stream().map(c ->
                OptionModel.of(c.getModelId(), c.getName()).setImageRef(c.getImage())).collect(Collectors.toList());
    }
}
