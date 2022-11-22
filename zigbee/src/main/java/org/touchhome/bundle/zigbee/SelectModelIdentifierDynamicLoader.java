package org.touchhome.bundle.zigbee;

import java.util.List;
import java.util.stream.Collectors;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.zigbee.requireEndpoint.ZigBeeDefineEndpoints;

public class SelectModelIdentifierDynamicLoader implements DynamicOptionLoader {

  @Override
  public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
    return ZigBeeDefineEndpoints.getDefineEndpoints().stream()
        .filter(c -> c.getModelId() != null)
        .map(c -> {
          String title = c.getLabel();
          if (c.getCategory() != null) {
            title += "(" + c.getCategory() + ")";
          }
          return OptionModel.of(c.getModelId(), title).setIcon(c.getImage()).setDescription(c.getDescription());
        })
        .collect(Collectors.toList());
  }
}
