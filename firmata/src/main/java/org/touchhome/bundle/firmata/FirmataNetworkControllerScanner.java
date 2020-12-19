package org.touchhome.bundle.firmata;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.micro.MicroControllerScanner;
import org.touchhome.bundle.api.hardware.network.NetworkHardwareRepository;
import org.touchhome.bundle.api.model.ProgressBar;
import org.touchhome.bundle.api.service.scan.BaseItemsDiscovery;

import java.util.*;
import java.util.concurrent.Callable;

@Log4j2
@Component
@RequiredArgsConstructor
public class FirmataNetworkControllerScanner implements MicroControllerScanner {
    private static final int port = 3132;
    private static final int timeout = 2000;

    private final NetworkHardwareRepository networkHardwareRepository;

    @Override
    public String getName() {
        return "firmata-network";
    }

    @Override
    public BaseItemsDiscovery.DeviceScannerResult scan(EntityContext entityContext, ProgressBar progressBar, String headerConfirmButtonKey) {
        Set<String> existedDevices = new HashSet<>();
        Map<String, Callable<Integer>> tasks = networkHardwareRepository.buildPingIpAddressTasks(log, Collections.singleton(port), timeout, (ipAddress, integer) -> {
            if (!FirmataBundleEntryPoint.foundController(entityContext, null, null, ipAddress, headerConfirmButtonKey)) {
                existedDevices.add(ipAddress);
            }
        });

        BaseItemsDiscovery.DeviceScannerResult result = new BaseItemsDiscovery.DeviceScannerResult();
        List<Integer> availableIpAddresses = entityContext.bgp().runInBatchAndGet("firmata-ip-scan", 5 * 60, 8, tasks,
                completedTaskCount -> progressBar.progress(100 / 256F * completedTaskCount, "Scanned " + completedTaskCount + " ip addresses"));
        log.debug("Found {} devices", availableIpAddresses.size());
        return new BaseItemsDiscovery.DeviceScannerResult(existedDevices.size(), availableIpAddresses.size() - existedDevices.size());
    }
}
