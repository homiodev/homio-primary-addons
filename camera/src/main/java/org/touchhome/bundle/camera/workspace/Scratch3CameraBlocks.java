package org.touchhome.bundle.camera.workspace;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.VideoBaseStorageService;
import org.touchhome.bundle.api.model.KeyValueEnum;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.RawType;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamHandler;
import org.touchhome.bundle.api.video.DownloadFile;
import org.touchhome.bundle.api.video.VideoPlaybackStorage;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.*;
import org.touchhome.bundle.camera.CameraEntryPoint;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.common.util.CommonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
@Getter
@Component
public class Scratch3CameraBlocks extends Scratch3ExtensionBlocks {

    public static final String RECORD_STORAGE_MENU = "recordStorageMenu";
    protected static final String VIDEO_STREAM = "VIDEO_STREAM";
    private static final String ON_OFF = "ON_OFF";
    private static final String RECORD_TYPE = "RECORD_TYPE";

    private final BroadcastLockManager broadcastLockManager;

    private final MenuBlock.ServerMenuBlock ffmpegCameraMenu;
    private final MenuBlock.StaticMenuBlock<OnOffType.OnOffTypeEnum> onOffMenu;
    private final MenuBlock.StaticMenuBlock<RecordType> recordTypeMenu;

    private final Scratch3Block whenBoolHat;
    // private final Scratch3Block recordImageCommand;
    private final Scratch3Block recordGifOrMP4Command;
    private final Scratch3Block gifMP4Reporter;

    private final Scratch3Block setIntParamCommand;
    private final Scratch3Block setBoolParamCommand;

    private final Scratch3Block recordVideo;
    private final Scratch3Block getCameraWithProfileParameterReporter;

    private final Scratch3Block getCameraParameterReporter;

    // private final MenuBlock.ServerMenuBlock profileMenu;
    private final MenuBlock.ServerMenuBlock ffmpegCameraMenuWithProfiles;
    private final MenuBlock.StaticMenuBlock<CameraReportCommands> cameraReportCommandMenu;
    private final MenuBlock.StaticMenuBlock<BoolHatMenuEnum> boolHatMenu;
    private final MenuBlock.StaticMenuBlock<SetCameraIntParamEnum> intParamMenu;
    private final MenuBlock.StaticMenuBlock<SetCameraBoolParamEnum> boolParamMenu;
    private final MenuBlock.StaticMenuBlock<CameraProfileReportCommands> cameraProfileReportCommandMenu;

    private final MenuBlock.ServerMenuBlock cameraRecordHandler;

    private static final RetryPolicy<DownloadFile> PLAYBACK_DOWNLOAD_FILE_RETRY_POLICY =
            RetryPolicy.<DownloadFile>builder()
                    .handle(Exception.class)
                    .withDelay(Duration.ofSeconds(5))
                    .withMaxRetries(3)
                    .build();

    public Scratch3CameraBlocks(EntityContext entityContext, BroadcastLockManager broadcastLockManager,
                                CameraEntryPoint cameraEntryPoint) {
        super("#8F4D77", entityContext, cameraEntryPoint, null);
        setParent("media");
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.cameraRecordHandler = MenuBlock.ofServerItems(RECORD_STORAGE_MENU, VideoBaseStorageService.class);
        this.ffmpegCameraMenu = MenuBlock.ofServerItems("ffmpegCameraMenu", BaseFFMPEGVideoStreamEntity.class);
        this.ffmpegCameraMenuWithProfiles =
                MenuBlock.ofServer("ffmpegCameraMenuWithProfiles", "rest/camera/ffmpegWithProfiles", "-", "-");
        /*this.profileMenu = MenuBlock.ofServer("profileMenu", "/rest/camera/profiles", "-", "-")
                .setDependency(this.ffmpegCameraMenu);*/
        this.onOffMenu = MenuBlock.ofStatic("onOff", OnOffType.OnOffTypeEnum.class, OnOffType.OnOffTypeEnum.On);
        this.recordTypeMenu = MenuBlock.ofStatic("recordType", RecordType.class, RecordType.Mp4);

        this.cameraReportCommandMenu =
                MenuBlock.ofStaticKV("camReport", CameraReportCommands.class, CameraReportCommands.MotionAlarmThreshold);
        this.cameraProfileReportCommandMenu = MenuBlock.ofStaticKV("camProfileReport", CameraProfileReportCommands.class,
                CameraProfileReportCommands.Snapshot);

        this.boolHatMenu = MenuBlock.ofStaticKV("boolHatMenu", BoolHatMenuEnum.class, BoolHatMenuEnum.MotionAlarm);
        this.intParamMenu = MenuBlock.ofStaticKV("intParamMenu", SetCameraIntParamEnum.class, SetCameraIntParamEnum.MotionAlarm);
        this.boolParamMenu =
                MenuBlock.ofStaticKV("boolParamMenu", SetCameraBoolParamEnum.class, SetCameraBoolParamEnum.IRLedValue);

        // Hats
        this.whenBoolHat = withServerFfmpeg(Scratch3Block.ofHandler(10, "bool_hat",
                BlockType.hat, "[VALUE] [ON_OFF] of [VIDEO_STREAM]", this::listenBoolHat));
        this.whenBoolHat.addArgument("VALUE", this.boolHatMenu);
        this.whenBoolHat.addArgument(ON_OFF, this.onOffMenu);

        this.recordVideo = withServerFfmpegAndProfile(Scratch3Block.ofCommand(15, "record",
                "Record [VIDEO_STREAM] to file [NAME] using [FS]", this::recordCameraHandler));
        this.recordVideo.addArgument("FS", this.cameraRecordHandler);
        this.recordVideo.addArgument("NAME", "output-%03d.mp4");

        // Reporters
        this.getCameraParameterReporter = withServerFfmpeg(Scratch3Block.ofReporter(33, "camReporter",
                "Get [ENTITY] of [VIDEO_STREAM]", this::getCameraReportHandler));
        this.getCameraParameterReporter.addArgument(ENTITY, this.cameraReportCommandMenu);

        this.getCameraWithProfileParameterReporter = withServerFfmpegAndProfile(Scratch3Block.ofReporter(40, "get_img",
                "Get [ENTITY] of [VIDEO_STREAM]", this::getCameraWithProfileParameterReporter));
        this.getCameraWithProfileParameterReporter.addArgument(ENTITY, this.cameraProfileReportCommandMenu);

        this.gifMP4Reporter = withServerFfmpegAndProfile(Scratch3Block.ofReporter(110, "get_gif_mp4",
                "Get [RECORD_TYPE] [TIME]sec. of [VIDEO_STREAM]", this::getGifMP4Reporter));
        this.gifMP4Reporter.addArgument("TIME", ArgumentType.number, "5");
        this.gifMP4Reporter.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.gifMP4Reporter.appendSpace();

        // Commands
        /*this.recordImageCommand = withServerFfmpeg(Scratch3Block.ofCommand(140, "record_image",
                "Record image of [VIDEO_STREAM]", this::firePollImageCommand));*/

        this.recordGifOrMP4Command = withServerFfmpegAndProfile(Scratch3Block.ofCommand(150, "record_gif_mp4",
                "Record [RECORD_TYPE] [TIME]sec. to file [FILE_NAME] of [VIDEO_STREAM]",
                this::fireRecordGifMP4Command));
        this.recordGifOrMP4Command.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.recordGifOrMP4Command.addArgument("TIME", ArgumentType.number, "5");
        this.recordGifOrMP4Command.addArgument("FILE_NAME", ArgumentType.string, "name");

        this.setIntParamCommand = withServerFfmpeg(Scratch3Block.ofCommand(160, "set_int_param",
                "Set [ENTITY] as [VALUE] of [VIDEO_STREAM]", this::setIntParamCommand));
        this.setIntParamCommand.addArgument(ENTITY, this.intParamMenu);
        this.setIntParamCommand.addArgument(VALUE, 35);

        this.setBoolParamCommand = withServerFfmpeg(Scratch3Block.ofCommand(170, "set_bool_param",
                "Set [ENTITY] as [ON_OFF] of [VIDEO_STREAM]", this::setBoolParamCommand));
        this.setBoolParamCommand.addArgument(ENTITY, this.boolParamMenu);
        this.setBoolParamCommand.addArgument(ON_OFF, this.onOffMenu);
    }

    private Object getCameraReportHandler(WorkspaceBlock workspaceBlock) {
        CameraReportCommands camReportCommands = workspaceBlock.getMenuValue(ENTITY, this.cameraReportCommandMenu);
        return camReportCommands.reportFn.apply(workspaceBlock, this);
    }

    private void recordCameraHandler(WorkspaceBlock workspaceBlock) {
        VideoBaseStorageService storage = workspaceBlock.getMenuValueEntity("FS", this.cameraRecordHandler);
        String output = workspaceBlock.getInputString("NAME");

        CameraWithProfile camera = getCameraProfile(workspaceBlock);

        workspaceBlock.handleAndRelease(
                () -> storage.startRecord(output, output, camera.profile, camera.entity),
                () -> storage.stopRecord(output, output, camera.entity));
    }

    @RequiredArgsConstructor
    private enum SetCameraBoolParamEnum implements KeyValueEnum {
        IRLedValue("IR led value", (entity, onOffType, workspaceBlock) -> {
            if (entity instanceof OnvifCameraEntity) {
                Consumer<Boolean> handler = ((OnvifCameraEntity) entity).getBaseBrandCameraHandler().getIRLedHandler();
                if (handler == null) {
                    workspaceBlock.logErrorAndThrow("Unable to find ir led handler for camera: <{}>", entity.getTitle());
                } else {
                    handler.accept(onOffType.boolValue());
                }
            } else {
                workspaceBlock.logErrorAndThrow("Unable to find ir led handler for camera: <{}>", entity.getTitle());
            }
        }),

        AudioAlarm("Audio alarm", (entity, onOffType, workspaceBlock) -> {
            entity.getVideoHandler().audioDetected(onOffType.boolValue());
        }),

        MotionAlarm("Motion alarm", (entity, onOffType, workspaceBlock) -> {
            entity.getVideoHandler().motionDetected(onOffType.boolValue(), CHANNEL_EXTERNAL_MOTION);
        });

        @Getter
        private final String value;

        private final SetHandler handler;

        private interface SetHandler {
            void handle(BaseFFMPEGVideoStreamEntity entity, OnOffType.OnOffTypeEnum onOffType, WorkspaceBlock workspaceBlock);
        }
    }

    @RequiredArgsConstructor
    private enum BoolHatMenuEnum implements KeyValueEnum {
        AudioAlarm("Audio alarm", (entity, onOffType, scratch, workspaceBlock, next) -> {
            BroadcastLock audioAlarm = scratch.broadcastLockManager.getOrCreateLock(workspaceBlock,
                    CHANNEL_AUDIO_ALARM + ":" + entity.getEntityID());
            workspaceBlock.subscribeToLock(audioAlarm, state -> ((OnOffType) state).boolValue() == onOffType.boolValue(),
                    next::handle);
        }),

        MotionAlarm("Motion alarm", (entity, onOffType, scratch, workspaceBlock, next) -> {
            BroadcastLock motionAlarm =
                    scratch.broadcastLockManager.getOrCreateLock(workspaceBlock, MOTION_ALARM + ":" + entity.getEntityID());
            workspaceBlock.subscribeToLock(motionAlarm, state -> ((OnOffType) state).boolValue() == onOffType.boolValue(),
                    next::handle);
        });

        @Getter
        private final String value;
        private final AlarmHandler handler;

        private interface AlarmHandler {
            void handle(BaseFFMPEGVideoStreamEntity entity, OnOffType.OnOffTypeEnum onOffType, Scratch3CameraBlocks scratch,
                        WorkspaceBlock workspaceBlock, WorkspaceBlock next);
        }
    }

    @RequiredArgsConstructor
    private enum SetCameraIntParamEnum implements KeyValueEnum {
        AudioAlarm("Audio alarm", (entity, value, scratch, workspaceBlock) -> {
            entity.setAudioThreshold(value);
        }),

        MotionAlarm("Motion alarm", (entity, value, scratch, workspaceBlock) -> {
            entity.setMotionThreshold(value);
        });

        @Getter
        private final String value;
        private final SetCameraIntParamHandler handler;

        private interface SetCameraIntParamHandler {
            void handle(BaseFFMPEGVideoStreamEntity entity, int value, Scratch3CameraBlocks scratch,
                        WorkspaceBlock workspaceBlock);
        }
    }

    @RequiredArgsConstructor
    private enum CameraProfileReportCommands implements KeyValueEnum {
        Snapshot("Snapshot", (workspaceBlock, scratch, cameraProfile) -> {
            return cameraProfile.entity.getVideoHandler().recordImageSync(cameraProfile.profile);
        }),
        LastPlayback("Last playback", (workspaceBlock, scratch, cameraProfile) -> {
            BaseFFMPEGVideoStreamEntity entity = cameraProfile.entity;
            if (entity instanceof VideoPlaybackStorage) {
                VideoPlaybackStorage videoPlaybackStorage = (VideoPlaybackStorage) entity;
                String profile = cameraProfile.profile;
                VideoPlaybackStorage.PlaybackFile playbackFile =
                        videoPlaybackStorage.getLastPlaybackFile(workspaceBlock.getEntityContext(), profile);
                if (playbackFile == null) {
                    return null;
                }
                Path path = TouchHomeUtils.getMediaPath().resolve("camera").resolve(entity.getEntityID()).resolve("playback")
                        .resolve(playbackFile.id);

                DownloadFile downloadFile;

                if (Files.exists(path)) {
                    downloadFile = new DownloadFile(new UrlResource(path.toUri()), Files.size(path),
                            playbackFile.id, null);
                } else {
                    downloadFile = Failsafe.with(PLAYBACK_DOWNLOAD_FILE_RETRY_POLICY)
                            .onFailure(event -> {
                                log.error("Unable to download playback file: <{}>. <{}>. Msg: <{}>",
                                        entity.getTitle(),
                                        playbackFile.id,
                                        CommonUtils.getErrorMessage(event.getFailure()));
                            })
                            .get(context -> {
                                log.info("Reply <{}>. Download playback video file <{}>. <{}>", context.getAttemptCount(),
                                        entity.getTitle(),
                                        playbackFile.id);
                                return videoPlaybackStorage.downloadPlaybackFile(workspaceBlock.getEntityContext(), "main",
                                        playbackFile.id, path);
                            });
                }
                return new RawType(IOUtils.toByteArray(downloadFile.getStream().getInputStream()),
                        "video/mp4", playbackFile.name);
            }
            workspaceBlock.logErrorAndThrow("Camera not support playback storage");
            return null;
        });

        @Getter
        private final String value;
        private final CameraProfileHandler handler;

        interface CameraProfileHandler {
            RawType handle(WorkspaceBlock workspaceBlock, Scratch3CameraBlocks scratch3CameraBlocks,
                           CameraWithProfile cameraProfile) throws IOException;
        }
    }

    @RequiredArgsConstructor
    private enum CameraReportCommands implements KeyValueEnum {
        IRValue("IR led value", (workspaceBlock, scratch) -> {
            BaseFFMPEGVideoStreamEntity entity = scratch.getEntity(workspaceBlock, scratch.ffmpegCameraMenu);
            if (entity instanceof OnvifCameraEntity) {
                Supplier<Boolean> handler = ((OnvifCameraEntity) entity).getBaseBrandCameraHandler().getIrLedValueHandler();
                if (handler == null) {
                    workspaceBlock.logErrorAndThrow("Unable to find ir led get value handler for camera: <{}>",
                            entity.getTitle());
                } else {
                    return OnOffType.of(handler.get());
                }
            } else {
                workspaceBlock.logErrorAndThrow("Unable to find ir led get value handler for camera: <{}>", entity.getTitle());
            }
            return null;
        }),
        AudioAlarmThreshold("Audio alarm threshold", (workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock, scratch.ffmpegCameraMenu).getAudioThreshold();
        }),
        MotionAlarmThreshold("Motion alarm threshold", (workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock, scratch.ffmpegCameraMenu).getMotionThreshold();
        }),
        LastMotionType("Last motion type", (workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock, scratch.ffmpegCameraMenu)
                    .getVideoHandler().getAttributes().getOrDefault(CHANNEL_LAST_MOTION_TYPE, StringType.EMPTY).toString();
        }),
        LastImage("Last image", (workspaceBlock, scratch) -> {
            return new RawType(scratch.getEntity(workspaceBlock, scratch.ffmpegCameraMenu).getLastSnapshot(),
                    MimeTypeUtils.IMAGE_JPEG_VALUE);
        });

        @Getter
        private final String value;
        private final BiFunction<WorkspaceBlock, Scratch3CameraBlocks, Object> reportFn;
    }

    @SneakyThrows
    private RawType getCameraWithProfileParameterReporter(WorkspaceBlock workspaceBlock) {
        CameraWithProfile cameraProfile = getCameraProfile(workspaceBlock);
        CameraProfileReportCommands command = workspaceBlock.getMenuValue(ENTITY, this.cameraProfileReportCommandMenu);
        return command.handler.handle(workspaceBlock, this, cameraProfile);
    }

    private void setIntParamCommand(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue(ENTITY, this.intParamMenu).handler
                .handle(getEntity(workspaceBlock, this.ffmpegCameraMenu), workspaceBlock.getInputInteger(VALUE), this,
                        workspaceBlock);
    }

    private void setBoolParamCommand(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue(ENTITY, this.boolParamMenu).handler
                .handle(getEntity(workspaceBlock, this.ffmpegCameraMenu), workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu),
                        workspaceBlock);
    }

    private RawType getGifMP4Reporter(WorkspaceBlock workspaceBlock) {
        Integer time = workspaceBlock.getInputInteger("TIME");
        String timeStr = (String) workspaceBlock.getValue("time");
        if (timeStr != null) {
            time = Integer.parseInt(timeStr);
        }
        CameraWithProfile camera = getCameraProfile(workspaceBlock);
        RecordType recordType = workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu);
        return new RawType(recordType.getHandler
                .getData(camera.entity.getVideoHandler(), camera.profile,
                        time), recordType.mimeType,
                camera.entity.getTitle() + " " + recordType.name());
    }

    private void fireRecordGifMP4Command(WorkspaceBlock workspaceBlock) {
        CameraWithProfile camera = getCameraProfile(workspaceBlock);
        workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).recordHandler
                .record(camera.entity.getVideoHandler(), camera.profile,
                        workspaceBlock.getInputString("FILE_NAME"),
                        workspaceBlock.getInputInteger("TIME"));
    }

    /*private void firePollImageCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).startSnapshot();
    }*/

    private void listenBoolHat(WorkspaceBlock workspaceBlock) {
        BoolHatMenuEnum boolHatMenuEnum = workspaceBlock.getMenuValue("ALARM", this.boolHatMenu);
        workspaceBlock.handleNext(next -> {
            BaseFFMPEGVideoStreamEntity entity = getEntity(workspaceBlock, this.ffmpegCameraMenu);

            workspaceBlock.onRelease(() -> entity.getVideoHandler().removeMotionAlarmListener(workspaceBlock.getId()));
            entity.getVideoHandler().startOrAddMotionAlarmListener(workspaceBlock.getId());

            OnOffType.OnOffTypeEnum onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
            boolHatMenuEnum.handler.handle(entity, onOffType, this, workspaceBlock, next);
        });
    }

    private <T extends BaseFFMPEGVideoStreamEntity> T getEntity(WorkspaceBlock workspaceBlock,
                                                                MenuBlock.ServerMenuBlock serverMenuBlock) {
        BaseFFMPEGVideoStreamEntity entity = workspaceBlock.getMenuValueEntityRequired(VIDEO_STREAM, serverMenuBlock);
        if (!entity.isStart()) {
            throw new RuntimeException("Video camera " + entity.getTitle() + " not started");
        }
        return (T) entity;
    }

    private Scratch3Block withServerFfmpegAndProfile(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(VIDEO_STREAM, this.ffmpegCameraMenuWithProfiles);
        return scratch3Block;
    }

    private Scratch3Block withServerFfmpeg(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(VIDEO_STREAM, this.ffmpegCameraMenu);
        return scratch3Block;
    }

    @RequiredArgsConstructor
    public enum RecordType {
        Gif(BaseFFMPEGVideoStreamHandler::recordGifSync, BaseFFMPEGVideoStreamHandler::recordGif, MediaType.IMAGE_GIF_VALUE),
        Mp4(BaseFFMPEGVideoStreamHandler::recordMp4Sync, BaseFFMPEGVideoStreamHandler::recordMp4, "video/mp4");
        private final GetDataHandler getHandler;
        private final RecordHandler recordHandler;
        public final String mimeType;

        private interface GetDataHandler {
            byte[] getData(BaseFFMPEGVideoStreamHandler handler, String profile, int time);
        }

        private interface RecordHandler {
            void record(BaseFFMPEGVideoStreamHandler handler, String fileName, String profile, int time);
        }
    }

    private CameraWithProfile getCameraProfile(WorkspaceBlock workspaceBlock) {
        String[] cameraWithProfile = workspaceBlock.getMenuValue(VIDEO_STREAM, ffmpegCameraMenuWithProfiles).split("/");
        BaseFFMPEGVideoStreamEntity cameraEntity = workspaceBlock.getEntityContext().getEntity(cameraWithProfile[0]);
        String profile = cameraWithProfile.length > 1 ? cameraWithProfile[1] : null;
        return new CameraWithProfile(cameraEntity, profile);
    }

    @AllArgsConstructor
    private static class CameraWithProfile {
        private BaseFFMPEGVideoStreamEntity entity;
        private String profile;
    }
}
