package org.touchhome.bundle.zigbee.workspace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.ZigBeeDeviceStateUUID;

@Setter
@Getter
@RequiredArgsConstructor
public class ScratchDeviceState {

  private final ZigBeeDevice zigBeeDevice;
  private final ZigBeeDeviceStateUUID uuid;
  private final State state;
  private final long date = System.currentTimeMillis();
  private boolean isHandled = false;
}
