package org.touchhome.bundle.zigbee.converter.impl;

import static com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster.ATTR_ZONETYPE;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.ZclCommand;
import com.zsmartsystems.zigbee.zcl.ZclCommandListener;
import com.zsmartsystems.zigbee.zcl.ZclStatus;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneStatusChangeNotificationCommand;
import com.zsmartsystems.zigbee.zcl.clusters.iaszone.ZoneTypeEnum;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;

/**
 * Converter for the IAS zone sensors. This is an abstract class used as a base for different IAS sensors.
 */
public abstract class ZigBeeConverterIas extends ZigBeeBaseChannelConverter
    implements ZclCommandListener, ZclAttributeListener {

  /**
   * CIE Zone Status Attribute flags
   */
  protected static final int CIE_ALARM1 = 0x0001;
  protected static final int CIE_ALARM2 = 0x0002;
  protected static final int CIE_TAMPER = 0x0004;
  protected static final int CIE_BATTERY = 0x0008;
  protected static final int CIE_SUPERVISION = 0x0010;
  protected static final int CIE_RESTORE = 0x0020;
  protected static final int CIE_TROUBLE = 0x0040;
  protected static final int CIE_ACMAINS = 0x0080;
  protected static final int CIE_TEST = 0x0100;
  protected static final int CIE_BATTERYDEFECT = 0x0200;
  protected int bitTest = CIE_ALARM1;
  private ZclIasZoneCluster clusterIasZone;

  @Override
  public void initializeDevice() {
    log.debug("[{}]: Initialising device IAS Zone cluster {}", entityID, endpoint);

    ZclIasZoneCluster serverClusterIasZone = getInputCluster(ZclIasZoneCluster.CLUSTER_ID);
    if (serverClusterIasZone == null) {
      log.error("[{}]: Error opening IAS zone cluster {}", entityID, endpoint);
      throw new RuntimeException("Error opening IAS zone cluster");
    }

    try {
      CommandResult bindResponse = bind(serverClusterIasZone).get();
      if (bindResponse.isSuccess()) {
        // Configure reporting - no faster than once per second - no slower than 2 hours.
        ZclAttribute attribute = serverClusterIasZone.getAttribute(ZclIasZoneCluster.ATTR_ZONESTATUS);
        CommandResult reportingResponse = serverClusterIasZone
            .setReporting(attribute, 3, REPORTING_PERIOD_DEFAULT_MAX).get();
        handleReportingResponse(reportingResponse);
      }
    } catch (Exception e) {
      log.debug("[{}]: Exception configuring ias zone status reporting {}", entityID, endpoint, e);
      throw new RuntimeException("Exception configuring ias zone status reporting");
    }
  }

  @Override
  public void initializeConverter() {
    log.debug("[{}]: Initialising device IAS Zone cluster {}", entityID, endpoint);

    clusterIasZone = getInputCluster(ZclIasZoneCluster.CLUSTER_ID);
    if (clusterIasZone == null) {
      log.error("[{}]: Error opening IAS zone cluster {}", entityID, endpoint);
      throw new RuntimeException("Error opening IAS zone cluster");
    }
    // Add a listener, then request the status
    clusterIasZone.addCommandListener(this);
    clusterIasZone.addAttributeListener(this);
  }

  @Override
  public void disposeConverter() {
    log.debug("[{}]: Closing device IAS zone cluster {}", entityID, endpoint);

    clusterIasZone.removeCommandListener(this);
    clusterIasZone.removeAttributeListener(this);
  }

  @Override
  protected void handleRefresh() {
    clusterIasZone.getZoneStatus(0);
  }

  protected boolean supportsIasChannel(ZigBeeEndpoint endpoint, String entityID, ZoneTypeEnum requiredZoneType) {
    if (!hasIasZoneInputCluster(endpoint, entityID)) {
      return false;
    }

    ZclIasZoneCluster cluster = (ZclIasZoneCluster) endpoint.getInputCluster(ZclIasZoneCluster.CLUSTER_ID);
    Integer zoneTypeId = null;
    ZclAttribute zclAttribute = cluster.getAttribute(ATTR_ZONETYPE);
    for (int retry = 0; retry < 3; retry++) {
      zoneTypeId = (Integer) zclAttribute.readValue(Long.MAX_VALUE);
      if (zoneTypeId != null) {
        break;
      }
    }
    if (zoneTypeId == null) {
      log.debug("[{}]: Did not get IAS zone type {}", entityID, endpoint);
      return false;
    }
    ZoneTypeEnum zoneType = ZoneTypeEnum.getByValue(zoneTypeId);
    log.debug("[{}]: IAS zone type {} {}", entityID, zoneType, endpoint);
    return zoneType == requiredZoneType;
  }

  protected boolean hasIasZoneInputCluster(ZigBeeEndpoint endpoint, String entityID) {
    if (endpoint.getInputCluster(ZclIasZoneCluster.CLUSTER_ID) == null) {
      log.trace("[{}]: IAS zone cluster not found {}", entityID, endpoint);
      return false;
    }

    return true;
  }

  @Override
  public boolean commandReceived(ZclCommand command) {
    log.debug("[{}]: ZigBee command report {}. {}", entityID, command, endpoint);
    if (command instanceof ZoneStatusChangeNotificationCommand) {
      ZoneStatusChangeNotificationCommand zoneStatus = (ZoneStatusChangeNotificationCommand) command;
      updateChannelState(zoneStatus.getZoneStatus());

      clusterIasZone.sendDefaultResponse(command, ZclStatus.SUCCESS);
      return true;
    }

    return false;
  }

  @Override
  public void attributeUpdated(ZclAttribute attribute, Object val) {
    log.debug("[{}]: ZigBee attribute reports {}. {}", entityID, attribute, endpoint);
    if (attribute.getClusterType() == ZclClusterType.IAS_ZONE
        && attribute.getId() == ZclIasZoneCluster.ATTR_ZONESTATUS) {
      updateChannelState((Integer) val);
    }
  }

  private void updateChannelState(Integer state) {
    updateChannelState(((state & bitTest) != 0) ? OnOffType.ON : OnOffType.OFF);
  }
}
