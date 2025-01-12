package org.homio.addon.camera.scanner;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.homio.addon.camera.entity.IpCameraEntity;
import org.homio.addon.camera.onvif.OnvifDiscovery;
import org.homio.api.Context;
import org.homio.api.service.discovery.ItemDiscoverySupport;
import org.homio.api.util.Lang;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class OnvifWsDiscoveryCameraScanner implements ItemDiscoverySupport {

  @Override
  public @NotNull String getName() {
    return "scan-ws-discovery-camera";
  }

  @Override
  public DeviceScannerResult scan(@NotNull Context context, @NotNull ProgressBar progressBar) {
    OnvifDiscovery onvifDiscovery = new OnvifDiscovery(context);
    DeviceScannerResult result = new DeviceScannerResult();
    try {
      Map<String, IpCameraEntity> existsCamera = context.db().findAll(IpCameraEntity.class)
        .stream().collect(Collectors.toMap(IpCameraEntity::getIp, Function.identity()));

      onvifDiscovery.discoverCameras((brand, ipAddress, onvifPort, hardwareID) -> {
        if (!existsCamera.containsKey(ipAddress)) {
          result.getNewCount().incrementAndGet();
          handleDevice("onvif-" + ipAddress,
            "Onvif", context,
            messages -> {
              messages.add(Lang.getServerMessage("VIDEO_STREAM.NEW_DEVICE_QUESTION"));
              messages.add(Lang.getServerMessage("TITLE.ADDRESS", ipAddress + ":" + onvifPort));
              messages.add(Lang.getServerMessage("TITLE.BRAND", brand.getName()));
              if (!StringUtils.isEmpty(hardwareID)) {
                messages.add(Lang.getServerMessage("VIDEO_STREAM.HARDWARE", hardwareID));
              }
            },
            () -> {
              log.info("Confirm save onvif camera with ip address: <{}>", ipAddress);
              context.db().save(new IpCameraEntity()
                .setIp(ipAddress)
                .setOnvifPort(onvifPort)
                .setCameraType(brand.getID()));
            });
        } else {
          result.getExistedCount().incrementAndGet();
        }
      });
    } catch (UnknownHostException | InterruptedException e) {
      log.warn("IpCamera Discovery has an issue discovering the network settings to find cameras with. Try setting up the camera manually.");
    }
    return result;
  }
}
