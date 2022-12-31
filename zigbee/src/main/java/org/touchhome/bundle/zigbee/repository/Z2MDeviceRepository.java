package org.touchhome.bundle.zigbee.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.repository.AbstractRepository;
import org.touchhome.bundle.zigbee.model.z2m.Z2MDeviceEntity;
import org.touchhome.bundle.zigbee.model.z2m.Z2MLocalCoordinatorEntity;
import org.touchhome.bundle.zigbee.service.z2m.Z2MDeviceService;

@Repository
public class Z2MDeviceRepository extends AbstractRepository<Z2MDeviceEntity> {

    private final EntityContext entityContext;

    public Z2MDeviceRepository(EntityContext entityContext) {
        super(Z2MDeviceEntity.class);
        this.entityContext = entityContext;
    }

    @Override
    public List<Z2MDeviceEntity> listAll() {
        List<Z2MDeviceEntity> list = new ArrayList<>();
        for (Z2MLocalCoordinatorEntity coordinator : entityContext.findAll(Z2MLocalCoordinatorEntity.class)) {
            list.addAll(coordinator.getService().getDeviceHandlers().values().stream()
                                   .map(Z2MDeviceService::getDeviceEntity)
                                   .collect(Collectors.toList()));
        }
        return list;
    }

    @Override
    public Z2MDeviceEntity getByEntityID(String entityID) {
        for (Z2MLocalCoordinatorEntity coordinator : entityContext.findAll(Z2MLocalCoordinatorEntity.class)) {
            for (Z2MDeviceService deviceService :
                coordinator.getService().getDeviceHandlers().values()) {
                if (deviceService.getDeviceEntity().getEntityID().equals(entityID)) {
                    return deviceService.getDeviceEntity();
                }
            }
        }
        return null;
    }

    @Override
    public Z2MDeviceEntity save(Z2MDeviceEntity entity) {
        // ignore
        return entity;
    }

    @Override
    public Z2MDeviceEntity getByEntityIDWithFetchLazy(String entityID, boolean ignoreNotUILazy) {
        return getByEntityID(entityID);
    }
}
