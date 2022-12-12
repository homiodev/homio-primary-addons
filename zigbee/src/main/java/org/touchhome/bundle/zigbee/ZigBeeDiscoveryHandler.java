package org.touchhome.bundle.zigbee;

import java.util.List;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.action.UIActionHandler;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

public class ZigBeeDiscoveryHandler implements UIActionHandler {

  @Override
  public ActionResponseModel handleAction(EntityContext entityContext, JSONObject params) {
    List<ZigbeeCoordinatorEntity> coordinators = entityContext.findAll(ZigbeeCoordinatorEntity.class);
    if (coordinators.isEmpty()) {
      return ActionResponseModel.showError("zigbee.error.no_coordinators");
    }
    return coordinators.get(0).scan();
  }
}
