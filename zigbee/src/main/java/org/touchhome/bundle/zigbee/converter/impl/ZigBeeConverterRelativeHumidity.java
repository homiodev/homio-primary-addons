package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.RELATIVE_HUMIDITY_MEASUREMENT;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclRelativeHumidityMeasurementCluster;
import java.math.BigDecimal;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.api.state.DecimalType;

@ZigBeeConverter(name = "zigbee:measurement_relativehumidity", linkType = VariableType.Float,
                 clientCluster = ZclRelativeHumidityMeasurementCluster.CLUSTER_ID, category = "Humidity")
public class ZigBeeConverterRelativeHumidity extends ZigBeeInputBaseConverter<ZclRelativeHumidityMeasurementCluster> {

  public ZigBeeConverterRelativeHumidity() {
    super(RELATIVE_HUMIDITY_MEASUREMENT, ZclRelativeHumidityMeasurementCluster.ATTR_MEASUREDVALUE);
  }

  @Override
  protected void updateValue(Object val, ZclAttribute attribute) {
    Integer value = (Integer) val;
    updateChannelState(new DecimalType(BigDecimal.valueOf(value, 2)));
  }
}
