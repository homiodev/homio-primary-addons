package org.homio.bundle.z2m;

import com.fazecast.jSerialComm.SerialPort;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.bundle.api.BundleEntrypoint;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.entity.zigbee.ZigBeeBaseCoordinatorEntity;
import org.homio.bundle.z2m.model.Z2MDeviceEntity;
import org.homio.bundle.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.bundle.z2m.setting.ZigBeeEntityCompactModeSetting;
import org.springframework.stereotype.Component;

/**
 * Available fired events:
 * <ul>
 * <li>'zigbee-' + ieeeAddress === Status</li>
 * <li>'zigbee-' + ieeeAddress + '-' + propertyName === Status</li>
 * <li>'zigbee_coordinator-' + entityID === Status</li>
 * </ul>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class Z2MEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;
    public static final String Z2M_RESOURCE = "ROLE_Z2M";

    @Override
    public void init() {
        entityContext.registerResource(Z2M_RESOURCE);
        entityContext.ui().registerConsolePluginName("zigbee", Z2M_RESOURCE);
        entityContext.setting().listenValue(ZigBeeEntityCompactModeSetting.class, "zigbee-compact-mode",
            (value) -> entityContext.ui().updateItems(Z2MDeviceEntity.class));
        entityContext.var().createGroup("z2m", "ZigBee2MQTT", true, "fab fa-laravel", "#ED3A3A");

        entityContext.event().addPortChangeStatusListener("zigbee-ports",
            port -> {
                Map<String, SerialPort> ports = getPorts();
                testCoordinators(entityContext.findAll(Z2MLocalCoordinatorEntity.class), ports, coordinator ->
                    coordinator.getService().restartCoordinator());
            });
    }

    @Override
    public int order() {
        return 600;
    }

    private <T extends ZigBeeBaseCoordinatorEntity> void testCoordinators(List<T> entities, Map<String, SerialPort> ports,
        Consumer<T> reInitializeCoordinatorHandler) {
        for (T coordinator : entities) {
            if (StringUtils.isNotEmpty(coordinator.getPort()) && coordinator.isStart() && coordinator.getStatus().isOffline()) {
                testCoordinator(ports, reInitializeCoordinatorHandler, coordinator);
            }
        }
    }

    private <T extends ZigBeeBaseCoordinatorEntity> void testCoordinator(Map<String, SerialPort> ports, Consumer<T> reInitializeCoordinatorHandler,
        T coordinator) {
        if (ports.containsKey(coordinator.getPort())) {
            // try re-initialize coordinator
            reInitializeCoordinatorHandler.accept(coordinator);
        } else {
            // test maybe port had been changed
            for (SerialPort serialPort : ports.values()) {
                if (Objects.equals(serialPort.getDescriptivePortName(), coordinator.getPortD())) {
                    log.info("[{}]: Coordinator port changed from {} -> {}", coordinator.getEntityID(), coordinator.getPort(),
                        serialPort.getSystemPortName());
                    entityContext.save(coordinator.setSerialPort(serialPort));
                }
            }
        }
    }

    private Map<String, SerialPort> getPorts() {
        return Stream.of(SerialPort.getCommPorts()).collect(Collectors.toMap(SerialPort::getSystemPortName, p -> p));
    }
}
