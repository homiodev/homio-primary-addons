package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.json.JSONObject;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyLastUpdate extends Z2MProperty {

  public Z2MPropertyLastUpdate() {
    super("updated", "#BA5623", "fa fa-fw fa-clock", null);
  }

  @Override
  protected State readValue(JSONObject payload) {
    return new DecimalType(System.currentTimeMillis());
  }
}
