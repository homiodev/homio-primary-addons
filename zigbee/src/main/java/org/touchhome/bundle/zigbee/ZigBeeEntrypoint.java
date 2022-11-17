package org.touchhome.bundle.zigbee;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.zigbee.model.ZigbeeCoordinatorEntity;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZigBeeEntrypoint implements BundleEntrypoint {

  private final EntityContext entityContext;

  @Override
  public void init() {
    entityContext.ui().registerConsolePluginName("ZIGBEE");
    entityContext.var().createGroup("zigbee", "ZigBee", true, "fab fa-laravel", "#ED3A3A");

    // listen for port changes and reinitialise coordinator if port became available
    entityContext.event().addPortChangeStatusListener("zigbee-ports", o -> {
      for (ZigbeeCoordinatorEntity coordinator : entityContext.findAll(ZigbeeCoordinatorEntity.class)) {
        if (StringUtils.isNotEmpty(coordinator.getPort()) &&
            coordinator.isStart() &&
            coordinator.getStatus().isOffline() &&
            TouchHomeUtils.getSerialPort(coordinator.getPort()) != null) {
          // try re-initialise coordinator
          coordinator.getService().entityUpdated(coordinator);
        }
      }
    });
  }

  @Override
  public int order() {
    return 600;
  }
}
