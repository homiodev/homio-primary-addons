package org.touchhome.bundle.cloud.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.hardware.other.MachineHardwareRepository;
import org.touchhome.bundle.api.ui.builder.BellNotificationBuilder;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;

import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class SshCloudProvider implements CloudProvider {

    private final MachineHardwareRepository machineHardwareRepository;

    @Override
    public String getStatus() {
        int serviceStatus = machineHardwareRepository.getServiceStatus("touchhome-tunnel");
        return serviceStatus == 0 ? ServerConnectionStatus.CONNECTED.name() : ServerConnectionStatus.DISCONNECTED_WIDTH_ERRORS.name();
    }

    @Override
    public void assembleBellNotifications(BellNotificationBuilder bellNotificationBuilder) {
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome"))) {
            bellNotificationBuilder.danger("private-key", "Cloud", "Private Key not found");
        }
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome.pub"))) {
            bellNotificationBuilder.danger("public-key", "Cloud", "Public Key not found");
        }
        int serviceStatus = machineHardwareRepository.getServiceStatus("touchhome-tunnel");
        if (serviceStatus == 0) {
            bellNotificationBuilder.info("cloud-status", "Cloud", "Connected");
        } else {
            bellNotificationBuilder.warn("cloud-status", "Cloud", "Connection status not active " + serviceStatus);
        }
    }
}
