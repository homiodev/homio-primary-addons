package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;

import java.util.concurrent.ExecutionException;

@Log4j2
@RequiredArgsConstructor
public abstract class ZigBeeInputBaseConverter extends ZigBeeBaseChannelConverter implements ZclAttributeListener {

    private final ZclClusterType zclClusterType;
    @Getter
    private final int attributeId;
    private int reportingFailedPollingInterval;
    private final Integer reportMinInterval;
    private final Integer reportMaxInterval;
    private final Object reportableChange;
    @Getter
    private ZclCluster zclCluster;
    @Getter
    private ZclAttribute attribute;

    public ZigBeeInputBaseConverter(ZclClusterType zclClusterType, int attributeId, int reportingFailedPollingInterval) {
        this.zclClusterType = zclClusterType;
        this.attributeId = attributeId;
        this.reportMinInterval = null;
        this.reportMaxInterval = null;
        this.reportableChange = null;
        this.reportingFailedPollingInterval = reportingFailedPollingInterval;
    }

    @Override
    public boolean initializeDevice() {
        log.debug("{}/{}: Initialising {} device cluster", endpoint.getIeeeAddress(), endpoint.getEndpointId(), getClass().getSimpleName());

        ZclCluster zclCluster = getZclClusterInternal();
        boolean success = false;
        if (zclCluster != null) {
            try {
                CommandResult bindResponse = bind(zclCluster).get();
                if (bindResponse.isSuccess()) {
                    ZclAttribute attribute = zclCluster.getAttribute(this.attributeId);

                    if (reportMinInterval == null || reportMaxInterval == null) {
                        CommandResult reportingResponse = attribute.setReporting(zigBeeDevice.getZigBeeDeviceEntity().getReportingTimeMin(),
                                zigBeeDevice.getZigBeeDeviceEntity().getReportingTimeMax(),
                                zigBeeDevice.getZigBeeDeviceEntity().getReportingChange()).get();
                        handleReportingResponse(reportingResponse, reportingFailedPollingInterval,
                                zigBeeDevice.getZigBeeDeviceEntity().getPoolingPeriod());
                    } else {
                        CommandResult reportingResponse = attribute.setReporting(reportMinInterval, reportMaxInterval, reportableChange).get();
                        handleReportingResponseDuringInitializeDevice(reportingResponse);
                    }
                    success = true;
                } else {
                    log.error("{}/{}: Error 0x{} setting server binding for cluster {}", endpoint.getIeeeAddress(),
                            endpoint.getEndpointId(), Integer.toHexString(bindResponse.getStatusCode()), zclClusterType);
                    success = initializeDeviceFailed();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("{}/{}: Exception setting reporting ", endpoint.getIeeeAddress(), endpoint.getEndpointId(), e);
                return false;
            }
        }
        if (success) {
            return afterInitializeDevice();
        }
        return success;
    }

    protected void handleReportingResponseDuringInitializeDevice(CommandResult reportingResponse) {
        handleReportingResponse(reportingResponse);
    }

    protected boolean afterInitializeDevice() {
        return true;
    }

    protected boolean initializeDeviceFailed() {
        return true;
    }

    @Override
    public boolean initializeConverter() {
        zclCluster = getZclClusterInternal();

        if (zclCluster == null) {
            log.error("{}/{}: Error opening cluster {}", endpoint.getIeeeAddress(), endpoint.getEndpointId(),
                    zclClusterType);
            return false;
        }

        attribute = zclCluster.getAttribute(attributeId);
        if (attribute == null) {
            log.error("{}/{}: Error opening device {} attribute", endpoint.getIeeeAddress(),
                    endpoint.getEndpointId(), zclClusterType);
            return false;
        }

        zclCluster.addAttributeListener(this);
        afterInitializeConverter();
        return true;
    }

    protected void afterInitializeConverter() {

    }

    @Override
    public void disposeConverter() {
        log.debug("{}/{}: Closing device input cluster {}", endpoint.getIeeeAddress(), endpoint.getEndpointId(),
                zclClusterType);

        zclCluster.removeAttributeListener(this);
    }

    @Override
    protected void handleRefresh() {
        attribute.readValue(0);
    }

    @Override
    public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
        ZclCluster cluster = endpoint.getInputCluster(zclClusterType.getId());
        if (cluster == null) {
            log.trace("{}/{}: Binary input sensing cluster not found", endpoint.getIeeeAddress(), endpoint.getEndpointId());
            return false;
        }
        return acceptEndpointExtra(cluster);
    }

    protected boolean acceptEndpointExtra(ZclCluster cluster) {
        return true;
    }

    @Override
    public void attributeUpdated(ZclAttribute attribute, Object val) {
        log.debug("{}/{}: ZigBee attribute reports {}", endpoint.getIeeeAddress(), endpoint.getEndpointId(), attribute);
        if (attribute.getClusterType() == zclClusterType && attribute.getId() == attributeId) {
            updateValue(val, attribute);
        }
    }

    protected void updateValue(Object val, ZclAttribute attribute) {
        if (val instanceof Double) {
            updateChannelState(new DecimalType((Double) val));
        } else if (val instanceof Integer) {
            updateChannelState(new DecimalType((Integer) val));
        } else if (val instanceof Boolean) {
            updateChannelState(OnOffType.of((Boolean) val));
        } else {
            throw new IllegalStateException("Unable to find value handler for type: " + val);
        }
    }

    public void updateServerPoolingPeriod() {
        updateServerPoolingPeriod(zclCluster, attributeId, true);
    }

    protected ZclCluster getZclClusterInternal() {
        ZclCluster zclCluster = endpoint.getInputCluster(zclClusterType.getId());
        if (zclCluster == null) {
            log.error("{}/{}: Error opening cluster {}", endpoint.getIeeeAddress(), endpoint.getEndpointId(),
                    zclClusterType);
            return null;
        }
        return zclCluster;
    }
}
