package org.touchhome.bundle.firmata.workspace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataMessageFactory;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.state.CompareType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.firmata.FirmataBundleEntryPoint;
import org.touchhome.bundle.firmata.model.FirmataBaseEntity;
import org.touchhome.bundle.firmata.provider.command.FirmataCommand;
import org.touchhome.bundle.firmata.provider.command.FirmataGetTimeValueCommand;

import java.util.function.Function;

@Log4j2
@Getter
@Component
public class Scratch3FirmataBlocks extends Scratch3FirmataBaseBlock {

    private final FirmataGetTimeValueCommand getTimeValueCommand;
    private final Scratch3Block getProtocol;
    private final Scratch3Block isReady;

    private final MenuBlock.StaticMenuBlock<OnOffType> onOffMenu;
    private final MenuBlock.StaticMenuBlock<CompareType> opMenu;
    private final MenuBlock.StaticMenuBlock<PinMode> pinModeMenu;

    private final MenuBlock.ServerMenuBlock pinMenuDigital;
    private final MenuBlock.ServerMenuBlock pinMenuPwm;
    private final MenuBlock.ServerMenuBlock pinMenuAnalog;
    private final MenuBlock.ServerMenuBlock pinMenuAll;
    private final MenuBlock.ServerMenuBlock pinMenuServo;

    private final Scratch3Block pinRead;
    private final Scratch3Block pwmWrite;
    private final Scratch3Block invertPin;
    private final Scratch3Block digitalWrite;
    private final Scratch3Block getTime;
    private final Scratch3Block whenDeviceReady;
    private final Scratch3Block whenPinOpValue;
    private final Scratch3Block whenPinChanged;
    private final Scratch3Block analogWrite;
    private final Scratch3Block setPinMode;
    private final Scratch3Block setSamplingInterval;
    private final Scratch3Block setServoConfig;
    private final Scratch3Block delay;

    public Scratch3FirmataBlocks(EntityContext entityContext, FirmataBundleEntryPoint firmataBundleEntryPoint,
                                 BroadcastLockManager broadcastLockManager,
                                 FirmataGetTimeValueCommand getTimeValueCommand) {
        super("#C9BF43", entityContext, firmataBundleEntryPoint, broadcastLockManager, null);
        this.getTimeValueCommand = getTimeValueCommand;

        // Menu
        this.pinMenuDigital = MenuBlock.ofServer("pinMenuDigital", REST_PIN + Pin.Mode.OUTPUT).setDependency(this.firmataIdMenu);
        this.pinMenuPwm = MenuBlock.ofServer("pinMenuPwm", REST_PIN + Pin.Mode.PWM).setDependency(this.firmataIdMenu);
        this.pinMenuAnalog = MenuBlock.ofServer("pinMenuAnalog", REST_PIN + Pin.Mode.ANALOG).setDependency(this.firmataIdMenu);
        this.pinMenuServo = MenuBlock.ofServer("pinMenuAnalog", REST_PIN + Pin.Mode.SERVO).setDependency(this.firmataIdMenu);
        this.pinMenuAll = MenuBlock.ofServer("pinMenuAll", REST_PIN).setDependency(this.firmataIdMenu);

        this.onOffMenu = MenuBlock.ofStatic("onOffMenu", OnOffType.class, OnOffType.OFF);
        this.opMenu = MenuBlock.ofStatic("opMenu", CompareType.class, CompareType.GREATER);
        this.pinModeMenu = MenuBlock.ofStatic("pinModeMenu", PinMode.class, PinMode.PULL_UP);

        // Blocks
        this.pinRead = ofPin(Scratch3Block.ofEvaluate(5, "pinRead", BlockType.reporter,
                "Get [PIN] of [FIRMATA]", this::readPinEvaluate), this.pinMenuAll);

        this.digitalWrite = ofPin(Scratch3Block.ofHandler(10, "digital_write", BlockType.command,
                "Set(D) Pin [PIN] [ON_OFF] to [FIRMATA]", this::digitalWriteHandler), this.pinMenuDigital);
        this.digitalWrite.addArgument("ON_OFF", this.onOffMenu);

        this.analogWrite = ofPin(Scratch3Block.ofHandler(15, "analogWrite", BlockType.command,
                "Set(A) Pin [PIN] [VALUE] to [FIRMATA]", this::pwmWriteHandler), this.pinMenuAnalog);
        this.analogWrite.addArgument(VALUE, 50);

        this.pwmWrite = ofPin(Scratch3Block.ofHandler(20, "pwmWrite", BlockType.command,
                "Set(PWM) Pin [PIN] [VALUE] to [FIRMATA]", this::pwmWriteHandler), this.pinMenuPwm);
        this.pwmWrite.addArgument(VALUE, 50);

        this.invertPin = ofPin(Scratch3Block.ofHandler(25, "invertPin", BlockType.command,
                "Invert Pin(D) [PIN] of [FIRMATA]", this::invertPinHandler), this.pinMenuDigital);

        this.isReady = of(Scratch3Block.ofEvaluate(40, "ready", BlockType.Boolean, "[FIRMATA] ready",
                this::isReadyEvaluate));

        this.whenPinChanged = ofPin(Scratch3Block.ofHandler(45, "when_pin_changed", BlockType.hat,
                "When Pin [PIN] changed of [FIRMATA]", this::whenPinChangedHandler), this.pinMenuAll, "#CCD247");

        this.whenPinOpValue = ofPin(Scratch3Block.ofHandler(50, "when_pin_op_value", BlockType.hat,
                "When Pin [PIN] [OP] value [VALUE] of [FIRMATA]", this::whenPinOpValueHandler), this.pinMenuAll, "#CCD247");
        this.whenPinOpValue.addArgument("OP", this.opMenu);
        this.whenPinOpValue.addArgument(VALUE, 0);

        this.whenDeviceReady = of(Scratch3Block.ofHandler(55, "when_device_ready", BlockType.hat,
                "When [FIRMATA] ready", this::whenDeviceReady), "#CCD247");

        this.setPinMode = ofPin(Scratch3Block.ofHandler(60, "set_mode", BlockType.command,
                "Set Pin [PIN] mode [MODE] to [FIRMATA]", this::setPinModeHandler), this.pinMenuPwm, "#939844");
        this.setPinMode.addArgument("MODE", this.pinModeMenu);

        this.setSamplingInterval = of(Scratch3Block.ofHandler(65, "set_sampling_interval", BlockType.command,
                "Set Sampling [INTERVAL] of [FIRMATA]", this::setSamplingIntervalHandler), "#939844");
        this.setSamplingInterval.addArgument("INTERVAL", 19);

        this.setServoConfig = ofPin(Scratch3Block.ofHandler(70, "set_servo_config", BlockType.command,
                "Servo pulse min/max [MIN]/[MAX] of [FIRMATA]", this::setServoConfigHandler), this.pinMenuServo, "#939844");
        this.setServoConfig.addArgument("MIN", 0);
        this.setServoConfig.addArgument("MAX", 100);

        this.delay = of(Scratch3Block.ofHandler(70, "delay", BlockType.command,
                "Delay [VALUE] of [FIRMATA]", this::delayHandler), "#939844");
        this.delay.addArgument(VALUE, 3);

        this.getTime = of(Scratch3Block.ofEvaluate(80, "time", BlockType.reporter,
                "time of [FIRMATA]", this::getTimeEvaluate), "#939844");

        this.getProtocol = of(Scratch3Block.ofEvaluate(90, "protocol", BlockType.reporter,
                "protocol of [FIRMATA]", this::getProtocolEvaluate), "#939844");
    }

    private void delayHandler(WorkspaceBlock workspaceBlock) {
        int value = workspaceBlock.getInputInteger(VALUE);
        execute(workspaceBlock, false, entity -> {
            entity.getDevice().getIoOneWire().sendOneWireDelay((byte) 0, value);
        });
    }

    private void whenDeviceReady(WorkspaceBlock workspaceBlock) {
        String firmataId = workspaceBlock.getMenuValue(FIRMATA, this.firmataIdMenu);
        FirmataBaseEntity entity = entityContext.getEntity(firmataId);
        WorkspaceBlock substack = workspaceBlock.getNext();
        if (entity == null || substack == null || entity.getTarget() == -1) {
            return;
        }

        if (entity.getJoined() == Status.ONLINE) {
            substack.handle();
        } else {
            BroadcastLock<Object> readyLock = broadcastLockManager.getOrCreateLock(workspaceBlock, "firmata-ready-" + entity.getTarget());
            if (readyLock.await(workspaceBlock)) {
                substack.handle();
            }
        }
    }

    private void setServoConfigHandler(WorkspaceBlock workspaceBlock) {
        executeNoResponse(workspaceBlock, false, this.pinMenuAll, (entity, pin) -> {
            int minPulse = workspaceBlock.getInputInteger("MIN");
            int maxPulse = workspaceBlock.getInputInteger("MAX");
            pin.setServoMode(minPulse, maxPulse);
        });
    }

    private void setSamplingIntervalHandler(WorkspaceBlock workspaceBlock) {
        int interval = workspaceBlock.getInputInteger("INTERVAL");
        execute(workspaceBlock, false, entity -> {
            entity.getDevice().sendMessage(FirmataMessageFactory.setSamplingInterval(interval));
        });
    }

    private void setPinModeHandler(WorkspaceBlock workspaceBlock) {
        PinMode mode = workspaceBlock.getMenuValue("MODE", this.pinModeMenu);
        executeNoResponse(workspaceBlock, false, this.pinMenuAll, (entity, pin) -> pin.setMode(mode.value));
    }

    private void whenPinChangedHandler(WorkspaceBlock workspaceBlock) {
        whenPinChangedHandler(workspaceBlock, value -> true);
    }

    private void whenPinOpValueHandler(WorkspaceBlock workspaceBlock) {
        CompareType compareType = workspaceBlock.getMenuValue("OP", this.opMenu);
        Integer compareValue = workspaceBlock.getInputInteger(VALUE);
        whenPinChangedHandler(workspaceBlock, value -> compareType.match(value, compareValue));
    }

    private void whenPinChangedHandler(WorkspaceBlock workspaceBlock, Function<Long, Boolean> checkFn) {
        WorkspaceBlock substack = workspaceBlock.getNext();
        Integer pinNum = getPin(workspaceBlock, this.pinMenuAll);
        if (substack != null && pinNum != null) {
            execute(workspaceBlock, true, entity -> {
                Pin pin = entity.getDevice().getIoDevice().getPin(pinNum);
                BroadcastLock<Long> lock = broadcastLockManager.getOrCreateLock(workspaceBlock, entity.getTarget() + "_pin_" + pin.getIndex());
                workspaceBlock.subscribeToLock(lock, checkFn);
            });
        }
    }

    private Boolean isReadyEvaluate(WorkspaceBlock workspaceBlock) {
        return Boolean.TRUE.equals(execute(workspaceBlock, false, null, (entity, pin) -> entity.getDevice().getIoDevice().isReady()));
    }

    private String getProtocolEvaluate(WorkspaceBlock workspaceBlock) {
        return execute(workspaceBlock, false, null, (entity, pin) -> entity.getDevice().getIoDevice().getProtocol());
    }

    private Long getTimeEvaluate(WorkspaceBlock workspaceBlock) {
        return execute(workspaceBlock, false, entity -> {
            byte messageID = entity.getDevice().sendMessage(FirmataCommand.SYSEX_GET_TIME_COMMAND);
            return getTimeValueCommand.waitForValue(entity, messageID);
        });
    }

    private Long readPinEvaluate(WorkspaceBlock workspaceBlock) {
        return execute(workspaceBlock, false, pinMenuAll, (entity, pin) -> pin.getValue());
    }

    private void digitalWriteHandler(WorkspaceBlock workspaceBlock) {
        updatePinValue(workspaceBlock, Pin.Mode.OUTPUT, pin ->
                workspaceBlock.getMenuValue("ON_OFF", this.onOffMenu).longValue());
    }

    private void pwmWriteHandler(WorkspaceBlock workspaceBlock) {
        updatePinValue(workspaceBlock, Pin.Mode.PWM, pin -> workspaceBlock.getInputInteger(VALUE).longValue());
    }

    private void invertPinHandler(WorkspaceBlock workspaceBlock) {
        updatePinValue(workspaceBlock, Pin.Mode.OUTPUT, pin -> pin.getValue() == 1 ? 0L : 1L);
    }

    private void updatePinValue(WorkspaceBlock workspaceBlock, Pin.Mode mode, Function<Pin, Long> pinValueProducer) {
        executeNoResponse(workspaceBlock, false, this.pinMenuDigital, (entity, pin) -> {
            pin.setMode(mode);
            Long value = pinValueProducer.apply(pin);
            pin.setValue(value);
        });
    }

    @AllArgsConstructor
    public enum PinMode {
        ENCODER(Pin.Mode.ENCODER),
        SERVO(Pin.Mode.SERVO),
        SERIAL(Pin.Mode.SERIAL),
        PULL_UP(Pin.Mode.PULLUP);
        private final Pin.Mode value;
    }
}
