package org.touchhome.bundle.zigbee;

import java.util.List;
import java.util.stream.Collectors;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeRequireEndpoints;

public class SelectModelIdentifierDynamicLoader implements DynamicOptionLoader {

  @Override
  public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
    return ZigBeeRequireEndpoints.getDefineEndpoints().stream()
        .filter(c -> c.getModelId() != null)
        .map(c -> OptionModel.of(c.getModelId(), c.getModelId()).setIcon(c.getImage()))
        .collect(Collectors.toList());
  }
}
