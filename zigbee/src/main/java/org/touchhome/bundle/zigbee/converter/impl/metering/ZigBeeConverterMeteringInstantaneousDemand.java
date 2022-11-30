package org.touchhome.bundle.zigbee.converter.impl.metering;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverter;

/**
 * The instantaneous demand from the metering system ZigBee channel converter for instantaneous demand measurement
 */
@ZigBeeConverter(name = "zigbee:metering_instantdemand", linkType = VariableType.Float,
    clientCluster = ZclMeteringCluster.CLUSTER_ID, category = "Number")
public class ZigBeeConverterMeteringInstantaneousDemand extends ZigBeeConverterMeteringBaseConverter {

  private double divisor = 1.0;
  private double multiplier = 1.0;

  public ZigBeeConverterMeteringInstantaneousDemand() {
    super(ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND);
  }

  @Override
  public void initializeConverter() {
    super.initializeConverter();
    this.divisor = readAttribute(zclCluster, ZclMeteringCluster.ATTR_DIVISOR, 1);
    this.multiplier = readAttribute(zclCluster, ZclMeteringCluster.ATTR_DIVISOR, 1);
  }

  @Override
  public boolean acceptEndpoint(ZigBeeEndpoint endpoint, String entityID, EntityContext entityContext) {
    return acceptEndpoint(endpoint, entityID, true, true, entityContext);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    BigDecimal valueCalibrated = BigDecimal.valueOf((Integer) val * multiplier / divisor);
    updateChannelState(new DecimalType(valueCalibrated));
  }
}
