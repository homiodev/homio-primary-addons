package org.touchhome.bundle.firmata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.manager.En;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.util.FlowMap;
import org.touchhome.bundle.firmata.model.FirmataBaseEntity;
import org.touchhome.bundle.firmata.model.FirmataNetworkEntity;
import org.touchhome.bundle.firmata.provider.FirmataCommandPlugins;
import org.touchhome.bundle.firmata.repository.FirmataDeviceRepository;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.touchhome.bundle.firmata.provider.command.FirmataCommand.SYSEX_PING;

@Log4j2
@Component
@RequiredArgsConstructor
public class FirmataBundleEntrypoint implements BundleEntrypoint {

    @Getter
    private static final Map<String, UdpPayload> udpFoundDevices = new HashMap<>();

    private final EntityContext entityContext;

    @Getter
    private final FirmataDeviceRepository firmataDeviceRepository;

    @Getter
    private final FirmataCommandPlugins firmataCommandPlugins;

    public void init() {
        restartFirmataProviders();
        this.entityContext.addEntityUpdateListener(FirmataBaseEntity.class, FirmataBaseEntity::restartCommunicator);

        this.entityContext.listenUdp("listen-firmata-udp", null, 8266, (datagramPacket, payload) -> {
            if (payload.startsWith("th:")) {
                String[] parts = payload.split(":");
                if (parts.length == 3) {
                    this.foundController(parts[1].trim(), parts[2].trim(), datagramPacket.getAddress());
                    return;
                }
            }
            log.warn("Got udp notification on port 8266 with unknown payload: <{}>", payload);
        });

        // ping firmata device if live status is online
        this.entityContext.schedule("firmata-device-ping", 3, TimeUnit.MINUTES, () -> {
            for (FirmataBaseEntity firmataBaseEntity : entityContext.findAll(FirmataBaseEntity.class)) {
                if (firmataBaseEntity.getJoined() == Status.ONLINE) {
                    log.debug("Ping firmata device: <{}>", firmataBaseEntity.getTitle());
                    firmataBaseEntity.getDevice().sendMessage(SYSEX_PING);
                }
            }
        }, true);
    }

    // this method fires only from devices that support internet access
    private void foundController(String board, String deviceID, InetAddress address) {
        String hostAddress = address.getHostAddress();
        // check if we already have firmata device with deviceID
        FirmataBaseEntity<?> device = entityContext.findAll(FirmataBaseEntity.class).stream()
                .filter(d -> Objects.equals(d.getIeeeAddress(), deviceID))
                .findAny().orElse(null);

        if (device != null) {
            if (device instanceof FirmataNetworkEntity) {
                FirmataNetworkEntity ae = (FirmataNetworkEntity) device;
                if (!hostAddress.equals(ae.getIp())) {
                    entityContext.sendWarningMessage("FIRMATA.EVENT.CHANGED_IP",
                            FlowMap.of("DEVICE", device.getTitle(), "OLD", ae.getIp(), "NEW", hostAddress));
                    // update device ip address and try restart it
                    entityContext.save(ae.setIp(hostAddress)).restartCommunicator();
                }
            } else {
                this.entityContext.sendWarningMessage("FIRMATA.EVENT.FIRMATA_WRONG_DEVICE_TYPE", FlowMap.of("ID",
                        deviceID, "NAME", device.getTitle()));
            }
        } else {
            entityContext.sendConfirmation("Confirm-Firmata-" + deviceID, "TITLE.NEW_DEVICE", () -> {
                        // save device and try restart it
                        entityContext.save(new FirmataNetworkEntity().setIp(hostAddress)).restartCommunicator();
                    }, En.getServerMessage("FIRMATA.NEW_DEVICE_QUESTION"),
                    En.getServerMessage("FIRMATA.NEW_DEVICE_BOARD", "BOARD", board),
                    En.getServerMessage("FIRMATA.NEW_DEVICE_ID", "DEVICE_ID", deviceID),
                    En.getServerMessage("FIRMATA.NEW_DEVICE_ADDRESS", "ADDRESS", hostAddress));
            this.entityContext.sendInfoMessage("FIRMATA.EVENT.FOUND_UDP_FIRMATA_DEVICE",
                    FlowMap.of("ID", deviceID, "IP", hostAddress, "BOARD", board));
            udpFoundDevices.put(hostAddress, new UdpPayload(hostAddress, Short.parseShort(deviceID), board));
        }
    }

    private void restartFirmataProviders() {
        this.entityContext.findAll(FirmataBaseEntity.class).forEach(FirmataBaseEntity::restartCommunicator);
    }

    @Override
    public String getBundleId() {
        return "firmata";
    }

    @Override
    public int order() {
        return 500;
    }

    @Getter
    @AllArgsConstructor
    public static class UdpPayload {
        private final String address;
        private final Short deviceID;
        private final String board;

        @Override
        public String toString() {
            return String.format("IP: %s. [Device ID: %s / Board: %s]", address, deviceID, board);
        }
    }
}
