package org.touchhome.bundle.camera.entity.storage;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.storage.VideoBaseStorageService;
import org.touchhome.bundle.api.ui.UISidebarChildren;

@Log4j2
@Setter
@Getter
//@Entity
@UISidebarChildren(icon = "rest/bundle/image/camera/memory-card.png", color = "#AACC00")
public class IpCameraSDCardStorageService extends VideoBaseStorageService<IpCameraSDCardStorageService> {

  public static final String PREFIX = "ipcsd_";

  @Override
  public String getEntityPrefix() {
    return PREFIX;
  }

  @SneakyThrows
  @Override
  public void startRecord(String id, String output, String profile, DeviceBaseEntity deviceEntity) {
    throw new NotImplementedException();
  }

  @Override
  public void stopRecord(String id, String output, DeviceBaseEntity cameraEntity) {
    throw new NotImplementedException();
  }
}