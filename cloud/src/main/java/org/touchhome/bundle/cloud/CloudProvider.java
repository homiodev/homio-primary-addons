package org.touchhome.bundle.cloud;

import org.touchhome.bundle.api.model.NotificationModel;

import java.util.Set;

public interface CloudProvider {
    String getStatus();

    Set<NotificationModel> getNotifications();
}
