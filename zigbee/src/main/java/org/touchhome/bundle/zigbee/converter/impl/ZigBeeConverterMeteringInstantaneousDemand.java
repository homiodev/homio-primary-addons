package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.clusters.ZclMeteringCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * ZigBee channel converter for instantaneous demand measurement
 */
@Log4j2
@ZigBeeConverter(name = "zigbee:metering_instantaneous", clientClusters = {ZclMeteringCluster.CLUSTER_ID})
public class ZigBeeConverterMeteringInstantaneousDemand extends ZigBeeInputBaseConverter
        implements ZclAttributeListener {

    private ZclMeteringCluster clusterMetering;

    private ZclAttribute attribute;

    private double divisor = 1.0;
    private double multiplier = 1.0;

    @Override
    public int getInputAttributeId() {
        return ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND;
    }

    @Override
    public ZclClusterType getZclClusterType() {
        return ZclClusterType.METERING;
    }

    @Override
    protected int getInputClusterType() {
        return ZclMeteringCluster.CLUSTER_ID;
    }

    @Override
    float getReportableChange() {
        return 1;
    }

    @Override
    int getMinReportInterval() {
        return 3;
    }

    @Override
    public boolean initializeConverter() {
        clusterMetering = (ZclMeteringCluster) endpoint.getInputCluster(ZclMeteringCluster.CLUSTER_ID);
        if (clusterMetering == null) {
            log.error("{}: Error opening metering cluster", endpoint.getIeeeAddress());
            return false;
        }

        attribute = clusterMetering.getAttribute(getInputAttributeId());
        if (!configureReporting(attribute)) {
            return false;
        }

        determineDivisorAndMultiplier();

        // Add a listener
        clusterMetering.addAttributeListener(this);
        return true;
    }

    @Override
    public void disposeConverter() {
        clusterMetering.removeAttributeListener(this);
    }

    @Override
    public void handleRefresh() {
        attribute.readValue(0);
    }

    @Override
    public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
        ZclMeteringCluster cluster = (ZclMeteringCluster) endpoint.getInputCluster(ZclMeteringCluster.CLUSTER_ID);
        if (cluster == null) {
            return false;
        }
        try {
            if (!cluster.discoverAttributes(false).get()
                    && !cluster.isAttributeSupported(getInputAttributeId())) {
                return false;
            } else {
                ZclAttribute attribute = cluster.getAttribute(getInputAttributeId());
                if (attribute.readValue(Long.MAX_VALUE) == null) {
                    return false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("{}: Exception discovering attributes in metering cluster", endpoint.getIeeeAddress(), e);
            return false;
        }
        return true;
    }

    @Override
    public void attributeUpdated(ZclAttribute attribute, Object val) {
        log.debug("{}: ZigBee attribute reports {}", endpoint.getIeeeAddress(), attribute);
        if (attribute.getClusterType() == ZclClusterType.METERING
                && attribute.getId() == getInputAttributeId()) {
            double value = (Integer) val;
            BigDecimal valueCalibrated = BigDecimal.valueOf(value * multiplier / divisor);
            updateChannelState(new DecimalType(valueCalibrated));
        }
    }

    private void determineDivisorAndMultiplier() {
        ZclAttribute divisorAttribute = clusterMetering.getAttribute(ZclMeteringCluster.ATTR_DIVISOR);
        ZclAttribute multiplierAttribute = clusterMetering.getAttribute(ZclMeteringCluster.ATTR_MULTIPLIER);

        Integer iDiv = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
        Integer iMult = (Integer) multiplierAttribute.readValue(Long.MAX_VALUE);
        if (iDiv == null || iMult == null) {
            iDiv = 1;
            iMult = 1;
        }

        divisor = iDiv;
        multiplier = iMult;
    }
}
