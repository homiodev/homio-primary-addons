package org.touchhome.bundle.zigbee.model.service;

import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.ZigBeeDevice;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

@Log4j2
@Getter
public class ZigbeeEndpointService {

  private final EntityContext entityContext;
  private final ZigBeeBaseChannelConverter channel;
  private final ZigBeeDevice zigBeeDevice;
  private final ZigbeeCoordinatorEntity coordinator;
  // node local endpoint id
  private final int localEndpointId;
  // node local ip address
  private final IeeeAddress localIpAddress;
  private final Map<String, Consumer<State>> valueUpdateListeners = new HashMap<>();

  private final String variableId;

  @Setter
  private ZigBeeEndpoint zigBeeEndpoint;

  @Setter
  private ZigBeeEndpointEntity zigBeeEndpointEntity;

  // TODO: NEED HANDLE into properties!
  @Setter
  @Getter
  private List<Object> configOptions;

  private State lastState;

  public void updateValue(State state) {
    this.lastState = state;
    if (coordinator.isLogEvents()) {
      log.info("ZigBee <{}>, event: {}", zigBeeEndpointEntity.getEndpointUUID(), state);
    }
    if (variableId != null) {
      entityContext.var().set(variableId, state);
    }

    entityContext.event().fireEvent(zigBeeEndpointEntity.getIeeeAddress(), state);
    entityContext.event().fireEvent(zigBeeEndpointEntity.getEndpointUUID().asKey(), state);

    for (Consumer<State> listener : valueUpdateListeners.values()) {
      listener.accept(state);
    }
  }

  public ZigbeeEndpointService(EntityContext entityContext, ZigBeeBaseChannelConverter channel,
      ZigBeeDevice zigBeeDevice, ZigBeeEndpointEntity zigBeeEndpointEntity,
      ZigbeeCoordinatorEntity coordinator,
      int localEndpointId, IeeeAddress localIpAddress) {
    this.entityContext = entityContext;
    this.channel = channel;
    this.zigBeeDevice = zigBeeDevice;
    this.coordinator = coordinator;
    this.localEndpointId = localEndpointId;
    this.localIpAddress = localIpAddress;

    if (channel.getAnnotation().linkType() != VariableType.Any) {
      String varId = zigBeeEndpointEntity.getEndpointUUID().asKey();
      this.variableId = entityContext.var().createVariable("zigbee", varId, varId, channel.getAnnotation().linkType());
    } else {
      this.variableId = null;
    }
  }
}
