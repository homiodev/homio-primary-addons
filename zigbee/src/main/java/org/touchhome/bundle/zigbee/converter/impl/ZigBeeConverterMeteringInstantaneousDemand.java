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

    private Integer divisor;
    private Integer multiplier;

    @Override
    public int getInputAttributeId() {
        return ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND;
    }

    @Override
    public ZclClusterType getZclClusterType() {
        return ZclClusterType.ELECTRICAL_MEASUREMENT;
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

        attribute = clusterMetering.getAttribute(ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND);

        determineDivisorAndMultiplier(clusterMetering);

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
                    && !cluster.isAttributeSupported(ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND)) {
                return false;
            } else {
                ZclAttribute attribute = cluster.getAttribute(ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND);
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
        if (attribute.getCluster() == ZclClusterType.ELECTRICAL_MEASUREMENT
                && attribute.getId() == ZclMeteringCluster.ATTR_INSTANTANEOUSDEMAND) {
            Integer value = (Integer) val;
            BigDecimal valueCalibrated = BigDecimal.valueOf(value * multiplier / divisor);
            updateChannelState(new DecimalType(valueCalibrated));
        }
    }

    private void determineDivisorAndMultiplier(ZclMeteringCluster serverClusterMeasurement) {
        ZclAttribute divisorAttribute = clusterMetering.getAttribute(ZclMeteringCluster.ATTR_DIVISOR);
        ZclAttribute multiplierAttribute = clusterMetering.getAttribute(ZclMeteringCluster.ATTR_MULTIPLIER);

        divisor = (Integer) divisorAttribute.readValue(Long.MAX_VALUE);
        multiplier = (Integer) multiplierAttribute.readValue(Long.MAX_VALUE);
        if (divisor == null || multiplier == null) {
            divisor = 1;
            multiplier = 1;
        }
    }
}
