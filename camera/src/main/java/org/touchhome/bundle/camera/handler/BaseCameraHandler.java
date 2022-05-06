package org.touchhome.bundle.camera.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.EntityContextBGP;
import org.touchhome.bundle.api.model.Status;
import org.touchhome.bundle.api.netty.HasBootstrapServer;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.ui.field.action.v1.UIInputBuilder;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.ffmpeg.FfmpegInputDeviceHardwareRepository;
import org.touchhome.bundle.camera.setting.FFMPEGInstallPathSetting;
import org.touchhome.bundle.camera.ui.CameraActionsContext;
import org.touchhome.common.util.CommonUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Log4j2
public abstract class BaseCameraHandler<T extends BaseVideoCameraEntity> implements HasBootstrapServer, CameraActionsContext {

    @Getter
    protected final EntityContext entityContext;
    protected final BroadcastLockManager broadcastLockManager;
    @Getter
    protected final int serverPort;
    protected final FfmpegInputDeviceHardwareRepository ffmpegInputDeviceHardwareRepository;
    public ReentrantLock lockCurrentSnapshot = new ReentrantLock();
    @Getter
    protected byte[] latestSnapshot = new byte[0];
    @Setter
    @Getter
    protected T cameraEntity;
    protected String cameraEntityID;
    @Getter
    protected String ffmpegLocation;
    @Getter
    protected Map<String, State> attributes = new ConcurrentHashMap<>();
    @Getter
    protected Map<String, State> requestAttributes = new ConcurrentHashMap<>();
    @Getter
    protected long lastAnswerFromCamera;
    @Getter
    private Path ffmpegGifOutputPath;
    @Getter
    private Path ffmpegMP4OutputPath;
    @Getter
    private Path ffmpegHLSOutputPath;
    @Getter
    private Path ffmpegImageOutputPath;
    @Getter
    private boolean isCameraOnline = false; // Used so only 1 error is logged when a network issue occurs.
    @Getter
    private boolean isHandlerInitialized = false;
    private EntityContextBGP.ThreadContext<Void> cameraConnectionJob;
    private EntityContextBGP.ThreadContext<Void> pollCameraJob;
    private Map<String, Consumer<Status>> stateListeners = new HashMap<>();

    // actions holder
    private UIInputBuilder uiInputBuilder;

    public BaseCameraHandler(T cameraEntity, EntityContext entityContext) {
        setCameraEntity(cameraEntity);
        this.cameraEntityID = cameraEntity.getEntityID();

        this.entityContext = entityContext;
        this.ffmpegInputDeviceHardwareRepository = entityContext.getBean(FfmpegInputDeviceHardwareRepository.class);
        this.broadcastLockManager = entityContext.getBean(BroadcastLockManager.class);
        this.serverPort = cameraEntity.getServerPort();

        Path ffmpegOutputPath = cameraEntity.getFolder();
        ffmpegImageOutputPath = CommonUtils.createDirectoriesIfNotExists(ffmpegOutputPath.resolve("images"));
        ffmpegGifOutputPath = CommonUtils.createDirectoriesIfNotExists(ffmpegOutputPath.resolve("gif"));
        ffmpegMP4OutputPath = CommonUtils.createDirectoriesIfNotExists(ffmpegOutputPath.resolve("mp4"));
        ffmpegHLSOutputPath = CommonUtils.createDirectoriesIfNotExists(ffmpegOutputPath.resolve("hls"));
        try {
            FileUtils.cleanDirectory(ffmpegHLSOutputPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Unable to clean path: " + ffmpegHLSOutputPath);
        }

        // for custom ffmpeg path
        entityContext.setting().listenValueAndGet(FFMPEGInstallPathSetting.class, "listen-ffmpeg-path-" + cameraEntityID,
                path -> {
                    this.ffmpegLocation = path.toString();
                    this.restart("ffmpeg location changed", null, false);
                });
    }

    protected abstract void pollingCameraConnection();

    protected abstract void pollCameraRunnable();

    public final boolean initialize(T cameraEntity) {
        if (isHandlerInitialized) {
            return true;
        }
        log.info("Initialize camera: <{}>", cameraEntity.getTitle());
        isHandlerInitialized = true;
        try {
            if (cameraEntity == null) {
                if (this.cameraEntity == null) {
                    throw new RuntimeException("Unable to init camera with id: " + cameraEntityID);
                }
            } else if (!cameraEntity.getEntityID().equals(cameraEntityID)) {
                throw new RuntimeException("Unable to init camera <" + cameraEntity + "> with different id than: " + cameraEntityID);
            } else {
                this.cameraEntity = cameraEntity;
            }

            log.info("Init camera: <{}>", this.cameraEntity.getTitle());
            initialize0(cameraEntity);
            cameraConnectionJob = entityContext.bgp().schedule("poll-camera-connection-" + cameraEntityID,
                    60, TimeUnit.SECONDS, this::pollingCameraConnection, true, true);
            return true;
        } catch (Exception ex) {
            disposeAndSetStatus(Status.ERROR, "Error while init camera: " + CommonUtils.getErrorMessage(ex));
        }
        return false;
    }

    protected abstract void initialize0(T cameraEntity);

    public final void disposeAndSetStatus(Status status, String reason) {
        if (isHandlerInitialized) {
            // need here to avoid infinite loop
            isHandlerInitialized = false;
            // set it before to avoid recursively disposing from listeners
            log.warn("Set camera <{}> to status <{}>. Msg: <{}>", cameraEntity.getTitle(), status, reason);

            cameraEntity.setStatus(status, reason);
            entityContext.updateDelayed(this.cameraEntity, e -> e.setStart(false));
            entityContext.ui().sendEntityUpdated(this.cameraEntity);
            this.stateListeners.values().forEach(h -> h.accept(status));

            // need set to true to handle dispose !!!
            isHandlerInitialized = true;
            dispose();
        }
    }

    public final void dispose() {
        if (isHandlerInitialized) {
            log.info("Dispose camera: <{}>", cameraEntity.getTitle());
            isHandlerInitialized = false;
            disposeCameraConnectionJob();
            disposePollCameraJob();
            try {
                dispose0();
            } catch (Exception ex) {
                log.error("Error while dispose camera: <{}>", cameraEntity.getTitle(), ex);
            }
            isCameraOnline = false;
        }
    }

    protected abstract void dispose0();

    public abstract void recordMp4(String fileName, @Nullable String profile, int secondsToRecord);

    public abstract void recordGif(String fileName, @Nullable String profile, int secondsToRecord);

    public final void bringCameraOnline() {
        lastAnswerFromCamera = System.currentTimeMillis();
        if (!isCameraOnline && isHandlerInitialized) {
            isCameraOnline = true;
            updateStatus(Status.ONLINE, null);

            disposeCameraConnectionJob();
            pollCameraJob = entityContext.bgp().schedule("poll-camera-runnable-" + cameraEntityID,
                    8, TimeUnit.SECONDS, this::pollCameraRunnable, true, true);
        }
    }

    private void disposeCameraConnectionJob() {
        Optional.ofNullable(cameraConnectionJob).ifPresent(EntityContextBGP.ThreadContext::cancel);
    }

    private void disposePollCameraJob() {
        Optional.ofNullable(pollCameraJob).ifPresent(EntityContextBGP.ThreadContext::cancel);
    }

    public final boolean restart(String reason, T cameraEntity, boolean force) {
        if (force && !this.isHandlerInitialized) {
            return initialize(cameraEntity);
        } else if (isCameraOnline) { // if already offline dont try reconnecting in 6 seconds, we want 30sec wait.
            updateStatus(Status.OFFLINE, reason); // will try to reconnect again as camera may be rebooting.
            dispose();
            return initialize(cameraEntity);
        }
        return false;
    }

    protected final void updateStatus(Status status, String message) {
        if (message != null) {
            log.info("Camera update status: <{}>. <{}>", status, message);
        } else {
            log.info("Camera update status: <{}>", status);
        }
        cameraEntity.setStatus(status, message);
    }

    public UIInputBuilder assembleActions() {
        if (this.uiInputBuilder == null) {
            this.uiInputBuilder = entityContext.ui().inputBuilder();
            assembleAdditionalCameraActions(uiInputBuilder);
        }
        return uiInputBuilder;
    }

    protected void assembleAdditionalCameraActions(UIInputBuilder uiInputBuilder) {

    }

    public void setAttribute(String key, State state) {
        attributes.put(key, state);
        broadcastLockManager.signalAll(key + ":" + cameraEntityID, state);
    }

    @Override
    public State getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttributeRequest(String key, State state) {
        requestAttributes.put(key, state);
    }

    protected final void fireFfmpeg(Ffmpeg ffmpeg, Consumer<Ffmpeg> handler) {
        if (ffmpeg != null) {
            handler.accept(ffmpeg);
        }
    }

    public abstract void startSnapshot();

    public void deleteDirectories() {
        CommonUtils.deleteDirectory(ffmpegGifOutputPath);
        CommonUtils.deleteDirectory(ffmpegMP4OutputPath);
        CommonUtils.deleteDirectory(ffmpegImageOutputPath);
    }

    @Override
    public String getName() {
        return cameraEntity.getTitle();
    }

    public void addCameraChangeState(String key, Consumer<Status> handler) {
        this.stateListeners.put(key, handler);
    }

    public void removeCameraChangeState(String key) {
        this.stateListeners.remove(key);
    }

    public abstract void testOnline();
}
