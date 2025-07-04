package org.homio.addon.camera.entity.storage;

import org.homio.api.Context;
import org.homio.api.entity.device.DeviceBaseEntity;
import org.homio.api.ui.route.UIRouteStorage;

@UIRouteStorage
public abstract class VideoBaseStorageService<T extends VideoBaseStorageService>
    extends DeviceBaseEntity {

  public abstract void startRecord(
      String id, String output, String profile, DeviceBaseEntity videoEntity, Context context);

  public abstract void stopRecord(String id, String output, DeviceBaseEntity videoEntity);
}
