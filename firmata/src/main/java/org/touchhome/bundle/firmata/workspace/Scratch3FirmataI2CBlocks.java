package org.touchhome.bundle.firmata.workspace;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.firmata4j.I2CDevice;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.scratch.BlockType;
import org.touchhome.bundle.api.scratch.MenuBlock;
import org.touchhome.bundle.api.scratch.Scratch3Block;
import org.touchhome.bundle.api.scratch.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.firmata.FirmataBundleEntryPoint;

@Log4j2
@Getter
//@Component
/**
 * For now it's too much work to implement i2c devices
 */
public class Scratch3FirmataI2CBlocks extends Scratch3FirmataBaseBlock {
    private final MenuBlock.StaticMenuBlock<BME280ValueMenu> bme280ValueMenu;
    private final Scratch3Block getBME280Value;

    public Scratch3FirmataI2CBlocks(EntityContext entityContext,
                                    FirmataBundleEntryPoint firmataBundleEntryPoint,
                                    BroadcastLockManager broadcastLockManager) {
        super("#E0D225", entityContext, firmataBundleEntryPoint, broadcastLockManager, "ic");

        this.bme280ValueMenu = MenuBlock.ofStatic("bme280ValueMenu", BME280ValueMenu.class, BME280ValueMenu.Temp);

        this.getBME280Value = of(Scratch3Block.ofEvaluate(9, "BME280", BlockType.reporter,
                "BME280 [TYPE] of [FIRMATA]", this::getBME280ValueEvaluate));
        this.getBME280Value.addArgument("TYPE", this.bme280ValueMenu);

        postConstruct();
    }

    private Object getBME280ValueEvaluate(WorkspaceBlock workspaceBlock) {
        return execute(workspaceBlock, false, entity -> {
            BME280ValueMenu type = workspaceBlock.getMenuValue("TYPE", this.bme280ValueMenu);
            I2CDevice i2CDevice = entity.getDevice().getIoDevice().getI2CDevice((byte) 0x77);


            return entity.getDevice().getIoDevice().getProtocol();
        });
    }

    private enum BME280ValueMenu {
        Temp, Pressure, Humidity
    }
}
