package org.touchhome.bundle.zigbee;

import java.util.ArrayList;
import java.util.List;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.zigbee.util.DeviceConfiguration;
import org.touchhome.bundle.zigbee.util.DeviceConfigurations;

public class SelectModelIdentifierDynamicLoader implements DynamicOptionLoader {

  @Override
  public List<OptionModel> loadOptions(DynamicOptionLoaderParameters parameters) {
    List<OptionModel> models = new ArrayList<>();
    for (DeviceConfiguration defineEndpoint : DeviceConfigurations.getDefineEndpoints()) {
      for (String model : defineEndpoint.getModels()) {
        String title = defineEndpoint.getLabel();
        if (defineEndpoint.getCategory() != null) {
          title += "(" + defineEndpoint.getCategory() + ")";
        }
        models.add(OptionModel.of(model, title).setIcon(defineEndpoint.getImage()).setDescription(defineEndpoint.getDescription()));
      }
    }
    return models;
  }
}
