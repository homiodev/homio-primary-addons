package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.ZclCommandListener;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
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
  @Getter @Nullable private final Integer attributeId;
  protected @Nullable ZclAttribute attribute;

  protected Cluster zclCluster;
  @Getter @NotNull protected ClusterAttributeConfiguration configuration;

  public ZigBeeInputBaseConverter(ZclClusterType zclClusterType, @Nullable Integer attributeId) {
    this.zclClusterType = zclClusterType;
    this.attributeId = attributeId;

    ClusterConfiguration configuration =
        ClusterConfigurations.getClusterConfiguration(zclClusterType.getId());
    // get or create configuration
    this.configuration = configuration.getAttributeConfiguration(attributeId == null ? -1 : attributeId);
  }

  /**
   * Test cluster. must be override if attributeID is null
   */
  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    if (attributeId == null) {
      throw new IllegalStateException("Cluster with null attributeId must override acceptEndpoint(...) method");
    }
    return acceptEndpoint(endpoint, entityID, entityContext, zclClusterType.getId(), attributeId,
        configuration.isDiscoverAttributes(), configuration.isReadAttribute());
  }

  protected void initializeReportConfigurations() {
  }

  @Override
  public void initialize() {
    if (zclCluster == null) {
      log.debug("[{}]: Initialising {} device cluster {}", entityID, getClass().getSimpleName(), endpoint);
      zclCluster = getInputCluster(zclClusterType.getId());

      if (configuration.isReportConfigurable()) {
        configReporting = new ZclReportingConfig(getEntity());
      }
      initializeReportConfigurations();
    }

    initializeBinding();

    initializeAttribute();
  }

  protected void initializeBinding() {
    if (bindStatus != Status.DONE) {
      try {
        initializeBindingReport();
      } catch (Exception ex) {
        bindStatus = Status.ERROR;
        log.error("[{}]: Exception setting reporting {}", endpoint, ex);
        if (configuration.getBindFailedPollingPeriod() != null) {
          pollingPeriod = configuration.getBindFailedPollingPeriod();
        }
      }
    }
  }

  protected void initializeAttribute() {
    if (attribute == null && attributeId != null) {
      attribute = zclCluster.getAttribute(attributeId);
      if (attribute == null) {
        log.error("[{}]: Error opening device {} attribute {}", entityID, zclClusterType, endpoint);
        throw new RuntimeException("Error opening device attribute");
      }

      zclCluster.addAttributeListener(this);
    }
  }

  @Override
  public void disposeConverter() {
    log.debug("[{}]: Closing device input cluster {}. {}", entityID, zclClusterType, endpoint);
    zclCluster.removeAttributeListener(this);

    if (this instanceof ZclCommandListener) {
      zclCluster.removeCommandListener((ZclCommandListener) this);
    }
  }

  @Override
  protected void handleRefresh() {
    if (attribute != null) {
      attribute.readValue(0);
    }
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    if (attributeId == null) {
      throw new IllegalStateException("Cluster with null attributeId must override attributeUpdated(...) method");
    }

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
    if (configReporting != null && configReporting.updateConfiguration(getEntity())) {
      updateDeviceReporting(zclCluster, attributeId, true);
    }
  }

  protected void initializeBindingReport() throws Exception {
    if (attributeId == null) {
      throw new IllegalStateException("Cluster with null attributeId must override initializeBindingReport(...) method");
    }
    CommandResult bindResponse = bind(zclCluster);
    if (bindResponse.isSuccess()) {
      ZclAttribute attribute = zclCluster.getAttribute(attributeId);

      ZigBeeEndpointEntity endpointEntity = getEndpointService().getEntity();
      CommandResult reportingResponse = attribute.setReporting(
          configuration.getReportMinInterval(endpointEntity),
          configuration.getReportMaxInterval(endpointEntity),
          configuration.getReportChange(endpointEntity)).get();

      handleReportingResponse(reportingResponse, configuration.getFailedPollingInterval(), configuration.getSuccessMaxReportInterval(endpointEntity));
    } else {
      if (configuration.getBindFailedPollingPeriod() != null) {
        pollingPeriod = configuration.getBindFailedPollingPeriod();
      }
      log.warn("[{}]: Could not bind to the '{}' configuration cluster; Response code: {}", entityID, zclClusterType.name(), bindResponse.getStatusCode());
    }
  }
}
