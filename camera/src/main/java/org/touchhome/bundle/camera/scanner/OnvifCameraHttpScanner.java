package org.touchhome.bundle.camera.scanner;

import de.onvif.soap.OnvifDeviceState;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.service.scan.BaseItemsDiscovery;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultPasswordAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultUserAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpMaxPingTimeoutSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifPortsSetting;

import javax.ws.rs.NotAuthorizedException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class OnvifCameraHttpScanner implements VideoStreamScanner {

    private static final int THREAD_COUNT = 8;
    private final EntityContext entityContext;
    private Map<String, OnvifCameraEntity> existsCamera;
    private BaseItemsDiscovery.DeviceScannerResult result;
    private String headerConfirmButtonKey;

    @Override
    public String getName() {
        return "scan-onvif-http-stream";
    }

    @SneakyThrows
    @Override
    public synchronized BaseItemsDiscovery.DeviceScannerResult scan(EntityContext entityContext, ProgressBar progressBar, String headerConfirmButtonKey) {
        this.headerConfirmButtonKey = headerConfirmButtonKey;
        this.result = new BaseItemsDiscovery.DeviceScannerResult();
        this.existsCamera = entityContext.findAll(OnvifCameraEntity.class)
                .stream().collect(Collectors.toMap(OnvifCameraEntity::getIeeeAddress, Function.identity()));

        String user = entityContext.setting().getValue(ScanOnvifHttpDefaultUserAuthSetting.class);
        String password = entityContext.setting().getValue(ScanOnvifHttpDefaultPasswordAuthSetting.class);
        NetworkHardwareRepository networkHardwareRepository = entityContext.getBean(NetworkHardwareRepository.class);

        Set<Integer> ports = entityContext.setting().getValue(ScanOnvifPortsSetting.class);
        int pingTimeout = entityContext.setting().getValue(ScanOnvifHttpMaxPingTimeoutSetting.class);

        Map<String, Callable<Integer>> tasks = networkHardwareRepository.buildPingIpAddressTasks(log, ports, pingTimeout, (ipAddress, port) -> {
            String host = ipAddress + ":" + port;
            log.info("Onvif ip alive: <{}>. Fetching camera capabilities", host);
            OnvifDeviceState onvifDeviceState = new OnvifDeviceState(ipAddress, port, user, password);
            try {
                onvifDeviceState.checkForErrors();
                foundDeviceServices(onvifDeviceState, false);
            } catch (NotAuthorizedException naex) {
                log.warn("Onvif camera <{}> got fault response: <{}>", host, naex.getMessage());
                foundDeviceServices(onvifDeviceState, true);
            }
        });
        List<Integer> availableOnvifCameras = entityContext.bgp().runInBatchAndGet("scan-onvif-http-batch-result",
                pingTimeout * tasks.size() / 1000, THREAD_COUNT, tasks,
                completedTaskCount -> progressBar.progress(100 / (float) tasks.size() * completedTaskCount,
                        "Onvif http stream done " + completedTaskCount + "/" + tasks.size() + " tasks"));

        log.info("Found {} onvif cameras", availableOnvifCameras.size());

        return result;
    }

    private void foundDeviceServices(OnvifDeviceState onvifDeviceState, boolean requireAuth) {
        log.info("Scan found onvif camera: <{}>", onvifDeviceState.getHOST_IP());
        OnvifCameraEntity existedCamera = existsCamera.get(onvifDeviceState.getIEEEAddress());
        if (existedCamera != null) {
            existedCamera.tryUpdateData(entityContext, onvifDeviceState.getIp(), onvifDeviceState.getPort(), onvifDeviceState.getDevices().getName());
            result.getExistedCount().incrementAndGet();
            return;
        }
        result.getNewCount().incrementAndGet();

        handleDevice(headerConfirmButtonKey,
                "onvif-http-" + onvifDeviceState.getHOST_IP(),
                onvifDeviceState.getHOST_IP(), entityContext,
                messages -> {
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.ADDRESS", "ADDRESS", onvifDeviceState.getHOST_IP()));
                    if (requireAuth) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.REQUIRE_AUTH"));
                    }
                    String name = onvifDeviceState.getDevices().getName();
                    if (name != null) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.NAME", "NAME", name));
                    }
                    String model = onvifDeviceState.getDevices().getDeviceInformation().getModel();
                    if (model != null) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.MODEL", "MODEL", model));
                    }
                },
                () -> {
                    log.info("Saving onvif camera with host: <{}>", onvifDeviceState.getHOST_IP());
                    OnvifCameraEntity entity = new OnvifCameraEntity()
                            .setIp(onvifDeviceState.getIp())
                            .setOnvifPort(onvifDeviceState.getPort())
                            .setName(onvifDeviceState.getDevices().getName())
                            .setUser(onvifDeviceState.getUsername())
                            .setPassword(onvifDeviceState.getPassword())
                            .setIeeeAddress(onvifDeviceState.getIEEEAddress());
                    entityContext.save(entity);
                });
    }
}
