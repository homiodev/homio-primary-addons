package org.touchhome.bundle.zigbee.handler;

import com.zsmartsystems.zigbee.dongle.cc2531.ZigBeeDongleTiCc2531;
import com.zsmartsystems.zigbee.transport.TransportConfig;
import com.zsmartsystems.zigbee.transport.TransportConfigOption;
import com.zsmartsystems.zigbee.transport.ZigBeeTransportTransmit;
import com.zsmartsystems.zigbee.zcl.clusters.ZclIasZoneCluster;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.port.PortFlowControl;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.zigbee.ZigBeeCoordinatorHandler;
import org.touchhome.bundle.zigbee.internal.ZigBeeSerialPort;

@Log4j2
public class CC2531Handler extends ZigBeeCoordinatorHandler {

  public CC2531Handler(EntityContext entityContext) {
    super(entityContext);
  }

  @Override
  public String getEntityID() {
    return getClass().getSimpleName();
  }

  @Override
  protected void initializeDongle() {
    log.debug("Initializing ZigBee CC2531 serial bridge handler.");

    ZigBeeTransportTransmit dongle = createDongle();
    TransportConfig transportConfig = createTransportConfig();

    startZigBee(dongle, transportConfig);
  }

  private ZigBeeTransportTransmit createDongle() {
    ZigBeeSerialPort serialPort = new ZigBeeSerialPort(
        "cc2531",
        entityContext,
        TouchHomeUtils.getSerialPort(getCoordinator().getPort()),
        getCoordinator().getPortBaud(),
        PortFlowControl.FLOWCONTROL_OUT_RTSCTS,
        () -> getCoordinator().setStatus(Status.ERROR, "PORT_COMMUNICATION_ERROR"),
        (port -> {
          if (!getCoordinator().getPort().equals(port.getSystemPortName())) {
            getCoordinator().setPort(port.getSystemPortName());
            entityContext.save(getCoordinator(), false);
          }
        }));
    return new ZigBeeDongleTiCc2531(serialPort);
  }

  private TransportConfig createTransportConfig() {
    TransportConfig transportConfig = new TransportConfig();

    // The CC2531EMK dongle doesn't pass the MatchDescriptor commands to the stack, so we can't manage our services
    // directly. Instead, register any services we want to support so the CC2531EMK can handle the MatchDescriptor.
    Set<Integer> clusters = new HashSet<>();
    clusters.add(ZclIasZoneCluster.CLUSTER_ID);
    transportConfig.addOption(TransportConfigOption.SUPPORTED_OUTPUT_CLUSTERS, clusters);
    return transportConfig;
  }
}
