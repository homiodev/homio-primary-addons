package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

import java.math.BigDecimal;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclPowerConfigurationCluster.ATTR_BATTERYVOLTAGE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.POWER_CONFIGURATION;

/**
 * Converter for the battery voltage channel.
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:battery_voltage", clientClusters = {ZclPowerConfigurationCluster.CLUSTER_ID})
public class ZigBeeConverterBatteryVoltage extends ZigBeeInputBaseConverter {

    public ZigBeeConverterBatteryVoltage() {
        super(POWER_CONFIGURATION, ATTR_BATTERYVOLTAGE, 600, REPORTING_PERIOD_DEFAULT_MAX, 1);
    }

    @Override
    protected boolean acceptEndpointExtra(ZclCluster cluster) {
        if (((ZclPowerConfigurationCluster) cluster).getBatteryVoltage(Long.MAX_VALUE) == null) {
            log.warn("{}/{}: Exception discovering attributes in power configuration cluster",
                    endpoint.getIeeeAddress(), endpoint.getEndpointId());
            return false;
        }

        return true;
    }

    @Override
    protected void updateValue(Object val, ZclAttribute attribute) {
        Integer value = (Integer) val;
        if (value == 0xFF) {
            // The value 0xFF indicates an invalid or unknown reading.
            return;
        }
        BigDecimal valueInVolt = BigDecimal.valueOf(value, 1);
        updateChannelState(new QuantityType<>(valueInVolt, Units.VOLT));
    }
}
