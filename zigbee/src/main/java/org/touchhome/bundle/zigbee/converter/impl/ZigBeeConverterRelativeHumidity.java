package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.RELATIVE_HUMIDITY_MEASUREMENT;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclRelativeHumidityMeasurementCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;


/**
 * Indicates the current relative humidity Converter for the relative humidity channel
 */
@ZigBeeConverter(name = "zigbee:measurement_relativehumidity", linkType = VariableType.Float,
    clientCluster = ZclRelativeHumidityMeasurementCluster.CLUSTER_ID, category = "Humidity")
public class ZigBeeConverterRelativeHumidity extends ZigBeeInputBaseConverter {

  public ZigBeeConverterRelativeHumidity() {
    super(RELATIVE_HUMIDITY_MEASUREMENT, ZclRelativeHumidityMeasurementCluster.ATTR_MEASUREDVALUE, 1,
        REPORTING_PERIOD_DEFAULT_MAX, 10, POLLING_PERIOD_HIGH);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, false, false, entityContext);
  }

  @Override
  protected void handleReportingResponseOnBind(CommandResult reportingResponse) {
    handleReportingResponse(reportingResponse, POLLING_PERIOD_DEFAULT, REPORTING_PERIOD_DEFAULT_MAX);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(BigDecimal.valueOf(value, 2)));
  }
}
