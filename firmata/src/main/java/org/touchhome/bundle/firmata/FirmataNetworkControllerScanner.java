package org.touchhome.bundle.firmata;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.hardware.wifi.WirelessHardwareRepository;
import org.touchhome.bundle.api.model.micro.MicroControllerScanner;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            ForkJoinPool forkJoinPool = new ForkJoinPool(8);
            List<Integer> availableIpAddresses = forkJoinPool.submit(() ->
                    IntStream.range(0, 255).parallel().filter(i -> {
                        log.info("Check for ip: {}", scanIp + i);
                        return wirelessHardwareRepository.pingAddress(scanIp + i, port, timeout);
                    }).boxed().collect(Collectors.toList())
            ).get();
            for (int ipSuffix : availableIpAddresses) {
                FirmataBundleEntrypoint.foundController(entityContext, null, null, scanIp + ipSuffix);
            }
            log.info("Done scan for ip addresses {} and port: {}. Found {} devices",
                    gatewayIpAddress, port, availableIpAddresses.size());
            return availableIpAddresses.size();
        }
        return 0;
    }
}
