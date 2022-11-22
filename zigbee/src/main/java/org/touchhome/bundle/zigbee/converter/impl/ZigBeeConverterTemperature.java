package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.clusters.ZclTemperatureMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;

/**
 * Indicates the current temperature Converter for the temperature channel
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:measurement_temperature", linkType = VariableType.Float,
    serverClusters = {ZclTemperatureMeasurementCluster.CLUSTER_ID},
    clientCluster = ZclTemperatureMeasurementCluster.CLUSTER_ID, category = "Temperature")
public class ZigBeeConverterTemperature extends ZigBeeBaseChannelConverter implements ZclAttributeListener {

  private ZclTemperatureMeasurementCluster cluster;
  private ZclTemperatureMeasurementCluster clusterClient;
  private ZclAttribute attribute;
  private ZclAttribute attributeClient;

  @Override
  public boolean initializeDevice() {
    pollingPeriod = REPORTING_PERIOD_DEFAULT_MAX;
    ZclTemperatureMeasurementCluster clientCluster = getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (clientCluster == null) {
      // Nothing to do, but we still return success
      return true;
    }

    ZclTemperatureMeasurementCluster serverCluster = getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error("[{}]: Error opening device temperature measurement cluster {}", entityID, endpoint);
      return false;
    }

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting
        ZclAttribute attribute = serverCluster.getAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
        CommandResult reportingResponse = attribute.setReporting(1, REPORTING_PERIOD_DEFAULT_MAX, 10).get();
        handleReportingResponse(reportingResponse);
      } else {
        log.debug("[{}]: Failed to bind temperature measurement cluster {}", entityID, endpoint);
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting", endpoint, e);
      return false;
    }

    return true;
  }

  @Override
  public boolean initializeConverter() {
    cluster = getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (cluster != null) {
      attribute = cluster.getAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
      // Add a listener
      cluster.addAttributeListener(this);
    } else {
      clusterClient = getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
      attributeClient = clusterClient.getLocalAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
      attributeClient.setImplemented(true);
    }

    if (cluster == null && clusterClient == null) {
      log.error("[{}]: Error opening device temperature measurement cluster {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public void disposeConverter() {
    if (cluster != null) {
      cluster.removeAttributeListener(this);
    }
  }

  @Override
  protected void handleRefresh() {
    if (attribute != null) {
      attribute.readValue(0);
    }
  }

    /*@Override
    public void handleCommand(final ZigBeeCommand command) {
        if (attributeClient == null) {
            log.warn("[{}]: Temperature measurement update but remote client not set", endpoint,
                    command, command.getClass().getSimpleName());
            return;
        }

        Integer value = temperatureToValue(command);

        if (value == null) {
            log.warn("[{}]: Temperature measurement update {} [{}] was not processed", endpoint,
                    command, command.getClass().getSimpleName());
            return;
        }

        attributeClient.setValue(value);
        attributeClient.reportValue(value);
    }*/

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID) {
    if (endpoint.getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null
        && endpoint.getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null) {
      log.trace("[{}]: Temperature measurement cluster not found {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() == ZclClusterType.TEMPERATURE_MEASUREMENT
        && attribute.getId() == ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE) {
      updateChannelState(valueToTemperature((Integer) val));
    }
  }
}
