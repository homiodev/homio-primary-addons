package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.Getter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.converter.config.ZclReportingConfig;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;
import org.touchhome.bundle.zigbee.util.ClusterAttributeConfiguration;
import org.touchhome.bundle.zigbee.util.ClusterConfiguration;
import org.touchhome.bundle.zigbee.util.ClusterConfigurations;

public abstract class ZigBeeInputBaseConverter<Cluster extends ZclCluster> extends ZigBeeBaseChannelConverter
    implements ZclAttributeListener {

  @Getter private final ZclClusterType zclClusterType;
  @Getter private final int attributeId;

  protected Cluster zclCluster;
  protected ZclAttribute attribute;
  @Getter protected ClusterAttributeConfiguration configuration;

  public ZigBeeInputBaseConverter(ZclClusterType zclClusterType, int attributeId) {
    this.zclClusterType = zclClusterType;
    this.attributeId = attributeId;

    ClusterConfiguration configuration =
        ClusterConfigurations.getClusterConfiguration(zclClusterType.getId());
    this.configuration = configuration.getAttributeConfiguration(attributeId);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, entityContext, zclClusterType.getId(), attributeId,
        configuration.isDiscoverAttributes(), configuration.isReadAttribute());
  }

  @Override
  public void initializeDevice() throws Exception {
    log.debug("[{}]: Initialising {} device cluster {}", entityID, getClass().getSimpleName(), this.endpoint);
    this.zclCluster = getInputCluster(zclClusterType.getId());

    if (configuration.isReportConfigurable()) {
      configReporting = new ZclReportingConfig(getEntity());
    }
    this.initializeReportConfigurations();

    try {
      CommandResult bindResponse = bind(zclCluster);
      if (bindResponse.isSuccess()) {
        ZclAttribute attribute = zclCluster.getAttribute(this.attributeId);

        ZigBeeEndpointEntity endpointEntity = getEndpointService().getEntity();
        CommandResult reportingResponse = attribute.setReporting(
            configuration.getReportMinInterval(endpointEntity),
            configuration.getReportMaxInterval(endpointEntity),
            configuration.getReportChange(endpointEntity)).get();

        handleReportingResponse(
            reportingResponse,
            configuration.getFailedPollingInterval(),
            configuration.getSuccessMaxReportInterval(endpointEntity));
      } else {
        if (configuration.getBindFailedPollingPeriod() != null) {
          pollingPeriod = configuration.getBindFailedPollingPeriod();
        }
        log.warn("[{}]: Could not bind to the '{}' configuration cluster; Response code: {}", entityID, zclClusterType.name(), bindResponse.getStatusCode());
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting {}", endpoint, e);
      if (configuration.getBindFailedPollingPeriod() != null) {
        pollingPeriod = configuration.getBindFailedPollingPeriod();
      }
      throw new RuntimeException("Exception setting reporting");
    }
  }

  protected void initializeReportConfigurations() {
  }

  @Override
  public void initializeConverter() {
    attribute = zclCluster.getAttribute(attributeId);
    if (attribute == null) {
      log.error("[{}]: Error opening device {} attribute {}", entityID, zclClusterType, endpoint);
      throw new RuntimeException("Error opening device attribute");
    }

    zclCluster.addAttributeListener(this);
  }

  @Override
  public void disposeConverter() {
    log.debug("[{}]: Closing device input cluster {}. {}", entityID, zclClusterType, endpoint);

    zclCluster.removeAttributeListener(this);
  }

  @Override
  protected void handleRefresh() {
    if (attribute != null) {
      attribute.readValue(0);
    }
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() == zclClusterType && attribute.getId() == attributeId) {
      updateValue(val, attribute);
    }
  }

  protected void updateValue(Object val, ZclAttribute attribute) {
    if (val instanceof Double) {
      updateChannelState(new DecimalType((Double) val));
    } else if (val instanceof Integer) {
      updateChannelState(new DecimalType((Integer) val));
    } else if (val instanceof Boolean) {
      updateChannelState(OnOffType.of(val));
    } else {
      throw new IllegalStateException("Unable to find value handler for type: " + val);
    }
  }

  @Override
  public void updateConfiguration() {
    super.updateConfiguration();
    if (configReporting != null && configReporting.updateConfiguration(getEntity())) {
      updateDeviceReporting(zclCluster, attributeId, true);
    }
  }
}
