package org.touchhome.bundle.serial;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.BundleEntrypoint;

@Log4j2
@Component
@RequiredArgsConstructor
public class SerialEntrypoint implements BundleEntrypoint {

    private final SerialPortConsolePlugin serialPortConsolePlugin;

    public void init() {
        serialPortConsolePlugin.init();
    }

    @Override
    public String getBundleId() {
        return "serial";
    }

    @Override
    public int order() {
        return 2000;
    }
}
