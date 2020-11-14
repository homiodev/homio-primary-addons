package org.touchhome.bundle.firmata.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.firmata4j.Consumer;
import org.firmata4j.IODevice;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.firmata.FirmataWatchdog;
import org.firmata4j.firmata.parser.FirmataEventType;
import org.firmata4j.fsm.Event;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.hardware.HardwareEvents;
import org.touchhome.bundle.api.json.NotificationEntityJSON;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.util.FlowMap;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.firmata.model.FirmataBaseEntity;
import org.touchhome.bundle.firmata.provider.command.FirmataCommand;
import org.touchhome.bundle.firmata.provider.command.FirmataCommandPlugin;
import org.touchhome.bundle.firmata.provider.command.FirmataOneWireResponseDataCommand;
import org.touchhome.bundle.firmata.provider.util.THUtil;
import org.touchhome.bundle.firmata.setting.FirmataWatchDogIntervalSetting;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor
public abstract class FirmataDeviceCommunicator<T extends FirmataBaseEntity<T>> extends Consumer<Event> implements IODeviceEventListener {

    @Getter
    private final FirmataOneWireResponseDataCommand oneWireCommand;

    private final EntityContext entityContext;
    private final FirmataCommandPlugins firmataCommandPlugins;
    private final BroadcastLockManager broadcastLockManager;
    private final HardwareEvents hardwareEvents;
    private T entity;

    @Getter
    private IODeviceWrapper device;
    private IODevice ioDevice;

    private String firmataErrorEvent;
    private String firmataStartEvent;

    private Long lastRestartAttempt = 0L;

    public FirmataDeviceCommunicator(EntityContext entityContext, T entity) {
        this.entity = entity;
        this.entityContext = entityContext;
        this.oneWireCommand = entityContext.getBean(FirmataOneWireResponseDataCommand.class);
        this.firmataCommandPlugins = entityContext.getBean(FirmataCommandPlugins.class);
        this.broadcastLockManager = entityContext.getBean(BroadcastLockManager.class);
        this.hardwareEvents = entityContext.getBean(HardwareEvents.class);

        this.entityContext.addEntityUpdateListener(entity.getEntityID(), t -> this.entityUpdated((T) t, false));
        this.entityContext.addEntityRemovedListener(entity.getEntityID(), t -> this.entityUpdated((T) t, true));
    }

    protected abstract IODevice createIODevice(T entity) throws Exception;

    public abstract long generateUniqueIDOnRegistrationSuccess();

    protected T getEntity() {
        return entity;
    }

    @SneakyThrows
    public void destroy() {
        if (this.ioDevice != null) {
            try {
                this.ioDevice.stop();
            } catch (Exception ex) {
                log.warn("Unable to stop firmata communicator: <{}>", ex.getMessage());
            }
        }
    }

    public final String restart() {
        // skip restart if status ONLINE
        if (entity.getStatus() == Status.ONLINE && entity.getJoined() == Status.ONLINE) {
            return "ACTION.COMMUNICATOR.ALREADY_RUN";
        }

        // try restart not often that once per minute
        if (System.currentTimeMillis() - lastRestartAttempt < 60000) {
            throw new RuntimeException("ACTION.COMMUNICATOR.RESTART_TOO_OFTEN");
        }
        lastRestartAttempt = System.currentTimeMillis();

        // try restart
        try {
            this.destroy();

            this.ioDevice = this.createIODevice(this.entity);
            if (ioDevice == null) {
                throw new RuntimeException("ACTION.COMMUNICATOR.UNABLE_CREATE");
            }
            this.device = new IODeviceWrapper(ioDevice, this);
            ioDevice.addProtocolMessageHandler("sysexCustomMessage", this);
            ioDevice.addEventListener(this);
            ioDevice.start();

            // this method throws exception if unable to get any notifications from
            ioDevice.ensureInitializationIsDone();
            updateDeviceStatus(entity, Status.ONLINE, null);

            FirmataWatchdog watchdog = new FirmataWatchdog(TimeUnit.MINUTES.toMillis(entityContext.getSettingValue(FirmataWatchDogIntervalSetting.class)), () -> {
                entityContext.updateDelayed(entity, t -> t.setJoined(Status.ERROR));
                entityContext.addHeaderNotification(NotificationEntityJSON.warn(entity.getEntityID())
                        .setName("Firmata-" + entity.getEntityID()).setDescription("Firmata Watchdog error"));
                this.hardwareEvents.fireEvent(firmataErrorEvent);
            });

            ioDevice.addProtocolMessageHandler(FirmataEventType.ANY, watchdog);
            if (this.entity.getTarget() != -1) {
                this.device.sendMessage(FirmataCommand.SYSEX_REGISTER);
            }

            return "ACTION.COMMUNICATOR.SUCCESS";
        } catch (Exception ex) {
            updateDeviceStatus(entity, Status.ERROR, TouchHomeUtils.getErrorMessage(ex));
            log.error("Error while initialize device: {} for device type: {}", entity.getTitle(), getClass().getSimpleName(), ex);
            throw new RuntimeException("ACTION.COMMUNICATOR.UNKNOWN_ERROR");
        }
    }

    @Override
    public void accept(Event event) {
        ByteBuffer payload = ByteBuffer.wrap((byte[]) event.getBodyItem("sysexCustomMessage"));
        byte commandID = payload.get();
        FirmataCommandPlugin handler = firmataCommandPlugins.getFirmataCommandPlugin(commandID);
        if (handler == null) {
            log.error("Unable to find firmata custom handler with type: <{}>", commandID);
            return;
        }
        byte messageID = handler.hasTH() ? THUtil.getByte(payload) : 0;
        short target = handler.hasTH() ? THUtil.getShort(payload) : 0;
        if (entity.getJoined() == Status.ONLINE) {
            handler.handle(device, entity, messageID, payload);
        } else if (entity.getTarget() == target || handler.isHandleBroadcastEvents()) {
            handler.broadcastHandle(device, entity, messageID, target, payload);
        }
    }

    @Override
    public void onStart(IOEvent event) {
        log.info("Firmata device: " + entity.getTitle() + " communication started");
        hardwareEvents.fireEvent(firmataStartEvent);
    }

    @Override
    public void onStop(IOEvent event) {
        log.info("Firmata device: " + entity.getTitle() + " communication stopped");
        hardwareEvents.fireEvent(firmataErrorEvent);
    }

    @Override
    public void onPinChange(IOEvent event) {
        if (entity.getTarget() != -1) {
            broadcastLockManager.signalAll(entity.getTarget() + "_pin_" + event.getPin().getIndex(), event.getPin().getValue());
        }
    }

    @Override
    public void onMessageReceive(IOEvent event, String message) {
        log.info("Firmata <{}> got message: <{}>", entity.getTitle(), message);
    }

    private void entityUpdated(T entity, boolean remove) {
        if (remove) {
            this.destroy();
            this.hardwareEvents.removeEvents(firmataErrorEvent, firmataStartEvent);
        } else {
            if (!this.entity.getTitle().equals(entity.getTitle())) {
                this.firmataErrorEvent = this.hardwareEvents.addEvent("firmata-comm-error-" + entity.getEntityID(),
                        "FIRMATA.EVENT.FIRMATA_COMM_FAILED", FlowMap.of("NAME", entity.getTitle()));

                this.firmataStartEvent = this.hardwareEvents.addEvent("firmata-comm-start-" + entity.getEntityID(),
                        "FIRMATA.EVENT.FIRMATA_COMM_STARTED", FlowMap.of("NAME", entity.getTitle()));
            }
            this.entity = entity;
        }
    }

    private void updateDeviceStatus(T entity, Status status, String statusMessage) {
        if (entity.getStatus() != status) {
            entityContext.updateDelayed(entity, t -> t.setStatus(status).setStatusMessage(statusMessage));

            entityContext.addHeaderNotification(NotificationEntityJSON.warn(entity.getEntityID())
                    .setName("A-" + entity.getEntityID()).setDescription("Communicator status: " + status));
        }
        if (status == Status.OFFLINE || status == Status.ERROR) {
            this.hardwareEvents.fireEvent(firmataErrorEvent);
        }
    }
}
