package org.touchhome.bundle.camera.workspace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.storage.CameraBaseStorageService;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.RawType;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.api.workspace.BroadcastLock;
import org.touchhome.bundle.api.workspace.BroadcastLockManager;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.*;
import org.touchhome.bundle.camera.CameraEntryPoint;
import org.touchhome.bundle.camera.entity.BaseFFmpegStreamEntity;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;

import java.util.function.BiFunction;

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

    private final Scratch3Block whenAlarmHat;
    private final Scratch3Block fireAlarmCommand;
    // private final Scratch3Block recordImageCommand;
    private final Scratch3Block recordGifOrMP4Command;
    private final Scratch3Block gifMP4Reporter;
    private final Scratch3Block setAlarmThresholdCommand;
    private final Scratch3Block recordVideo;

    // private final MenuBlock.ServerMenuBlock profileMenu;
    private final MenuBlock.ServerMenuBlock ffmpegCameraMenuWithProfiles;
    private final MenuBlock.StaticMenuBlock<CameraReportCommands> cameraReportCommandMenu;
    private final MenuBlock.StaticMenuBlock<AlarmEnum> alarmMenu;
    private final MenuBlock.ServerMenuBlock cameraRecordHandler;
    private final Scratch3Block cameraReporter;

    public Scratch3CameraBlocks(EntityContext entityContext, BroadcastLockManager broadcastLockManager,
                                CameraEntryPoint cameraEntryPoint) {
        super("#8F4D77", entityContext, cameraEntryPoint, null);
        setParent("media");
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.cameraRecordHandler = MenuBlock.ofServerItems(RECORD_STORAGE_MENU, CameraBaseStorageService.class);
        this.ffmpegCameraMenu = MenuBlock.ofServerItems("ffmpegCameraMenu", BaseFFmpegStreamEntity.class);
        this.ffmpegCameraMenuWithProfiles = MenuBlock.ofServer("ffmpegCameraMenuWithProfiles", "rest/camera/ffmpegWithProfiles", "-", "-");
        /*this.profileMenu = MenuBlock.ofServer("profileMenu", "/rest/camera/profiles", "-", "-")
                .setDependency(this.ffmpegCameraMenu);*/
        this.onOffMenu = MenuBlock.ofStatic("onOff", OnOffType.OnOffTypeEnum.class, OnOffType.OnOffTypeEnum.On);
        this.cameraReportCommandMenu = MenuBlock.ofStatic("camReport", CameraReportCommands.class, CameraReportCommands.MotionAlarmThreshold);
        this.recordTypeMenu = MenuBlock.ofStatic("recordType", RecordType.class, RecordType.Mp4);
        this.alarmMenu = MenuBlock.ofStatic("alarmMenu", AlarmEnum.class, AlarmEnum.Motion);

        // Hats
        this.whenAlarmHat = withServerFfmpeg(Scratch3Block.ofHandler(10, "alarm",
                BlockType.hat, "[ALARM] alarm [ON_OFF] of [VIDEO_STREAM]", this::listenAlarmHat));
        this.whenAlarmHat.addArgument("ALARM", this.alarmMenu);
        this.whenAlarmHat.addArgument(ON_OFF, this.onOffMenu);

        this.recordVideo = withServerFfmpegAndProfile(Scratch3Block.ofCommand(15, "record",
                "Record [VIDEO_STREAM] to file [NAME] using [FS]", this::recordCameraHandler));
        this.recordVideo.addArgument("FS", this.cameraRecordHandler);
        this.recordVideo.addArgument("NAME", "output-%03d.mp4");

        // Reporters
        this.cameraReporter = withServerFfmpeg(Scratch3Block.ofReporter(33, "camReporter",
                "Get [COMMAND] of [VIDEO_STREAM]", this::getCameraReportHandler));
        this.cameraReporter.addArgument("COMMAND", this.cameraReportCommandMenu);

        this.gifMP4Reporter = withServerFfmpegAndProfile(Scratch3Block.ofReporter(110, "get_gif_mp4",
                "Get [RECORD_TYPE] [TIME]sec. of [VIDEO_STREAM]", this::getGifMP4Reporter));
        this.gifMP4Reporter.addArgument("TIME", ArgumentType.number, "5");
        this.gifMP4Reporter.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.gifMP4Reporter.appendSpace();

        // Commands
        this.fireAlarmCommand = withServerFfmpeg(Scratch3Block.ofCommand(120, "fire__alarm",
                "Fire [ALARM] alarm [ON_OFF] of [VIDEO_STREAM]", this::fireAlarmCommand));
        this.fireAlarmCommand.addArgument("ALARM", this.alarmMenu);
        this.fireAlarmCommand.addArgument(ON_OFF, this.onOffMenu);

        /*this.recordImageCommand = withServerFfmpeg(Scratch3Block.ofCommand(140, "record_image",
                "Record image of [VIDEO_STREAM]", this::firePollImageCommand));*/

        this.recordGifOrMP4Command = withServerFfmpegAndProfile(Scratch3Block.ofCommand(150, "record_gif_mp4",
                "Record [RECORD_TYPE] [TIME]sec. to file [FILE_NAME] of [VIDEO_STREAM]",
                this::fireRecordGifMP4Command));
        this.recordGifOrMP4Command.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.recordGifOrMP4Command.addArgument("TIME", ArgumentType.number, "5");
        this.recordGifOrMP4Command.addArgument("FILE_NAME", ArgumentType.string, "name");

        this.setAlarmThresholdCommand = withServerFfmpeg(Scratch3Block.ofCommand(160, "set_alarm_threshold",
                "Set [ALARM] alarm as [VALUE] of [VIDEO_STREAM]", this::setAlarmThresholdCommand));
        this.setAlarmThresholdCommand.addArgument("ALARM", this.alarmMenu);
        this.setAlarmThresholdCommand.addArgument(VALUE, 35);
    }

    private Object getCameraReportHandler(WorkspaceBlock workspaceBlock) {
        CameraReportCommands camReportCommands = workspaceBlock.getMenuValue("COMMAND", this.cameraReportCommandMenu);
        return camReportCommands.reportFn.apply(workspaceBlock, this);
    }

    private void recordCameraHandler(WorkspaceBlock workspaceBlock) {
        CameraBaseStorageService storage = workspaceBlock.getMenuValueEntity("FS", this.cameraRecordHandler);
        String output = workspaceBlock.getInputString("NAME");

        CameraWithProfile camera = getCameraProfile(workspaceBlock);

        workspaceBlock.handleAndRelease(
                () -> storage.startRecord(output, output, camera.profile, camera.entity),
                () -> storage.stopRecord(output, output, camera.entity));
    }

    @RequiredArgsConstructor
    private enum AlarmEnum {
        Audio((entity, onOffType, scratch, workspaceBlock, next) -> {
            BroadcastLock audioAlarm = scratch.broadcastLockManager.getOrCreateLock(workspaceBlock, CHANNEL_AUDIO_ALARM + ":" + entity.getEntityID());
            workspaceBlock.subscribeToLock(audioAlarm, state -> ((OnOffType) state).boolValue() == onOffType.boolValue(), next::handle);
        }, (entity, onOffType, scratch, workspaceBlock) -> {
            entity.getCameraHandler().audioDetected(onOffType.boolValue());
        }, BaseFFmpegCameraHandler::setAudioThreshold),

        Motion((entity, onOffType, scratch, workspaceBlock, next) -> {
            BroadcastLock motionAlarm = scratch.broadcastLockManager.getOrCreateLock(workspaceBlock, MOTION_ALARM + ":" + entity.getEntityID());
            workspaceBlock.subscribeToLock(motionAlarm, state -> ((OnOffType) state).boolValue() == onOffType.boolValue(),
                    next::handle);
        }, (entity, onOffType, scratch, workspaceBlock) -> {
            entity.getCameraHandler().motionDetected(onOffType.boolValue(), CHANNEL_EXTERNAL_MOTION);
        }, BaseFFmpegCameraHandler::setMotionThreshold);

        private final AlarmHandler handler;
        private final FireAlarmHandler fireHandler;
        private final SetAlarmThresholdHandler alarmThresholdHandler;

        private interface AlarmHandler {
            void handle(BaseFFmpegStreamEntity entity, OnOffType.OnOffTypeEnum onOffType, Scratch3CameraBlocks scratch, WorkspaceBlock workspaceBlock, WorkspaceBlock next);
        }

        private interface FireAlarmHandler {
            void handle(BaseFFmpegStreamEntity entity, OnOffType.OnOffTypeEnum onOffType, Scratch3CameraBlocks scratch, WorkspaceBlock workspaceBlock);
        }

        private interface SetAlarmThresholdHandler {
            void handle(BaseFFmpegCameraHandler fFmpegHandler, int threshold);
        }
    }

    @RequiredArgsConstructor
    private enum CameraReportCommands {
        AudioAlarmThreshold((workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock).getAudioThreshold();
        }),
        MotionAlarmThreshold((workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock).getMotionThreshold();
        }),
        LastMotionType((workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock).getCameraHandler().getAttributes().getOrDefault(CHANNEL_LAST_MOTION_TYPE, StringType.EMPTY).toString();
        }),
        LastImage((workspaceBlock, scratch) -> {
            return new RawType(scratch.getEntity(workspaceBlock).getLastSnapshot(), MimeTypeUtils.IMAGE_JPEG_VALUE);
        });

        private final BiFunction<WorkspaceBlock, Scratch3CameraBlocks, Object> reportFn;
    }

    private void setAlarmThresholdCommand(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue("ALARM", this.alarmMenu).alarmThresholdHandler
                .handle(getEntity(workspaceBlock).getCameraHandler(), workspaceBlock.getInputInteger(VALUE));
    }

    private RawType getGifMP4Reporter(WorkspaceBlock workspaceBlock) {
        CameraWithProfile camera = getCameraProfile(workspaceBlock);
        RecordType recordType = workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu);
        return new RawType(recordType.getHandler
                .getData(camera.entity.getCameraHandler(), camera.profile,
                        workspaceBlock.getInputInteger("TIME")), recordType.mimeType);
    }

    private void fireRecordGifMP4Command(WorkspaceBlock workspaceBlock) {
        CameraWithProfile camera = getCameraProfile(workspaceBlock);
        workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).recordHandler
                .record(camera.entity.getCameraHandler(), camera.profile,
                        workspaceBlock.getInputString("FILE_NAME"),
                        workspaceBlock.getInputInteger("TIME"));
    }

    /*private void firePollImageCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).startSnapshot();
    }*/

    private void fireAlarmCommand(WorkspaceBlock workspaceBlock) {
        OnOffType.OnOffTypeEnum onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        AlarmEnum alarmEnum = workspaceBlock.getMenuValue("ALARM", this.alarmMenu);
        alarmEnum.fireHandler.handle(getEntity(workspaceBlock), onOffType, this, workspaceBlock);
    }

    private void listenAlarmHat(WorkspaceBlock workspaceBlock) {
        AlarmEnum alarmEnum = workspaceBlock.getMenuValue("ALARM", this.alarmMenu);
        workspaceBlock.handleNext(next -> {
            BaseFFmpegStreamEntity entity = getEntity(workspaceBlock);

            workspaceBlock.onRelease(() -> entity.getCameraHandler().removeMotionAlarmListener(workspaceBlock.getId()));
            entity.getCameraHandler().startOrAddMotionAlarmListener(workspaceBlock.getId());

            OnOffType.OnOffTypeEnum onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
            alarmEnum.handler.handle(entity, onOffType, this, workspaceBlock, next);
        });
    }

    private <T extends BaseFFmpegStreamEntity> T getEntity(WorkspaceBlock workspaceBlock) {
        BaseFFmpegStreamEntity entity = workspaceBlock.getMenuValueEntityRequired(VIDEO_STREAM, this.ffmpegCameraMenu);
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
        Gif(BaseFFmpegCameraHandler::recordGifSync, BaseFFmpegCameraHandler::recordGif, MediaType.IMAGE_GIF_VALUE),
        Image(BaseFFmpegCameraHandler::recordGifSync, (baseFFmpegCameraHandler, fileName, profile, secondsToRecord) -> {
            baseFFmpegCameraHandler.recordImageSync(profile);
        }, MediaType.IMAGE_JPEG_VALUE),
        Mp4(BaseFFmpegCameraHandler::recordMp4Sync, BaseFFmpegCameraHandler::recordMp4, "video/mp4");
        private final GetDataHandler getHandler;
        private final RecordHandler recordHandler;
        public final String mimeType;

        private interface GetDataHandler {
            byte[] getData(BaseFFmpegCameraHandler handler, String profile, int time);
        }

        private interface RecordHandler {
            void record(BaseFFmpegCameraHandler handler, String fileName, String profile, int time);
        }
    }

    private CameraWithProfile getCameraProfile(WorkspaceBlock workspaceBlock) {
        String[] cameraWithProfile = workspaceBlock.getMenuValue(VIDEO_STREAM, ffmpegCameraMenuWithProfiles).split("/");
        BaseFFmpegStreamEntity cameraEntity = workspaceBlock.getEntityContext().getEntity(cameraWithProfile[0]);
        String profile = cameraWithProfile.length > 1 ? cameraWithProfile[1] : null;
        return new CameraWithProfile(cameraEntity, profile);
    }

    @AllArgsConstructor
    private static class CameraWithProfile {
        private BaseFFmpegStreamEntity entity;
        private String profile;
    }
}
