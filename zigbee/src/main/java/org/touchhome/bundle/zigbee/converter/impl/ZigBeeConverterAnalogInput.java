package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.clusters.ZclAnalogInputBasicCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.EntityContextVar.VariableType;

/**
 * Analog Input Sensor(Switch) Indicates a analog input sensor state Converter for the binary input sensor.
 */
@ZigBeeConverter(name = "zigbee:analoginput", linkType = VariableType.Float,
    clientCluster = ZclAnalogInputBasicCluster.CLUSTER_ID, category = "")
public class ZigBeeConverterAnalogInput extends ZigBeeInputBaseConverter {

  public ZigBeeConverterAnalogInput() {
    super(ZclClusterType.ANALOG_INPUT_BASIC, ZclAnalogInputBasicCluster.ATTR_PRESENTVALUE);
  }
}
