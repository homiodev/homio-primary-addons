package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.clusters.ZclTemperatureMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import tec.uom.se.unit.Units;

/** Indicates the current temperature Converter for the temperature channel */
@ZigBeeConverter(
    name = "zigbee:measurement_temperature",
    linkType = VariableType.Float,
    serverClusters = {ZclTemperatureMeasurementCluster.CLUSTER_ID},
    clientCluster = ZclTemperatureMeasurementCluster.CLUSTER_ID,
    category = "Temperature")
public class ZigBeeConverterTemperature extends ZigBeeBaseChannelConverter
    implements ZclAttributeListener {

  private ZclTemperatureMeasurementCluster cluster;
  private ZclTemperatureMeasurementCluster clusterClient;
  private ZclAttribute attribute;
  private ZclAttribute attributeClient;

  public static QuantityType valueToTemperature(int value) {
    return new QuantityType<>(BigDecimal.valueOf(value, 2), Units.CELSIUS);
  }

  @Override
  public boolean acceptEndpoint(
      ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    if (endpoint.getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null
        && endpoint.getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null) {
      log.trace("[{}]: Temperature measurement cluster not found {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public void initializeDevice() {
    ZclTemperatureMeasurementCluster clientCluster =
        getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (clientCluster == null) {
      // Nothing to do, but we still return success
      return;
    }

    ZclTemperatureMeasurementCluster serverCluster =
        getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (serverCluster == null) {
      log.error(
          "[{}]: Error opening device temperature measurement cluster {}", entityID, this.endpoint);
      throw new RuntimeException("Error opening device temperature measurement cluster");
    }

    try {
      CommandResult bindResponse = bind(serverCluster).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting
        ZclAttribute attribute =
            serverCluster.getAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
        CommandResult reportingResponse =
            attribute.setReporting(1, REPORTING_PERIOD_DEFAULT_MAX, 10).get();
        handleReportingResponse(reportingResponse);
      } else {
        log.debug(
            "[{}]: Failed to bind temperature measurement cluster {}", entityID, this.endpoint);
      }
    } catch (Exception e) {
      log.error("[{}]: Exception setting reporting", this.endpoint, e);
      throw new RuntimeException("Exception setting reporting");
    }
  }

  @Override
  public void initializeConverter() {
    cluster = getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
    if (cluster != null) {
      attribute = cluster.getAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
      // Add a listener
      cluster.addAttributeListener(this);
    } else {
      clusterClient = getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
      attributeClient =
          clusterClient.getLocalAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
      attributeClient.setImplemented(true);
    }

    if (cluster == null && clusterClient == null) {
      log.error(
          "[{}]: Error opening device temperature measurement cluster {}", entityID, endpoint);
      throw new RuntimeException("Error opening device temperature measurement cluster");
    }
  }

  @Override
  public void disposeConverter() {
    if (cluster != null) {
      cluster.removeAttributeListener(this);
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
  protected void handleRefresh() {
    if (attribute != null) {
      attribute.readValue(0);
    }
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
