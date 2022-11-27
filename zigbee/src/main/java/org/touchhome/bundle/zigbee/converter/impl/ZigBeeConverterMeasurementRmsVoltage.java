package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ELECTRICAL_MEASUREMENT;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

/**
 * The current RMS voltage measurement ZigBee channel converter for RMS voltage measurement
 */
@ZigBeeConverter(name = "zigbee:electrical_rmsvoltage", linkType = VariableType.Float,
    clientCluster = ZclElectricalMeasurementCluster.CLUSTER_ID, category = "Energy")
public class ZigBeeConverterMeasurementRmsVoltage extends ZigBeeInputBaseConverter {

  private Integer divisor;
  private Integer multiplier;

  public ZigBeeConverterMeasurementRmsVoltage() {
    super(ELECTRICAL_MEASUREMENT, ZclElectricalMeasurementCluster.ATTR_RMSVOLTAGE, 3,
        REPORTING_PERIOD_DEFAULT_MAX, 1, POLLING_PERIOD_HIGH);
  }

  static Integer determineDivisor(ZclCluster zclCluster) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(ZclMeteringCluster.ATTR_DIVISOR);
    Integer value = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? 1 : value;
  }

  static Integer determineMultiplier(ZclCluster zclCluster) {
    ZclAttribute divisorAttribute = zclCluster.getAttribute(ZclMeteringCluster.ATTR_MULTIPLIER);
    Integer value = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
    return value == null ? 1 : value;
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, true, true, entityContext);
  }

  @Override
  public void initializeConverter() {
    super.initializeConverter();
    this.divisor = determineDivisor(getZclCluster());
    this.multiplier = determineMultiplier(getZclCluster());
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    BigDecimal valueInVolts = BigDecimal.valueOf((long) value * multiplier / divisor);
    updateChannelState(new QuantityType<>(valueInVolts, Units.VOLT));
  }
}
