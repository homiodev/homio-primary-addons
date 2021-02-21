package org.touchhome.bundle.camera.scanner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.service.scan.BaseItemsDiscovery;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraActions;
import org.touchhome.bundle.camera.onvif.OnvifConnection;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultPasswordAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultUserAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpMaxPingTimeoutSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifPortsSetting;

import java.util.HashMap;
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

    private Map<String, OnvifCameraCapability> hostToFetchData;

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
        this.hostToFetchData = new HashMap<>();

        String user = entityContext.setting().getValue(ScanOnvifHttpDefaultUserAuthSetting.class);
        String password = entityContext.setting().getValue(ScanOnvifHttpDefaultPasswordAuthSetting.class);
        NetworkHardwareRepository networkHardwareRepository = entityContext.getBean(NetworkHardwareRepository.class);

        Set<Integer> ports = entityContext.setting().getValue(ScanOnvifPortsSetting.class);
        int pingTimeout = entityContext.setting().getValue(ScanOnvifHttpMaxPingTimeoutSetting.class);

        Map<String, Callable<Integer>> tasks = networkHardwareRepository.buildPingIpAddressTasks(log, ports, pingTimeout, (ipAddress, port) -> {
            String host = ipAddress + ":" + port;
            this.hostToFetchData.put(host, new OnvifCameraCapability(ipAddress, port, user, password));
            log.info("Onvif ip alive: <{}>. Fetching camera capabilities", host);
            OnvifCameraActions onvifCameraActions = new OnvifCameraActions() {
                @Override
                public void onDeviceInformationReceived(OnvifConnection.GetDeviceInformationResponse deviceInformation) {
                    hostToFetchData.get(host).deviceInformation = deviceInformation;
                    foundDeviceServices(host, false);
                }

                @Override
                public void onCameraNameReceived(String name) {
                    hostToFetchData.get(host).name = StringUtils.defaultString(name, "no-name");
                    foundDeviceServices(host, false);
                }

                @Override
                public void cameraUnreachable(String message) {
                    log.warn("Onvif camera <{}> unreachable: <{}>", host, message);
                    hostToFetchData.remove(host);
                }

                @Override
                public void cameraFaultResponse(int code, String reason) {
                    if (code == 400) {
                        log.warn("Onvif camera <{}> got fault response: <{} - {}>", host, code, reason);
                        foundDeviceServices(host, true);
                    }
                }
            };
            OnvifConnection onvifConnection = new OnvifConnection(onvifCameraActions, ipAddress, port,
                    user, password);
            onvifConnection.sendOnvifDeviceServiceRequest(OnvifConnection.RequestType.GetDeviceInformation);
            onvifConnection.sendOnvifDeviceServiceRequest(OnvifConnection.RequestType.GetScopes);
        });
        List<Integer> availableOnvifCameras = entityContext.bgp().runInBatchAndGet("scan-onvif-http-batch-result",
                pingTimeout * tasks.size() / 1000, THREAD_COUNT, tasks,
                completedTaskCount -> progressBar.progress(100 / (float) tasks.size() * completedTaskCount,
                        "Onvif http stream done " + completedTaskCount + "/" + tasks.size() + " tasks"));

        log.info("Found {} onvif cameras", availableOnvifCameras.size());

        return result;
    }

    private void foundDeviceServices(String host, boolean requireAuth) {
        OnvifCameraCapability capability = this.hostToFetchData.get(host);
        OnvifConnection.GetDeviceInformationResponse deviceInformation = capability.deviceInformation;
        if ((deviceInformation == null || capability.name == null) && !requireAuth) {
            return;
        }
        log.info("Scan found onvif camera: <{}>. DeviceInfo: {}", host, deviceInformation);
        if (deviceInformation != null) {
            OnvifCameraEntity existedCamera = existsCamera.get(deviceInformation.getIeeeAddress());
            if (existedCamera != null) {
                existedCamera.tryUpdateData(entityContext, capability.ipAddress, capability.port, capability.name);
                result.getExistedCount().incrementAndGet();
                return;
            }
        }
        result.getNewCount().incrementAndGet();

        handleDevice(headerConfirmButtonKey,
                "onvif-http-" + host,
                host, entityContext,
                messages -> {
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.ADDRESS", "ADDRESS", host));
                    if (requireAuth) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.REQUIRE_AUTH"));
                    }
                    if (capability.name != null) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.NAME", "NAME", capability.name));
                    }
                    if (deviceInformation != null) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.MODEL", "MODEL", deviceInformation.getModel()));
                    }
                },
                () -> {
                    log.info("Saving onvif camera with host: <{}>", host);
                    OnvifCameraEntity entity = new OnvifCameraEntity()
                            .setIp(capability.ipAddress)
                            .setOnvifPort(capability.port)
                            .setName(capability.name)
                            .setUser(capability.user)
                            .setPassword(capability.password);
                    if (deviceInformation != null) {
                        entity.setIeeeAddress(deviceInformation.getIeeeAddress());
                    }
                    entityContext.save(entity);
                });
    }

    @RequiredArgsConstructor
    private static class OnvifCameraCapability {
        private final String ipAddress;
        private final int port;
        private final String user;
        private final String password;
        public String name;
        private OnvifConnection.GetDeviceInformationResponse deviceInformation;
    }
}
