package org.touchhome.bundle.cloud.netty;

import lombok.RequiredArgsConstructor;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.UserEntity;
import org.touchhome.bundle.api.ui.builder.BellNotificationBuilder;
import org.touchhome.bundle.api.util.NotificationLevel;
import org.touchhome.bundle.cloud.CloudProvider;
import org.touchhome.bundle.cloud.netty.impl.ServerConnectionStatus;
import org.touchhome.bundle.cloud.netty.setting.CloudServerConnectionMessageSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerConnectionStatusSetting;
import org.touchhome.bundle.cloud.netty.setting.CloudServerUrlSetting;

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
    public void assembleBellNotifications(BellNotificationBuilder bellNotificationBuilder) {
        UserEntity user = entityContext.getUser(false);
        if (user != null && user.getKeystore() == null) {
            bellNotificationBuilder.danger("keystore", "Keystore", "Keystore not found");
        }
        ServerConnectionStatus serverConnectionStatus = entityContext.setting().getValue(CloudServerConnectionStatusSetting.class);
        bellNotificationBuilder.notification(serverConnectionStatus == ServerConnectionStatus.CONNECTED ? NotificationLevel.info : NotificationLevel.warning,
                "cloud-status", "Cloud status", entityContext.setting().getValue(CloudServerConnectionMessageSetting.class));
    }
}
