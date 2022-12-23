package org.touchhome.bundle.zigbee.service.z2m.properties;

import org.json.JSONObject;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.service.z2m.Z2MProperty;

public class Z2MPropertyOccupancy extends Z2MProperty {

  public Z2MPropertyOccupancy() {
    super("occupancy", "#89D636", "fa fa-fw fa-walking", null);
  }

  @Override
  protected State readValue(JSONObject payload) {
    return OnOffType.of(payload.getBoolean(getProperty()));
  }
}
