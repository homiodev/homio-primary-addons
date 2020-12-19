package org.touchhome.bundle.cloud;

import org.touchhome.bundle.api.ui.BellNotification;

import java.util.Set;

public interface CloudProvider {
    String getStatus();

    Set<BellNotification> getBellNotifications();
}
