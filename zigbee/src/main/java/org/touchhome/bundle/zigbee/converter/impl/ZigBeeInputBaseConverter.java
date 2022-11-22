package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;

@Log4j2
@RequiredArgsConstructor
public abstract class ZigBeeInputBaseConverter extends ZigBeeBaseChannelConverter implements ZclAttributeListener {

  private final ZclClusterType zclClusterType;
  @Getter
  private final int attributeId;
  private final Integer reportMinInterval;
  private final Integer reportMaxInterval;
  private final Object reportableChange;
  private int reportingFailedPollingInterval;
  @Getter
  private ZclCluster zclCluster;
  @Getter
  private ZclAttribute attribute;

  public ZigBeeInputBaseConverter(ZclClusterType zclClusterType, int attributeId) {
    this.zclClusterType = zclClusterType;
    this.attributeId = attributeId;
    this.reportMinInterval = null;
    this.reportMaxInterval = null;
    this.reportableChange = null;
    this.reportingFailedPollingInterval = POLLING_PERIOD_DEFAULT;
  }

  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, boolean discoverAttribute, boolean readAttribute) {
    return acceptEndpoint(endpoint, entityID, zclClusterType.getId(), attributeId, discoverAttribute, readAttribute);
  }

  @Override
  public boolean initializeDevice() {
    log.debug("[{}]: Initialising {} device cluster {}", entityID, getClass().getSimpleName(), endpoint);

    ZclCluster zclCluster = getZclClusterInternal();
    boolean success = false;
    if (zclCluster != null) {
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
            handleReportingResponse(reportingResponse, reportingFailedPollingInterval, endpointEntity.getPollingPeriod());
          } else {
            CommandResult reportingResponse = attribute.setReporting(reportMinInterval, reportMaxInterval, reportableChange).get();
            handleReportingResponseDuringInitializeDevice(reportingResponse);
          }
          success = true;
        } else {
          log.error("[{}]: Error 0x{} setting server binding for cluster {}. {}", entityID, endpoint,
              Integer.toHexString(bindResponse.getStatusCode()), zclClusterType);
          success = initializeDeviceFailed();
        }
      } catch (Exception e) {
        log.error("[{}]: Exception setting reporting {}", endpoint, e);
        return false;
      }
    }
    if (success) {
      return afterInitializeDevice();
    }
    return success;
  }

  protected void handleReportingResponseDuringInitializeDevice(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse);
  }

  protected boolean afterInitializeDevice() {
    return true;
  }

  protected boolean initializeDeviceFailed() {
    return true;
  }

  @Override
  public boolean initializeConverter() {
    zclCluster = getZclClusterInternal();

    if (zclCluster == null) {
      log.error("[{}]: Error opening cluster {}. {}", entityID, zclClusterType, endpoint);
      return false;
    }

    attribute = zclCluster.getAttribute(attributeId);
    if (attribute == null) {
      log.error("[{}]: Error opening device {} attribute {}", entityID, zclClusterType, endpoint);
      return false;
    }

    zclCluster.addAttributeListener(this);
    afterInitializeConverter();
    return true;
  }

  protected void afterInitializeConverter() {

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
      updateChannelState(OnOffType.of((Boolean) val));
    } else {
      throw new IllegalStateException("Unable to find value handler for type: " + val);
    }
  }

  @Override
  public void updateConfiguration() {
    if (configReporting != null && configReporting.updateConfiguration(getEntity())) {
      updateServerPollingPeriod(zclCluster, attributeId);
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
