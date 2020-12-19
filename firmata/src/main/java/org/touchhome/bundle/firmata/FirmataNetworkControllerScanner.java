package org.touchhome.bundle.firmata;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.micro.MicroControllerScanner;
import org.touchhome.bundle.api.hardware.wifi.WirelessHardwareRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Component
@RequiredArgsConstructor
public class FirmataNetworkControllerScanner implements MicroControllerScanner {
    private static final int port = 3132;
    private static final int timeout = 5000;

    private final EntityContext entityContext;
    private final WirelessHardwareRepository wirelessHardwareRepository;

    @SneakyThrows
    @Override
    public int scan() {
        String gatewayIpAddress = wirelessHardwareRepository.getGatewayIpAddress();
        if (gatewayIpAddress != null) {
            log.info("Starting scan for ip addresses {} and port: {}", gatewayIpAddress, port);
            String scanIp = gatewayIpAddress.substring(0, gatewayIpAddress.lastIndexOf(".") + 1);
            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Integer> availableIpAddresses = new ArrayList<>();
            for (int i = 0; i < 255; i++) {
                int ipSuffix = i;
                executor.submit(() -> {
                    log.info("Check for ip: {}", scanIp + ipSuffix);
                    if (wirelessHardwareRepository.pingAddress(scanIp + ipSuffix, port, timeout)) {
                        availableIpAddresses.add(ipSuffix);
                    }
                });
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            for (int ipSuffix : availableIpAddresses) {
                FirmataBundleEntryPoint.foundController(entityContext, null, null, scanIp + ipSuffix);
            }
            log.info("Done scan for ip addresses {} and port: {}. Found {} devices",
                    gatewayIpAddress, port, availableIpAddresses.size());
            return availableIpAddresses.size();
        }
        return 0;
    }
}
