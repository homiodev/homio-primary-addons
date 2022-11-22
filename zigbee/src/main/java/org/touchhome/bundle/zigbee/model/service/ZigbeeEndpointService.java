package org.touchhome.bundle.zigbee.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.service.EntityService.ServiceInstance;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

@Log4j2
@Getter
public class ZigbeeEndpointService implements ServiceInstance<ZigBeeEndpointEntity> {

  private EntityContext entityContext;
  private ZigBeeBaseChannelConverter cluster;
  private ZigBeeDeviceService zigBeeDeviceService;
  private ZigbeeCoordinatorEntity coordinator;
  // node local endpoint id
  private int localEndpointId;
  // node local ip address
  private IeeeAddress localIpAddress;

  private String variableId;
  @Getter
  private JsonNode metadata;
  private ZigBeeEndpointEntity entity;
  // TODO: NEED HANDLE into properties!
  @Setter
  @Getter
  private List<Object> configOptions;

  // uses to make fake endpoint service with set status as error
  public ZigbeeEndpointService(ZigBeeEndpointEntity entity) {
    entity.setStatus(Status.OFFLINE);
  }

  public ZigbeeEndpointService(EntityContext entityContext, ZigBeeBaseChannelConverter cluster,
      ZigBeeDeviceService zigBeeDeviceService, ZigBeeEndpointEntity entity, String ieeeAddress,
      JsonNode metadata) {
    ZigBeeCoordinatorService coordinatorService = zigBeeDeviceService.getCoordinatorService();
    this.entityContext = entityContext;
    this.cluster = cluster;
    this.metadata = metadata;
    this.entity = entity;
    this.zigBeeDeviceService = zigBeeDeviceService;
    this.coordinator = coordinatorService.getEntity();
    this.localEndpointId = coordinatorService.getLocalEndpointId(ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION);
    this.localIpAddress = coordinatorService.getLocalIeeeAddress();

    // fire initialize endpoint
    ZigBeeEndpoint endpoint = coordinatorService.getEndpoint(zigBeeDeviceService.getNodeIeeeAddress(), entity.getAddress());
    cluster.initialize(this, endpoint);

    if (cluster.getAnnotation().linkType() != VariableType.Any) {
      String varId = ieeeAddress + "_" + entity.getAddress() + "-" + entity.getClusterId();
      this.variableId = entityContext.var().createVariable("zigbee", varId, varId, cluster.getAnnotation().linkType());
    } else {
      this.variableId = null;
    }
  }

  public void updateValue(State state) {
    this.entity.setLastState(state);
    this.entity.setLastAnswerFromEndpoint(System.currentTimeMillis());

    if (coordinator.isLogEvents()) {
      log.info("[{}]: ZigBee <{}>, event: {}", entity.getEntityID(), entity.getEndpointUUID(), state);
    }
    if (variableId != null) {
      entityContext.var().set(variableId, state);
    }

    entityContext.event().fireEvent(entity.getEndpointUUID().asKey(), state);
  }

  @Override
  public boolean entityUpdated(ZigBeeEndpointEntity entity) {
    this.entity = entity;
    this.cluster.updateConfiguration();
    // if entity has been updated during configuration
    if (entity.isOutdated()) {
      log.info("Endpoint had been updated during cluster configuration");
      entityContext.save(entity);
    }
    return false;
  }

  @Override
  public void destroy() {

  }

  @Override
  public boolean testService() {
    return false;
  }
}
