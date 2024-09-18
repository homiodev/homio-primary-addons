package org.homio.addon.z2m;

import com.fazecast.jSerialComm.SerialPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.z2m.model.Z2MDeviceEntity;
import org.homio.addon.z2m.model.Z2MLocalCoordinatorEntity;
import org.homio.addon.z2m.setting.ZigBeeEntityCompactModeSetting;
import org.homio.api.AddonEntrypoint;
import org.homio.api.Context;
import org.homio.api.entity.BaseEntity;
import org.homio.api.entity.zigbee.ZigBeeBaseCoordinatorEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Available fired events:
 * <ul>
 * <li>'zigbee-' + ieeeAddress === Status</li>
 * <li>'zigbee-' + ieeeAddress + '-' + propertyName === Status</li>
 * <li>'zigbee_coordinator-' + entityID === Status</li>
 * </ul>
 */
@SuppressWarnings("rawtypes")
@Log4j2
@Component
@RequiredArgsConstructor
public class Z2MEntrypoint implements AddonEntrypoint {

    private final Context context;

    @Override
    public void init() {
        context.ui().console().registerPluginName("zigbee");
        context.setting().listenValue(ZigBeeEntityCompactModeSetting.class, "zigbee-compact-mode",
                (value) -> context.ui().updateItems(Z2MDeviceEntity.class));

        context.event().addPortChangeStatusListener("zigbee-ports",
                port -> {
                    Map<String, SerialPort> ports = getPorts();
                    testCoordinators(context.db().findAll(Z2MLocalCoordinatorEntity.class), ports, coordinator ->
                            coordinator.getService().forceRestartCoordinator());
                });
    }

    private <T extends BaseEntity & ZigBeeBaseCoordinatorEntity> void testCoordinators(List<T> entities, Map<String, SerialPort> ports,
                                                                                       Consumer<T> reInitializeCoordinatorHandler) {
        for (T coordinator : entities) {
            if (StringUtils.isNotEmpty(coordinator.getPort()) && coordinator.isStart() && coordinator.getStatus().isOffline()) {
                testCoordinator(ports, reInitializeCoordinatorHandler, coordinator);
            }
        }
    }

    private <T extends BaseEntity & ZigBeeBaseCoordinatorEntity> void testCoordinator(Map<String, SerialPort> ports, Consumer<T> reInitializeCoordinatorHandler,
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
                    context.db().save((T) coordinator.setSerialPort(serialPort));
                }
            }
        }
    }

    private Map<String, SerialPort> getPorts() {
        return Stream.of(SerialPort.getCommPorts()).collect(Collectors.toMap(SerialPort::getSystemPortName, p -> p));
    }
}
