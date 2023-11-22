package org.homio.addon.z2m;

import static org.homio.addon.z2m.model.Z2MDeviceEntity.PREFIX;

import lombok.Getter;
import org.homio.api.Context;
import org.homio.api.workspace.scratch.MenuBlock;
import org.homio.api.workspace.scratch.Scratch3BaseDeviceBlocks;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Scratch3ZigBeeBlocks extends Scratch3BaseDeviceBlocks {

    private final @NotNull MenuBlock.ServerMenuBlock temperatureDeviceMenu;
    private final @NotNull MenuBlock.ServerMenuBlock humidityDeviceMenu;

    public Scratch3ZigBeeBlocks(Context context, Z2MEntrypoint z2MEntrypoint) {
        super("#6d4747", context, z2MEntrypoint, PREFIX);

        this.temperatureDeviceMenu = menuServer("temperatureDeviceMenu", DEVICE__BASE_URL + "/temperature", "Device", "-");
        this.humidityDeviceMenu = menuServer("humidityDeviceMenu", DEVICE__BASE_URL + "/humidity", "Device", "-");

        blockReporter(52, "temperature", "temperature [DEVICE]",
            workspaceBlock -> getDeviceEndpoint(workspaceBlock, deviceMenu, "temperature").getLastValue(),
            block -> {
                block.addArgument(DEVICE, this.temperatureDeviceMenu);
                block.overrideColor("#307596");
            });

        blockReporter(53, "humidity", "humidity [DEVICE]",
            workspaceBlock -> getDeviceEndpoint(workspaceBlock, deviceMenu, "humidity").getLastValue(),
            block -> {
                block.addArgument(DEVICE, humidityDeviceMenu);
                block.overrideColor("#3B8774");
            });
    }
}
