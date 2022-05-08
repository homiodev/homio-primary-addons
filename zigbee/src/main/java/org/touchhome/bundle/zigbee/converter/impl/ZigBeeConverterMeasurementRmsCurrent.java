package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.clusters.ZclElectricalMeasurementCluster;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.QuantityType;
import tec.uom.se.unit.Units;

import java.math.BigDecimal;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.ELECTRICAL_MEASUREMENT;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineDivisor;
import static org.touchhome.bundle.zigbee.converter.impl.ZigBeeConverterMeasurementRmsVoltage.determineMultiplier;

/**
 * ZigBee channel converter for RMS current measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:electrical_rmscurrent", clientClusters = {ZclElectricalMeasurementCluster.CLUSTER_ID})
public class ZigBeeConverterMeasurementRmsCurrent extends ZigBeeInputBaseConverter {

    public ZigBeeConverterMeasurementRmsCurrent() {
        super(ELECTRICAL_MEASUREMENT, ZclElectricalMeasurementCluster.ATTR_RMSCURRENT, 3,
                REPORTING_PERIOD_DEFAULT_MAX, 1);
    }

    private Integer divisor;
    private Integer multiplier;

    @Override
    protected void afterInitializeConverter() {
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
