package org.homio.bundle.z2m;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.console.ConsolePluginFrame;

@Getter
@RequiredArgsConstructor
public class ZigBee2MQTTFrontendConsolePlugin implements ConsolePluginFrame {

    private final EntityContext entityContext;
    private final FrameConfiguration value;

    @Override
    public int order() {
        return 500;
    }

    @Override
    public String getParentTab() {
        return "zigbee";
    }
}
