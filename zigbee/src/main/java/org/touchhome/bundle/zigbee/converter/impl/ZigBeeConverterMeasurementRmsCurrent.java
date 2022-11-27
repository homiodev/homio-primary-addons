package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ELECTRICAL_MEASUREMENT;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineDivisor;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineMultiplier;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

/**
 * The current RMS current measurement ZigBee channel converter for RMS current measurement
 */
@ZigBeeConverter(name = "zigbee:electrical_rmscurrent", linkType = VariableType.Float,
    clientCluster = ZclElectricalMeasurementCluster.CLUSTER_ID, category = "Energy")
public class ZigBeeConverterMeasurementRmsCurrent extends ZigBeeInputBaseConverter {

  private Integer divisor;
  private Integer multiplier;

  public ZigBeeConverterMeasurementRmsCurrent() {
    super(ELECTRICAL_MEASUREMENT, ZclElectricalMeasurementCluster.ATTR_RMSCURRENT, 3,
        REPORTING_PERIOD_DEFAULT_MAX, 1, POLLING_PERIOD_HIGH);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return super.acceptEndpoint(endpoint, entityID, true, true, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_HIGH, REPORTING_PERIOD_DEFAULT_MAX);
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
    BigDecimal valueInAmpere = BigDecimal.valueOf((long) value * multiplier / divisor);
    updateChannelState(new QuantityType<>(valueInAmpere, Units.AMPERE));
  }
}
