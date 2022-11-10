package org.touchhome.bundle.raspberry.workspace;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.DecimalType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.util.RaspberryGpioPin;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.raspberry.RaspberryEntrypoint;
import org.touchhome.bundle.raspberry.RaspberryGPIOService;
import org.touchhome.bundle.raspberry.entity.RaspberryDeviceEntity;

@Getter
@Component
public class Scratch3RaspberryBlocks extends Scratch3ExtensionBlocks {

  private final MenuBlock.StaticMenuBlock<OnOffType.OnOffTypeEnum> onOffMenu;
  private final MenuBlock.StaticMenuBlock<RaspberryGpioPin> allPinMenu;
  private final MenuBlock.StaticMenuBlock<RaspberryGpioPin> pwmPinMenu;
  private final MenuBlock.StaticMenuBlock<PinPullResistance> pullMenu;

  private final MenuBlock.ServerMenuBlock rpiIdMenu;
  private final MenuBlock.ServerMenuBlock ds18b20Menu;

  private final RaspberryGPIOService raspberryGPIOService;
  private final Scratch3Block isGpioInState;

  public Scratch3RaspberryBlocks(RaspberryGPIOService raspberryGPIOService,
      EntityContext entityContext, RaspberryEntrypoint raspberryEntrypoint) {
    super("#83be41", entityContext, raspberryEntrypoint);
    this.raspberryGPIOService = raspberryGPIOService;

    this.allPinMenu = menuStatic("allPinMenu", RaspberryGpioPin.class, RaspberryGpioPin.PIN3, p ->
        p.getPin().getSupportedPinModes().contains(PinMode.DIGITAL_INPUT));
    this.pwmPinMenu = menuStatic("pwmPinMenu", RaspberryGpioPin.class, RaspberryGpioPin.PIN12,
        p -> p.name().equals("PIN12") || p.name().equals("PIN33"));

    this.rpiIdMenu = menuServerItems("rpiIdMenu", RaspberryDeviceEntity.class, "Raspberry");
    this.onOffMenu = menuStatic("onOffMenu", OnOffType.OnOffTypeEnum.class, OnOffType.OnOffTypeEnum.On);

    this.pullMenu = menuStatic("pullMenu", PinPullResistance.class, PinPullResistance.PULL_UP);

    this.ds18b20Menu = menuServer("ds18b20Menu", "rest/raspberry/DS18B20", "DS18B20");

    of(blockCommand(0, "set_gpio", "Set [ONOFF] to pin [PIN] of [RPI]", this::writePin, block ->
        block.addArgument("ONOFF", this.onOffMenu)), this.allPinMenu);

    of(blockCommand(0, "set_pwm_gpio", "Set  pwm [VALUE] to pin [PIN] of [RPI]",
        this::writePwmPin, block -> {
          block.addArgument("PIN", this.pwmPinMenu);
          block.addArgument("VALUE", 255);
        }), this.pwmPinMenu);

    this.isGpioInState = of(blockReporter(2, "get_gpio", "[PIN] of [RPI]", this::isGpioInStateHandler, block -> {
      /*block.allowLinkBoolean((varId, workspaceBlock) -> {
        RaspberryGpioPin raspberryGpioPin = getPin(workspaceBlock);
        // listen from device and write to variable
        raspberryGPIOService.addGpioListener(varId, raspberryGpioPin, state -> {
          entityContext.var().setIfNotMatch(varId, state.getValue());
        });
        // listen boolean variable and fire events to device
        workspaceBlock.getEntityContext().event().addEventListener(varId,
            "workspace-rpi-bool-listen" + varId, value -> {
              if (Number.class.isAssignableFrom(value.getClass())) {
                PinState pinState = PinState.getState(((Number) value).intValue());
                raspberryGPIOService.setValue(raspberryGpioPin, pinState);

              }
            });
      });*/
      /* block.setLinkGenerator((varGroup, varName, parameter) ->
          block.codeGenerator("raspberry")
              .setMenu(this.allPinMenu, ((RaspberryGpioPin) parameter.get("pin")).name())
              .setMenu(this.rpiIdMenu, parameter.get("entityID"))
              .generateBooleanLink(varGroup, varName, entityContext));*/
    }), this.allPinMenu);

    of(blockHat(3, "when_gpio", "when [PIN] of [RPI] is [ONOFF]", this::whenGpioInState, block ->
        block.addArgument("ONOFF", this.onOffMenu)), this.allPinMenu);

    of(blockCommand(4, "set_pull", "set [PULL] to [PIN] of [RPI]", this::setPullStateHandler, block ->
        block.addArgument("PULL", this.pullMenu)), this.allPinMenu);

    blockReporter(4, "DS18B20_status", "DS18B20 [DS18B20] of [RPI]", this::getDS18B20ValueHandler, block -> {
      block.addArgument("DS18B20", this.ds18b20Menu);
      block.addArgument("RPI", this.rpiIdMenu);
    });
  }

  private Scratch3Block of(Scratch3Block scratch3Block, MenuBlock.StaticMenuBlock<RaspberryGpioPin> pinMenu) {
    scratch3Block.addArgument("PIN", pinMenu);
    scratch3Block.addArgument("RPI", this.rpiIdMenu);
    return scratch3Block;
  }

  private State getDS18B20ValueHandler(WorkspaceBlock workspaceBlock) {
    String ds18b20Id = workspaceBlock.getMenuValue("DS18B20", ds18b20Menu);
    return new DecimalType(raspberryGPIOService.getDS18B20Value(ds18b20Id));
  }

  private void setPullStateHandler(WorkspaceBlock workspaceBlock) {
    PinPullResistance pullResistance = workspaceBlock.getMenuValue("PULL", pullMenu);
    RaspberryGpioPin pin = getPin(workspaceBlock);

    raspberryGPIOService.setPullResistance(pin, pullResistance);
  }

  private void whenGpioInState(WorkspaceBlock workspaceBlock) {
    workspaceBlock.handleNext(next -> {
      RaspberryGpioPin pin = getPin(workspaceBlock);
      PinState state = getHighLow(workspaceBlock);
      BroadcastLock lock = workspaceBlock.getBroadcastLockManager().getOrCreateLock(workspaceBlock);
      raspberryGPIOService.addGpioListener(workspaceBlock.getId(), pin, state, lock::signalAll);

      workspaceBlock.subscribeToLock(lock, next::handle);
    });
  }

  private State isGpioInStateHandler(WorkspaceBlock workspaceBlock) {
    RaspberryGpioPin pin = getPin(workspaceBlock);
    return OnOffType.of(raspberryGPIOService.getValue(pin));
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
    return workspaceBlock.getMenuValue("ONOFF", this.onOffMenu) == OnOffType.OnOffTypeEnum.On ? PinState.HIGH : PinState.LOW;
  }

  private RaspberryGpioPin getPin(WorkspaceBlock workspaceBlock) {
    return workspaceBlock.getMenuValue("PIN", allPinMenu);
  }
}
