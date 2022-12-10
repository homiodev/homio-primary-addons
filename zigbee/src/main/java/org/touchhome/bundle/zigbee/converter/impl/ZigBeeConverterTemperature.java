package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclTemperatureMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.math.BigDecimal;
import lombok.Getter;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import org.touchhome.bundle.zigbee.converter.config.ZclReportingConfig;
import tec.uom.se.unit.Units;

/**
 * Indicates the current temperature Converter for the temperature channel
 */
@ZigBeeConverter(
    name = "zigbee:measurement_temperature",
    linkType = VariableType.Float,
    serverClusters = {ZclTemperatureMeasurementCluster.CLUSTER_ID},
    clientCluster = ZclTemperatureMeasurementCluster.CLUSTER_ID,
    category = "Temperature")
public class ZigBeeConverterTemperature extends ZigBeeInputBaseConverter<ZclTemperatureMeasurementCluster> {

  @Getter
  private boolean asClient;

  public ZigBeeConverterTemperature() {
    super(ZclClusterType.TEMPERATURE_MEASUREMENT, ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
  }

  public static QuantityType valueToTemperature(int value) {
    return new QuantityType<>(BigDecimal.valueOf(value, 2), Units.CELSIUS);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    if (endpoint.getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null
        && endpoint.getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID) == null) {
      log.trace("[{}]: Temperature measurement cluster not found {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public void initialize() {
    if (getInputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID)) {
      super.initialize();
      if (configuration.isReportConfigurable()) {
        configReporting = new ZclReportingConfig(getEntity());
      }
      initializeBinding();
      initializeAttribute();
    } else {
      this.asClient = true;
      zclCluster = getOutputCluster(ZclTemperatureMeasurementCluster.CLUSTER_ID);
      attribute = zclCluster.getLocalAttribute(ZclTemperatureMeasurementCluster.ATTR_MEASUREDVALUE);
      attribute.setImplemented(true);
    }
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    updateChannelState(valueToTemperature((Integer) val));
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
}
