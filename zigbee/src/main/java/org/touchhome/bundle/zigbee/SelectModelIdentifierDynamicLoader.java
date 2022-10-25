package org.touchhome.bundle.zigbee;

import java.util.List;
import java.util.stream.Collectors;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;

public class SelectModelIdentifierDynamicLoader implements DynamicOptionLoader {

  @Override
  public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
    return ZigBeeRequireEndpoints.get().getZigBeeRequireEndpoints().stream().map(c ->
        OptionModel.of(c.getModelId(), c.getName()).setIcon(c.getImage())).collect(Collectors.toList());
  }
}
