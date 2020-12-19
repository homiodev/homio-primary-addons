package org.touchhome.bundle.cloud.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.hardware.other.LinuxHardwareRepository;
import org.touchhome.bundle.api.model.NotificationModel;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SshCloudProvider implements CloudProvider {

    private final LinuxHardwareRepository linuxHardwareRepository;

    @Override
    public String getStatus() {
        int serviceStatus = linuxHardwareRepository.getServiceStatus("touchhome-tunnel");
        return serviceStatus == 0 ? ServerConnectionStatus.CONNECTED.name() : ServerConnectionStatus.DISCONNECTED_WIDTH_ERRORS.name();
    }

    @Override
    public Set<NotificationModel> getNotifications() {
        Set<NotificationModel> notifications = new HashSet<>();
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome"))) {
            notifications.add(NotificationModel.danger("private-key").setTitle("Cloud").setValue("Private Key not found"));
        }
        if (!Files.exists(TouchHomeUtils.getSshPath().resolve("id_rsa_touchhome.pub"))) {
            notifications.add(NotificationModel.danger("public-key").setTitle("Cloud").setValue("Public key not found"));
        }
        int serviceStatus = linuxHardwareRepository.getServiceStatus("touchhome-tunnel");
        if (serviceStatus == 0) {
            notifications.add(NotificationModel.info("cloud-status").setTitle("Cloud").setValue("Connected"));
        } else {
            notifications.add(NotificationModel.warn("cloud-status").setTitle("Cloud")
                    .setValue("Connection status not active " + serviceStatus));
        }
        return notifications;
    }
}
