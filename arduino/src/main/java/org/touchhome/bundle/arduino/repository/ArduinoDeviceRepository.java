package org.touchhome.bundle.arduino.repository;

import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.repository.AbstractDeviceRepository;
import org.touchhome.bundle.arduino.model.ArduinoDeviceEntity;

@Repository
public class ArduinoDeviceRepository extends AbstractDeviceRepository<ArduinoDeviceEntity> {

    public ArduinoDeviceRepository() {
        super(ArduinoDeviceEntity.class, ArduinoDeviceEntity.PREFIX);
    }
}
