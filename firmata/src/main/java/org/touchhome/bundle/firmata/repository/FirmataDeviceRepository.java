package org.touchhome.bundle.firmata.repository;

import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.firmata.model.FirmataBaseEntity;
import org.touchhome.bundle.firmata.provider.FirmataDeviceCommunicator;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FirmataDeviceRepository extends AbstractRepository<FirmataBaseEntity> {

    private final Map<String, FirmataDeviceCommunicator> entityIDToDeviceCommunicator = new HashMap<>();
    private final EntityContext entityContext;

    public FirmataDeviceRepository(EntityContext entityContext) {
        super(FirmataBaseEntity.class, FirmataBaseEntity.PREFIX);
        this.entityContext = entityContext;
    }

    @Override
    public void updateEntityAfterFetch(FirmataBaseEntity entity) {
        super.updateEntityAfterFetch(entity);
        entity.setFirmataDeviceCommunicator(entityIDToDeviceCommunicator.computeIfAbsent(entity.getEntityID(),
                ignore -> entity.createFirmataDeviceType(entityContext)));
    }

    @Override
    public FirmataBaseEntity deleteByEntityID(String entityID) {
        FirmataDeviceCommunicator firmataDeviceCommunicator = entityIDToDeviceCommunicator.remove(entityID);
        if (firmataDeviceCommunicator != null) {
            firmataDeviceCommunicator.destroy();
        }
        return super.deleteByEntityID(entityID);
    }
}
