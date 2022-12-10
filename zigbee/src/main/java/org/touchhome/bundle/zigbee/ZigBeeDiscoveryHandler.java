package org.touchhome.bundle.zigbee;

import java.util.List;
import org.json.JSONObject;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.ui.action.UIActionHandler;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;
import org.touchhome.common.util.Lang;

public class ZigBeeDiscoveryHandler implements UIActionHandler {

  @Override
  public ActionResponseModel handleAction(EntityContext entityContext, JSONObject params) {
    List<ZigbeeCoordinatorEntity> coordinators = entityContext.findAll(ZigbeeCoordinatorEntity.class);
    if (coordinators.isEmpty()) {
      return ActionResponseModel.showError(Lang.getServerMessage("zigbee.no_coordinators"));
    }
    return coordinators.get(0).scan();
  }
}
