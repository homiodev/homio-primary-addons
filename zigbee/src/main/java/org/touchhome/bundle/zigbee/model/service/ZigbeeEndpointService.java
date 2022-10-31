package org.touchhome.bundle.zigbee.model.service;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.converter.DeviceChannelLinkType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEndpoint;

@Getter
@RequiredArgsConstructor
public class ZigbeeEndpointService {

  private final ZigBeeBaseChannelConverter channel;
  private final ZigBeeDevice zigBeeDevice;
  private final int localEndpointId;
  private final IeeeAddress localIpAddress;

  @Setter
  private ZigBeeEndpoint zigBeeEndpoint;

  @Setter
  private ZigBeeDeviceEndpoint zigBeeDeviceEndpoint;

  @Setter
  @Getter
  private List<Object> configOptions;

  public DeviceChannelLinkType getLinkType() {
    return channel.getAnnotation().linkType();
  }
}
