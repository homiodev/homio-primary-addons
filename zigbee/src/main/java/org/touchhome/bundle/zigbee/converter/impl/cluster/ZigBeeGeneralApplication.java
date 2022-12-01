package org.touchhome.bundle.zigbee.converter.impl.cluster;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.IeeeAddress;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeProfileType;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.action.HasDynamicContextMenuActions;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.model.service.EndpointUpdate;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;
import org.touchhome.bundle.zigbee.util.ClusterAttributeConfiguration;
import org.touchhome.bundle.zigbee.util.ClusterConfiguration;
import org.touchhome.common.util.CommonUtils;

@Log4j2
public class ZigBeeGeneralApplication implements ZclAttributeListener, HasDynamicContextMenuActions {

  @Getter
  private final Map<Integer, AttributeHandler> attributes = new HashMap<>();

  @Getter
  private String entityID;
  @Getter
  private ZclClusterType zclClusterType;
  private ClusterConfiguration clusterConfiguration;
  @Getter
  protected ZigBeeEndpoint endpoint;
  @Getter
  protected String deviceEntityID;
  private ZigBeeDeviceService service;
  @Getter
  protected ZclCluster zclCluster;

  @Getter
  private long lastAnswer;
  @Getter
  private Status bindStatus = Status.UNKNOWN;
  @Getter
  private Status attributeDiscovered = Status.UNKNOWN;

  public void postConstruct(ZclClusterType zclClusterType, ClusterConfiguration clusterConfiguration, ZigBeeEndpoint endpoint, ZigBeeDeviceService service) {
    this.entityID = endpoint.getIeeeAddress() + "_" + endpoint.getEndpointId() + "_" + zclClusterType.getId();
    this.zclClusterType = zclClusterType;
    this.clusterConfiguration = clusterConfiguration;
    this.endpoint = endpoint;
    this.service = service;
    this.deviceEntityID = service.getEntityID();
  }

  protected EntityContext getEntityContext() {
    return service.getEntityContext();
  }

  public void appStartup() {
    this.zclCluster = endpoint.getInputCluster(zclClusterType.getId());
    if (clusterConfiguration.isDiscoveryAttributes()) {
      discoveryAttributes(zclCluster);
    } else {
      attributeDiscovered = Status.NOT_SUPPORTED;
    }

    try {
      this.bindStatus = bindCluster(zclCluster);
      initializeAttributes(zclCluster);
    } catch (Exception e) {
      log.error("[{}]: Exception binding endpoint {} ", deviceEntityID, endpoint, e);
    }
    zclCluster.addAttributeListener(this);
  }

  public void appShutdown() {
    zclCluster.removeAttributeListener(this);
  }

  @Override
  public void assembleActions(UIInputBuilder uiInputBuilder) {

  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object value) {
    this.lastAnswer = System.currentTimeMillis();
    AttributeHandler attributeHandler = attributes.get(attribute.getId());
    if (attributeHandler != null) {
      State state = attributeHandler.convertValue(value);
      if (state != null) {
        attributeHandler.setValue(state);

        if (attributeHandler.getVariableID() != null) {
          service.getEntityContext().var().set(attributeHandler.getVariableID(), state);
        }

        service.getEntity().setLastAnswerFromEndpoints(System.currentTimeMillis());
        if (service.getCoordinatorService().getEntity().isLogEvents()) {
          log.info("[{}]: ZigBee <{}>, Attribute <{}>, value: {}", entityID, endpoint, attribute.getName(), state);
        }

        service.getEntityContext().event().fireEvent(endpoint.getIeeeAddress().toString(), new EndpointUpdate(attribute, state));
      }
    }
  }

  public void handleRefresh(boolean force) {
    for (AttributeHandler attributeHandler : attributes.values()) {
      attributeHandler.handleRefresh(force);
    }
  }

  public Optional<AttributeHandler> getAttributeHandler(int attributeID) {
    return Optional.ofNullable(attributes.get(attributeID));
  }

  protected final void initializeAttributes(ZclCluster zclCluster) {
    List<AttributeHandler> createdAttributes = new ArrayList<>();
    for (Integer attributeID : zclCluster.getSupportedAttributes()) {
      createdAttributes.add(createAttribute(zclCluster, attributeID));
    }
    for (AttributeHandler attributeHandler : createdAttributes) {
      initializeAttribute(attributeHandler, bindStatus == Status.DONE);
    }
  }

  protected final AttributeHandler createAttribute(ZclCluster zclCluster, Integer attributeID) {
    ZclAttribute zclAttribute = zclCluster.getAttribute(attributeID);
    ClusterAttributeConfiguration attributeConfiguration = clusterConfiguration.getAttributeConfiguration(zclAttribute.getName());
    AttributeHandler attributeHandler = CommonUtils.newInstance(attributeConfiguration.getAttributeHandlerClass());
    attributeHandler.postConstruct(zclAttribute, attributeConfiguration, this);
    attributes.put(attributeID, attributeHandler);
    return attributeHandler;
  }

  protected final void initializeAttribute(AttributeHandler attributeHandler, boolean bindSuccess) {
    VariableType variableType = attributeHandler.getAttributeConfiguration().getVariableType();
    if (variableType != VariableType.Any) {
      attributeHandler.setVariableID(service.getEntityContext().var().createVariable("zigbee", attributeHandler.getEntityID(),
          attributeHandler.getEntityID(), variableType));
    }

    try {
      attributeHandler.initialize(bindSuccess);
      attributeHandler.handleRefresh(true);
    } catch (Exception ex) {
      attributeHandler.setStatus(Status.ERROR);
      log.error("[{}]: initialize attribute {}. {}", deviceEntityID, attributeHandler.getZclAttribute().getName(), endpoint, ex);
    }
  }

  protected final Status bindCluster(ZclCluster zclCluster) {
    try {
      IeeeAddress localIeeeAddress = service.getCoordinatorService().getLocalIeeeAddress();
      int localEndpointId = service.getCoordinatorService().getLocalEndpointId(ZigBeeProfileType.ZIGBEE_HOME_AUTOMATION);
      CommandResult commandResult = zclCluster.bind(localIeeeAddress, localEndpointId).get(60, TimeUnit.SECONDS);
      return commandResult.isSuccess() ? Status.DONE : Status.ERROR;
    } catch (Exception ex) {
      log.error("[{}]: Exception setting binding {}", deviceEntityID, endpoint, ex);
    }
    return Status.ERROR;
  }

  protected final void discoveryAttributes(ZclCluster zclCluster) {
    try {
      Boolean status = zclCluster.discoverAttributes(false).get(60, TimeUnit.SECONDS);
      this.attributeDiscovered = Boolean.TRUE.equals(status) ? Status.DONE : Status.ERROR;
    } catch (Exception te) {
      this.attributeDiscovered = Status.ERROR;
      log.error("[{}]: Unable to discover attributes for cluster {}", deviceEntityID, zclClusterType.name());
    }
    /*try {
      zclCluster.discoverCommandsGenerated(false).get(60, TimeUnit.SECONDS);
    } catch (Exception te) {
      log.error("[{}]: Unable to discover commands for cluster {}", deviceEntityID, zclClusterType.name());
    }*/
  }
}
