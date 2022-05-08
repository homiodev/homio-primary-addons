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

@Log4j2
@ZigBeeConverter(name = "zigbee:electrical_activepower", clientClusters = {ZclElectricalMeasurementCluster.CLUSTER_ID})
public class ZigBeeConverterMeasurementPower extends ZigBeeInputBaseConverter {

    private Integer divisor;
    private Integer multiplier;

    public ZigBeeConverterMeasurementPower() {
        super(ELECTRICAL_MEASUREMENT, ZclElectricalMeasurementCluster.ATTR_ACTIVEPOWER, 3,
                REPORTING_PERIOD_DEFAULT_MAX, 1);
    }

    @Override
    protected void afterInitializeConverter() {
        this.divisor = determineDivisor(getZclCluster());
        this.multiplier = determineMultiplier(getZclCluster());
    }

    @Override
    protected void updateValue(Object val, ZclAttribute attribute) {
        Integer value = (Integer) val;
        BigDecimal valueInWatt = BigDecimal.valueOf((long) value * multiplier / divisor);
        updateChannelState(new QuantityType<>(valueInWatt, Units.WATT));
    }
}
