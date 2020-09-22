package org.touchhome.bundle.raspberry.workspace;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.measure.OnOffType;
import org.touchhome.bundle.api.model.workspace.bool.WorkspaceBooleanEntity;
import org.touchhome.bundle.api.scratch.*;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.raspberry.RaspberryEntrypoint;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;

import java.util.function.Consumer;

@Getter
@Component
public class Scratch3RaspberryBlocks extends Scratch3ExtensionBlocks {

    private final MenuBlock.StaticMenuBlock<OnOffType> onOffMenu;
    private final MenuBlock.StaticMenuBlock<RaspberryGpioPin> allPinMenu;
    private final MenuBlock.StaticMenuBlock pwmPinMenu;
    private final MenuBlock.StaticMenuBlock<PinPullResistance> pullMenu;

    private final MenuBlock.ServerMenuBlock rpiIdMenu;
    private final MenuBlock.ServerMenuBlock ds18b20Menu;

    private final Scratch3Block isGpioInState;
    private final Scratch3Block writePin;
    private final Scratch3Block whenGpioInState;
    private final Scratch3Block set_pull;
    private final Scratch3Block ds18b20Value;
    private final RaspberryGPIOService raspberryGPIOService;
    private final BroadcastLockManager broadcastLockManager;
    private Scratch3Block writePwmPin;

    public Scratch3RaspberryBlocks(RaspberryGPIOService raspberryGPIOService, BroadcastLockManager broadcastLockManager,
                                   EntityContext entityContext, RaspberryEntrypoint raspberryEntrypoint) {
        super("#83be41", entityContext, raspberryEntrypoint);
        this.raspberryGPIOService = raspberryGPIOService;
        this.broadcastLockManager = broadcastLockManager;

        this.allPinMenu = MenuBlock.ofStatic("allPinMenu", RaspberryGpioPin.class, RaspberryGpioPin.PIN3, p ->
                p.getPin().getSupportedPinModes().contains(PinMode.DIGITAL_INPUT));
        this.pwmPinMenu = MenuBlock.ofStatic("pwmPinMenu", RaspberryGpioPin.class, RaspberryGpioPin.PIN12, p -> p.name().equals("PIN12") || p.name().equals("PIN33"));

        this.rpiIdMenu = MenuBlock.ofServer("rpiIdMenu", "rest/item/type/RaspberryDeviceEntity",
                "Select RPI", "-");
        this.onOffMenu = MenuBlock.ofStatic("onOffMenu", OnOffType.class, OnOffType.ON);

        this.pullMenu = MenuBlock.ofStatic("pullMenu", PinPullResistance.class, PinPullResistance.PULL_UP);

        this.ds18b20Menu = MenuBlock.ofServer("ds18b20Menu", "rest/raspberry/DS18B20", "-", "-");

        this.writePin = of(Scratch3Block.ofHandler(0, "set_gpio", BlockType.command, "Set [ONOFF] to pin [PIN] of [RPI]", this::writePin), this.allPinMenu);
        this.writePin.addArgument("ONOFF", this.onOffMenu);

        this.writePwmPin = of(Scratch3Block.ofHandler(0, "set_pwm_gpio", BlockType.command, "Set  pwm [VALUE] to pin [PIN] of [RPI]", this::writePwmPin), this.pwmPinMenu);
        this.writePwmPin.addArgument("PIN", this.pwmPinMenu);
        this.writePwmPin.addArgument("VALUE", 255);

        this.isGpioInState = of(Scratch3Block.ofEvaluate(2, "get_gpio", BlockType.reporter, "[PIN] of [RPI]", this::isGpioInStateHandler), this.allPinMenu);
        this.isGpioInState.allowLinkBoolean((varId, workspaceBlock) -> {
            RaspberryGpioPin raspberryGpioPin = getPin(workspaceBlock);
            WorkspaceBooleanEntity workspaceBooleanEntity = workspaceBlock.getEntityContext().getEntity(WorkspaceBooleanEntity.PREFIX + varId);
            // listen from device and write to variable
            raspberryGPIOService.addGpioListener(workspaceBooleanEntity.getEntityID(), raspberryGpioPin, (state) -> {
                if (workspaceBooleanEntity.getValue() != (state.getValue() == 1)) {
                    workspaceBlock.getEntityContext().save(workspaceBooleanEntity.inverseValue());
                }
            });
            // listen boolean variable and fire events to device
            workspaceBlock.getEntityContext().addEntityUpdateListener(WorkspaceBooleanEntity.PREFIX + varId, (Consumer<WorkspaceBooleanEntity>)
                    wbe -> raspberryGPIOService.setValue(raspberryGpioPin, PinState.getState(wbe.getValue())));
        });
        this.isGpioInState.setLinkGenerator((varGroup, varName, parameter) ->
                this.isGpioInState.codeGenerator("raspberry")
                        .setMenu(this.allPinMenu, ((RaspberryGpioPin) parameter.get("pin")).name())
                        .setMenu(this.rpiIdMenu, parameter.get("entityID"))
                        .generateBooleanLink(varGroup, varName, entityContext));

        this.whenGpioInState = of(Scratch3Block.ofHandler(3, "when_gpio", BlockType.hat, "when [PIN] of [RPI] is [ONOFF]", this::whenGpioInState), this.allPinMenu);
        this.whenGpioInState.addArgument("ONOFF", this.onOffMenu);

        this.set_pull = of(Scratch3Block.ofHandler(4, "set_pull", BlockType.command, "set [PULL] to [PIN] of [RPI]", this::setPullStateHandler), this.allPinMenu);
        this.set_pull.addArgument("PULL", this.pullMenu);

        this.ds18b20Value = Scratch3Block.ofEvaluate(4, "ds18B20_status", BlockType.reporter, "DS18B20 [DS18B20] of [RPI]", this::getDS18B20ValueHandler);
        this.ds18b20Value.addArgument("DS18B20", this.ds18b20Menu);
        this.ds18b20Value.addArgument("RPI", this.rpiIdMenu);

        this.postConstruct();
    }

    private Scratch3Block of(Scratch3Block scratch3Block, MenuBlock.StaticMenuBlock pinMenu) {
        scratch3Block.addArgument("PIN", pinMenu);
        scratch3Block.addArgument("RPI", this.rpiIdMenu);
        return scratch3Block;
    }

    private Float getDS18B20ValueHandler(WorkspaceBlock workspaceBlock) {
        String ds18b20Id = workspaceBlock.getMenuValue("DS18B20", ds18b20Menu);
        return raspberryGPIOService.getDS18B20Value(ds18b20Id);
    }

    private void setPullStateHandler(WorkspaceBlock workspaceBlock) {
        PinPullResistance pullResistance = workspaceBlock.getMenuValue("PULL", pullMenu);
        RaspberryGpioPin pin = getPin(workspaceBlock);

        raspberryGPIOService.setPullResistance(pin, pullResistance);
    }

    private void whenGpioInState(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getNextOrThrow();
        RaspberryGpioPin pin = getPin(workspaceBlock);
        PinState state = getHighLow(workspaceBlock);
        BroadcastLock lock = broadcastLockManager.getOrCreateLock(workspaceBlock);
        raspberryGPIOService.addGpioListener(workspaceBlock.getId(), pin, state, lock::signalAll);

        workspaceBlock.subscribeToLock(lock);
    }

    private boolean isGpioInStateHandler(WorkspaceBlock workspaceBlock) {
        RaspberryGpioPin pin = getPin(workspaceBlock);
        return raspberryGPIOService.getValue(pin);
    }

    private void writePwmPin(WorkspaceBlock workspaceBlock) {
        RaspberryGpioPin pin = getPin(workspaceBlock);
        Integer value = workspaceBlock.getInputInteger("VALUE");
        raspberryGPIOService.setPwmValue(pin, value);
    }

    private void writePin(WorkspaceBlock workspaceBlock) {
        RaspberryGpioPin pin = getPin(workspaceBlock);
        PinState state = getHighLow(workspaceBlock);
        raspberryGPIOService.setValue(pin, state);
    }

    private PinState getHighLow(WorkspaceBlock workspaceBlock) {
        return workspaceBlock.getMenuValue("ONOFF", this.onOffMenu) == OnOffType.ON ? PinState.HIGH : PinState.LOW;
    }

    private RaspberryGpioPin getPin(WorkspaceBlock workspaceBlock) {
        return workspaceBlock.getMenuValue("PIN", allPinMenu);
    }
}
