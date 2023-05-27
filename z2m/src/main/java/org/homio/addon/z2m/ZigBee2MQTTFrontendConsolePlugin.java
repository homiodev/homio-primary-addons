package org.homio.addon.z2m;

import static org.homio.addon.z2m.Z2MEntrypoint.Z2M_RESOURCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.homio.api.EntityContext;
import org.homio.api.console.ConsolePluginFrame;

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

    @Override
    public boolean isEnabled() {
        return entityContext.accessEnabled(Z2M_RESOURCE);
    }
}
