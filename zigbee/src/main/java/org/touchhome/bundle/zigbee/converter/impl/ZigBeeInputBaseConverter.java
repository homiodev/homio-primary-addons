package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclAnalogInputBasicCluster.ATTR_DESCRIPTION;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;

@RequiredArgsConstructor
public abstract class ZigBeeInputBaseConverter extends ZigBeeBaseChannelConverter implements ZclAttributeListener {

  private final ZclClusterType zclClusterType;
  @Getter
  private final int attributeId;
  private final Integer reportMinInterval;
  private final Integer reportMaxInterval;
  private final Object reportableChange;
  private final @Nullable Integer bindFailedReportPeriod;

  @Getter
  private ZclCluster zclCluster;
  @Getter
  private ZclAttribute attribute;

  public ZigBeeInputBaseConverter(ZclClusterType zclClusterType, int attributeId, @Nullable Integer bindFailedReportPeriod) {
    this.zclClusterType = zclClusterType;
    this.attributeId = attributeId;
    this.reportMinInterval = null;
    this.reportMaxInterval = null;
    this.reportableChange = null;
    this.bindFailedReportPeriod = bindFailedReportPeriod;
  }

  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, boolean discoverAttribute, boolean readAttribute, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, entityContext, zclClusterType.getId(), attributeId, discoverAttribute, readAttribute);
  }

  @Override
  public void initializeDevice() throws Exception {
    log.debug("[{}]: Initialising {} device cluster {}", entityID, getClass().getSimpleName(), endpoint);

    ZclCluster zclCluster = getInputCluster(zclClusterType.getId());
    if (zclCluster == null) {
      log.error("[{}]: Error opening server cluster {}. {}", entityID, zclClusterType, endpoint);
      throw new RuntimeException("Error opening server cluster");
    }
    try {
      CommandResult bindResponse = bind(zclCluster).get();
      if (bindResponse.isSuccess()) {
        ZclAttribute attribute = zclCluster.getAttribute(this.attributeId);

        if (reportMinInterval == null || reportMaxInterval == null) {
          ZigBeeEndpointEntity endpointEntity = getEndpointService().getEntity();
          CommandResult reportingResponse = attribute.setReporting(
              endpointEntity.getReportingTimeMin(),
              endpointEntity.getReportingTimeMax(),
              endpointEntity.getReportingChange()).get();
          handleReportingResponseOnBind(reportingResponse);
        } else {
          CommandResult reportingResponse = attribute.setReporting(reportMinInterval, reportMaxInterval, reportableChange).get();
          handleReportingResponseOnBind(reportingResponse);
        }
      } else {
        if (bindFailedReportPeriod != null) {
          pollingPeriod = bindFailedReportPeriod;
        }
        log.warn("[{}]: Could not bind to the '{}' configuration cluster; Response code: {}",
            entityID, zclClusterType.name(), bindResponse.getStatusCode());
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting {}", endpoint, e);
      pollingPeriod = POLLING_PERIOD_HIGH;
      throw new RuntimeException("Exception setting reporting");
    }
  }

  protected abstract void handleReportingResponseOnBind(CommandResult reportingResponse);

  @Override
  public void initializeConverter() {
    zclCluster = getZclClusterInternal();

    if (zclCluster == null) {
      log.error("[{}]: Error opening cluster {}. {}", entityID, zclClusterType, endpoint);
      throw new RuntimeException("Error opening cluster");
    }

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
    attribute.readValue(0);
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
    if (configReporting != null && configReporting.updateConfiguration(getEntity())) {
      updateDeviceReporting(zclCluster, attributeId, configReporting);
    }
  }

  protected ZclCluster getZclClusterInternal() {
    ZclCluster zclCluster = getInputCluster(zclClusterType.getId());
    if (zclCluster == null) {
      log.error("[{}]: Error opening server cluster {}. {}", entityID, zclClusterType, endpoint);
      return null;
    }
    return zclCluster;
  }
}
