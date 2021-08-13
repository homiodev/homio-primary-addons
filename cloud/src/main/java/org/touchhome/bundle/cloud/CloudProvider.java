package org.touchhome.bundle.cloud;

import org.touchhome.bundle.api.ui.builder.BellNotificationBuilder;

public interface CloudProvider {
    String getStatus();

    void assembleBellNotifications(BellNotificationBuilder bellNotificationBuilder);
}
