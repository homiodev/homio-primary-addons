package org.touchhome.bundle.zigbee.model.service;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.state.State;

@Getter
@RequiredArgsConstructor
public class EndpointUpdate {

  private final ZclAttribute zclAttribute;
  private final State value;
}
