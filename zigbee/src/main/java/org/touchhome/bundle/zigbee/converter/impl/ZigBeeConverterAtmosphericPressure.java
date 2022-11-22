package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster.ATTR_MEASUREDVALUE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster.ATTR_SCALE;
import static com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster.ATTR_SCALEDVALUE;
import static tec.uom.se.unit.MetricPrefix.HECTO;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPressureMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import tec.uom.se.unit.Units;

/**
 * Indicates the current pressure Converter for the atmospheric pressure channel. This channel will attempt to detect if the device is supporting the enhanced (scaled) value
 * reports and use them if they are available.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:measurement_pressure", linkType = VariableType.Float,
    clientCluster = ZclPressureMeasurementCluster.CLUSTER_ID, category = "Pressure")
public class ZigBeeConverterAtmosphericPressure extends ZigBeeBaseChannelConverter implements ZclAttributeListener {

  private ZclPressureMeasurementCluster cluster;

  /**
   * If enhancedScale is null, then the binding will use the MeasuredValue report, otherwise it will use the ScaledValue report
   */
  private Integer enhancedScale = null;

  @Override
  public boolean initializeDevice() {
    pollingPeriod = REPORTING_PERIOD_DEFAULT_MAX;
    ZclCluster serverCluster = endpoint.getInputCluster(ZclPressureMeasurementCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("[{}]: Error opening device pressure measurement cluster {}", entityID, endpoint);
      return false;
    }

    // Check if the enhanced attributes are supported
    determineEnhancedScale(serverCluster);

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting - no faster than once per second - no slower than 2 hours.
        CommandResult reportingResponse;
        if (enhancedScale != null) {
          reportingResponse = serverCluster.setReporting(ATTR_SCALEDVALUE, 1, REPORTING_PERIOD_DEFAULT_MAX, 0.1).get();
          handleReportingResponse(reportingResponse);
        } else {
          reportingResponse = serverCluster.setReporting(ATTR_MEASUREDVALUE, 1, REPORTING_PERIOD_DEFAULT_MAX, 0.1).get();
          handleReportingResponse(reportingResponse);
        }
      } else {
        log.error("[{}]: Error 0x{} setting server binding {}", entityID, Integer.toHexString(bindResponse.getStatusCode()), endpoint);
        pollingPeriod = POLLING_PERIOD_HIGH;
        return false;
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting {}", entityID, endpoint, e);
      pollingPeriod = POLLING_PERIOD_HIGH;
      return false;
    }
    return true;
  }

  @Override
  public boolean initializeConverter() {
    cluster = getInputCluster(ZclPressureMeasurementCluster.CLUSTER_ID);
    if (cluster == null) {
      log.error("[{}]: Error opening device pressure measurement cluster {}", entityID, endpoint);
      return false;
    }

    // Check if the enhanced attributes are supported
    determineEnhancedScale(cluster);

    // Add a listener
    cluster.addAttributeListener(this);
    return true;
  }

  @Override
  public void disposeConverter() {
    cluster.removeAttributeListener(this);
  }

  @Override
  protected void handleRefresh() {
    if (enhancedScale != null) {
      getScaleValue(cluster, 0);
    } else {
      getMeasuredValue(cluster);
    }
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    return super.acceptEndpoint(endpoint, entityID, ZclPressureMeasurementCluster.CLUSTER_ID, 0, false, false);
  }

  @Override
  public synchronized void attributeUpdated(ZclAttribute attribute, Object value) {
    log.debug("[{}]: ZigBee attribute reports {} for {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() != ZclClusterType.PRESSURE_MEASUREMENT) {
      return;
    }

    // Handle automatic reporting of the enhanced attribute configuration
    if (attribute.getId() == ATTR_SCALE) {
      enhancedScale = (Integer) value;
      if (enhancedScale != null) {
        enhancedScale *= -1;
      }
      return;
    }

    if (attribute.getId() == ZclPressureMeasurementCluster.ATTR_SCALEDVALUE && enhancedScale != null) {
      updateChannelState(new QuantityType<>(BigDecimal.valueOf((Integer) value, enhancedScale), HECTO(Units.PASCAL)));
      return;
    }

    if (attribute.getId() == ATTR_MEASUREDVALUE && enhancedScale == null) {
      updateChannelState(new QuantityType<>(BigDecimal.valueOf((Integer) value, 0), HECTO(Units.PASCAL)));
    }
  }

  private void determineEnhancedScale(ZclCluster cluster) {
    if (getScaleValue(cluster, Long.MAX_VALUE) != null) {
      enhancedScale = getScale(cluster);
      if (enhancedScale != null) {
        enhancedScale *= -1;
      }
    }
  }

  private void getMeasuredValue(ZclCluster zclCluster) {
    readAttribute(zclCluster, ATTR_MEASUREDVALUE, 0L);
  }

  private Integer getScaleValue(ZclCluster zclCluster, long refreshPeriod) {
    return (Integer) readAttribute(zclCluster, ATTR_SCALEDVALUE, refreshPeriod);
  }

  private Integer getScale(ZclCluster zclCluster) {
    return (Integer) zclCluster.getAttribute(ATTR_SCALE).readValue(Long.MAX_VALUE);
  }

  private Object readAttribute(ZclCluster zclCluster, int attributeID, long refreshPeriod) {
    return zclCluster.getAttribute(attributeID).readValue(refreshPeriod);
  }
}
