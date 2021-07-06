package org.touchhome.bundle.camera.scanner;

import de.onvif.soap.OnvifDeviceState;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.Lang;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.service.scan.BaseItemsDiscovery;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.CameraBrandHandlerDescription;
import org.touchhome.bundle.camera.onvif.OnvifDiscovery;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultPasswordAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpDefaultUserAuthSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifHttpMaxPingTimeoutSetting;
import org.touchhome.bundle.camera.setting.onvif.ScanOnvifPortsSetting;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public synchronized BaseItemsDiscovery.DeviceScannerResult scan(EntityContext entityContext,
                                                                    ProgressBar progressBar,
                                                                    String headerConfirmButtonKey) {
        return executeScan(entityContext, progressBar, headerConfirmButtonKey, false);
    }

    public BaseItemsDiscovery.DeviceScannerResult executeScan(EntityContext entityContext, ProgressBar progressBar,
                                                              String headerConfirmButtonKey, boolean rediscoverIpAddresses) {
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
            OnvifDeviceState onvifDeviceState = new OnvifDeviceState(ipAddress, port, 0, user, password);
            try {
                onvifDeviceState.checkForErrors();
                foundDeviceServices(onvifDeviceState, false, rediscoverIpAddresses);
            } catch (BadCredentialsException bex) {
                log.warn("Onvif camera <{}> got fault auth response: <{}>", host, bex.getMessage());
                foundDeviceServices(onvifDeviceState, true, rediscoverIpAddresses);
            } catch (Exception ex) {
                log.error("Onvif camera <{}> got fault response: <{}>", host, ex.getMessage());
            }
        });
        List<Integer> availableOnvifCameras = entityContext.bgp().runInBatchAndGet("scan-onvif-http-batch-result",
                2 * pingTimeout * tasks.size() / 1000, THREAD_COUNT, tasks,
                completedTaskCount -> {
                    if (progressBar != null) {
                        progressBar.progress(100 / (float) tasks.size() * completedTaskCount,
                                "Onvif http stream done " + completedTaskCount + "/" + tasks.size() + " tasks");
                    }
                });
        log.info("Found {} onvif cameras", availableOnvifCameras.stream().filter(Objects::nonNull).count());

        return result;
    }

    private void foundDeviceServices(OnvifDeviceState onvifDeviceState, boolean requireAuth, boolean rediscoverIpAddresses) {
        log.info("Scan found onvif camera: <{}>", onvifDeviceState.getHOST_IP());

        OnvifCameraEntity existedCamera = existsCamera.get(onvifDeviceState.getIEEEAddress());
        if (existedCamera != null) {
            try {
                existedCamera.tryUpdateData(entityContext, onvifDeviceState.getIp(), onvifDeviceState.getOnvifPort(),
                        onvifDeviceState.getInitialDevices().getName());
            } catch (Exception ex) {
                log.error("Error while trying update camera: <{}>", TouchHomeUtils.getErrorMessage(ex));
            }
            result.getExistedCount().incrementAndGet();
            return;
        } else if (!rediscoverIpAddresses) {
            return;
        }
        result.getNewCount().incrementAndGet();

        CameraBrandHandlerDescription brand = OnvifDiscovery.getBrandFromLoginPage(onvifDeviceState.getIp());
        handleDevice(headerConfirmButtonKey,
                "onvif-http-" + onvifDeviceState.getHOST_IP(),
                onvifDeviceState.getHOST_IP(), entityContext,
                messages -> {
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.ADDRESS", "ADDRESS", onvifDeviceState.getHOST_IP()));
                    if (requireAuth) {
                        messages.add(Lang.getServerMessage("VIDEO_STREAM.REQUIRE_AUTH"));
                    }
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.NAME", "NAME", fetchCameraName(onvifDeviceState)));
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.MODEL", "MODEL", fetchCameraModel(onvifDeviceState)));
                    messages.add(Lang.getServerMessage("VIDEO_STREAM.BRAND", "BRAND", brand.getName()));
                },
                () -> {
                    log.info("Saving onvif camera with host: <{}>", onvifDeviceState.getHOST_IP());
                    OnvifCameraEntity entity = new OnvifCameraEntity()
                            .setIp(onvifDeviceState.getIp())
                            .setOnvifPort(onvifDeviceState.getOnvifPort())
                            .setName(onvifDeviceState.getInitialDevices().getName())
                            .setUser(onvifDeviceState.getUsername())
                            .setCameraType(brand.getID())
                            .setPassword(onvifDeviceState.getPassword())
                            .setIeeeAddress(onvifDeviceState.getIEEEAddress());
                    entityContext.save(entity);
                });
    }

    private String fetchCameraName(OnvifDeviceState onvifDeviceState) {
        try {
            return onvifDeviceState.getInitialDevices().getName();
        } catch (BadCredentialsException ex) {
            return "Require auth to fetch name";
        } catch (Exception ex) {
            return "Unknown name: " + TouchHomeUtils.getErrorMessage(ex);
        }
    }

    private String fetchCameraModel(OnvifDeviceState onvifDeviceState) {
        try {
            return onvifDeviceState.getInitialDevices().getDeviceInformation().getModel();
        } catch (BadCredentialsException ex) {
            return "Require auth to fetch model";
        } catch (Exception ex) {
            return "Unknown model: " + TouchHomeUtils.getErrorMessage(ex);
        }
    }
}
