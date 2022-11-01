package org.touchhome.bundle.zigbee.model;

import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.repository.AbstractRepository;

@Repository
public class ZigBeeDeviceRepository extends AbstractRepository<ZigBeeDeviceEntity> {

  public ZigBeeDeviceRepository() {
    super(ZigBeeDeviceEntity.class);
  }
}
