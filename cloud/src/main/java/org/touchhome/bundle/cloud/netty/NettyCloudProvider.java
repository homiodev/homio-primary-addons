package org.touchhome.bundle.cloud.netty;

import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.ui.BellNotification;
import org.touchhome.bundle.api.util.NotificationLevel;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;
import org.touchhome.bundle.cloud.netty.setting.CloudServerConnectionMessageSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerConnectionStatusSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerUrlSetting;

import java.util.HashSet;
import java.util.Set;

//@Component
@RequiredArgsConstructor
public class NettyCloudProvider implements CloudProvider {

    private final EntityContext entityContext;

    @Override
    public String getStatus() {
        String error = entityContext.setting().getValue(CloudServerConnectionMessageSetting.class);
        ServerConnectionStatus status = entityContext.setting().getValue(CloudServerConnectionStatusSetting.class);
        return (status == null ? "Unknown" : status.name()) + ". Errors: " + error + ". Url: " + entityContext.setting().getValue(CloudServerUrlSetting.class);
    }

    @Override
    public Set<BellNotification> getBellNotifications() {
        UserEntity user = entityContext.getUser(false);
        Set<BellNotification> notifications = new HashSet<>();
        if (user != null && user.getKeystore() == null) {
            notifications.add(BellNotification.danger("keystore").setTitle("Keystore").setValue("Keystore not found"));
        }
        ServerConnectionStatus serverConnectionStatus = entityContext.setting().getValue(CloudServerConnectionStatusSetting.class);
        notifications.add(new BellNotification("cloud-status")
                .setTitle("Cloud status")
                .setValue(entityContext.setting().getValue(CloudServerConnectionMessageSetting.class))
                .setLevel(serverConnectionStatus == ServerConnectionStatus.CONNECTED ? NotificationLevel.info : NotificationLevel.warning));

        return notifications;
    }
}
