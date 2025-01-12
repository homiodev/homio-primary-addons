package org.homio.addon.camera.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.camera.entity.UsbCameraEntity;
import org.homio.api.Context;
import org.homio.api.service.discovery.ItemDiscoverySupport;
import org.homio.api.util.Lang;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.homio.api.ContextMediaVideo.VideoInputDevice;
import static org.homio.api.entity.HasJsonData.LIST_DELIMITER;

@Log4j2
@Component
@RequiredArgsConstructor
public class UsbCameraScanner implements ItemDiscoverySupport {

  @Override
  public @NotNull String getName() {
    return "scan-usb-camera";
  }

  @Override
  public DeviceScannerResult scan(@NotNull Context context, @NotNull ProgressBar progressBar) {
    DeviceScannerResult result = new DeviceScannerResult();
    List<VideoInputDevice> foundUsbVideoCameraDevices = new ArrayList<>();

    for (String deviceName : context.media().video().getVideoDevices()) {
      foundUsbVideoCameraDevices.add(context.media().video().createVideoInputDevice(deviceName).setName(deviceName));
    }
    Map<String, UsbCameraEntity> existsUsbCamera = context.db().findAll(UsbCameraEntity.class).stream()
      .collect(Collectors.toMap(UsbCameraEntity::getIeeeAddress, Function.identity()));

    // search if new devices not found and send confirm to ui
    for (VideoInputDevice foundUsbVideoDevice : foundUsbVideoCameraDevices) {
      if (!existsUsbCamera.containsKey(foundUsbVideoDevice.getName())) {
        result.getNewCount().incrementAndGet();
        String name = Lang.getServerMessage("NEW_DEVICE.USB_CAMERA") + foundUsbVideoDevice.getName();
        handleDevice(foundUsbVideoDevice.getName(), name, context,
          messages -> {
            messages.add(Lang.getServerMessage("VIDEO_STREAM.NEW_DEVICE_QUESTION"));
            messages.add(Lang.getServerMessage("NEW_DEVICE.NAME", foundUsbVideoDevice.getName()));
          },
          () -> {
            log.info("Confirm save usb camera: <{}>", foundUsbVideoDevice.getName());
            UsbCameraEntity entity = new UsbCameraEntity();
            entity.setName(foundUsbVideoDevice.getName());
            entity.setStreamResolutions(String.join(LIST_DELIMITER, foundUsbVideoDevice.getResolutionSet()));
            entity.setIeeeAddress(defaultIfEmpty(foundUsbVideoDevice.getName(), String.valueOf(System.currentTimeMillis())));
            context.db().save(entity);
          });
      } else {
        result.getExistedCount().incrementAndGet();
      }
    }

    return result;
  }
}
