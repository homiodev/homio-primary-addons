package org.touchhome.bundle.camera.workspace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.touchhome.bundle.api.EntityContext;
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

    public static final String FFMPEG_CAMERA_MENU = "ffmpegCameraMenu";
    protected static final String VIDEO_STREAM = "VIDEO_STREAM";
    private static final String ON_OFF = "ON_OFF";
    private static final String RECORD_TYPE = "RECORD_TYPE";

    private final BroadcastLockManager broadcastLockManager;

    private final MenuBlock.ServerMenuBlock ffmpegCameraMenu;
    private final MenuBlock.StaticMenuBlock<OnOffType> onOffMenu;
    private final MenuBlock.StaticMenuBlock<RecordType> recordTypeMenu;

    private final Scratch3Block whenAlarmHat;
    private final Scratch3Block fireAlarmCommand;
    private final Scratch3Block recordImageCommand;
    private final Scratch3Block imageReporter;
    private final Scratch3Block recordGifOrMP4Command;
    private final Scratch3Block gifMP4Reporter;
    private final Scratch3Block reporter;
    private final Scratch3Block setAlarmThresholdCommand;
    //private final MenuBlock.ServerMenuBlock profileMenu;
    private final MenuBlock.ServerMenuBlock ffmpegCameraMenuWithProfiles;
    private final MenuBlock.StaticMenuBlock<ReportCommands> reportCommandMenu;
    private final MenuBlock.StaticMenuBlock<AlarmEnum> alarmMenu;

    public Scratch3CameraBlocks(EntityContext entityContext, BroadcastLockManager broadcastLockManager,
                                CameraEntryPoint cameraEntryPoint) {
        super("#8F4D77", entityContext, cameraEntryPoint, null);
        setParent("media");
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.ffmpegCameraMenu = MenuBlock.ofServerItems(FFMPEG_CAMERA_MENU, BaseFFmpegStreamEntity.class);
        this.ffmpegCameraMenuWithProfiles = MenuBlock.ofServer("ffmpegCameraMenuWithProfiles", "rest/camera/ffmpegWithProfiles", "-", "-");
        /*this.profileMenu = MenuBlock.ofServer("profileMenu", "/rest/camera/profiles", "-", "-")
                .setDependency(this.ffmpegCameraMenu);*/
        this.onOffMenu = MenuBlock.ofStatic("onOff", OnOffType.class, OnOffType.ON);
        this.reportCommandMenu = MenuBlock.ofStatic("report", ReportCommands.class, ReportCommands.LastImage);
        this.recordTypeMenu = MenuBlock.ofStatic("recordType", RecordType.class, RecordType.Mp4);
        this.alarmMenu = MenuBlock.ofStatic("alarmMenu", AlarmEnum.class, AlarmEnum.Motion);

        // Hats
        this.whenAlarmHat = withServerFfmpegAndOnOff(Scratch3Block.ofHandler(10, "alarm",
                BlockType.hat, "[ALARM] alarm [ON_OFF] of [VIDEO_STREAM]", this::listenAlarmHat));
        this.whenAlarmHat.addArgument("ALARM", this.alarmMenu);

        // Reporters
        this.reporter = withServerFfmpeg(Scratch3Block.ofReporter(30, "reporter",
                "Get [COMMAND] of [VIDEO_STREAM]", this::getReportHandler));
        this.reporter.addArgument("COMMAND", this.reportCommandMenu);

        this.imageReporter = withServerFfmpegAndProfile(Scratch3Block.ofReporter(100, "get_image",
                "Get image of [VIDEO_STREAM]", this::getImageReporter));

        this.gifMP4Reporter = withServerFfmpeg(Scratch3Block.ofReporter(110, "get_gif_mp4",
                "Get [RECORD_TYPE] [TIME]sec. of [VIDEO_STREAM]", this::getGifMP4Reporter));
        this.gifMP4Reporter.addArgument("TIME", ArgumentType.number, "5");
        this.gifMP4Reporter.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.gifMP4Reporter.appendSpace();

        // Commands
        this.fireAlarmCommand = withServerFfmpegAndOnOff(Scratch3Block.ofCommand(120, "fire__alarm",
                "Fire [ALARM] alarm [ON_OFF] of [VIDEO_STREAM]", this::fireAlarmCommand));
        this.fireAlarmCommand.addArgument("ALARM", this.alarmMenu);

        this.recordImageCommand = withServerFfmpeg(Scratch3Block.ofCommand(140, "record_image",
                "Record image of [VIDEO_STREAM]", this::firePollImageCommand));

        this.recordGifOrMP4Command = withServerFfmpeg(Scratch3Block.ofCommand(150, "record_gif_mp4",
                "Record [RECORD_TYPE] [TIME]sec. to file [FILE_NAME] of [VIDEO_STREAM]",
                this::fireRecordGifMP4Command));
        this.recordGifOrMP4Command.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.recordGifOrMP4Command.addArgument("TIME", ArgumentType.number, "5");
        this.recordGifOrMP4Command.addArgument("FILE_NAME", ArgumentType.string, "name");

        this.setAlarmThresholdCommand = withServerFfmpegAndValue(Scratch3Block.ofCommand(160, "set_alarm_threshold",
                "Set [ALARM] alarm as [VALUE] of [VIDEO_STREAM]", this::setAlarmThresholdCommand), 35);
        this.setAlarmThresholdCommand.addArgument("ALARM", this.alarmMenu);
    }

    private Object getReportHandler(WorkspaceBlock workspaceBlock) {
        ReportCommands reportCommands = workspaceBlock.getMenuValue("COMMAND", this.reportCommandMenu);
        return reportCommands.reportFn.apply(workspaceBlock, this);
    }

    @RequiredArgsConstructor
    private enum AlarmEnum {
        Audio((entity, onOffType, scratch, workspaceBlock, next) -> {
            BroadcastLock audioAlarm = scratch.broadcastLockManager.getOrCreateLock(workspaceBlock, CHANNEL_AUDIO_ALARM + ":" + entity.getEntityID());
            workspaceBlock.subscribeToLock(audioAlarm, state -> ((OnOffType) state).boolValue() == onOffType.boolValue(), next::handle);
        }, (entity, onOffType, scratch, workspaceBlock) -> {
            entity.getCameraHandler().audioDetected(onOffType.boolValue());
        }, BaseFFmpegCameraHandler::setAudioAlarmThreshold),

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
            void handle(BaseFFmpegStreamEntity entity, OnOffType onOffType, Scratch3CameraBlocks scratch, WorkspaceBlock workspaceBlock, WorkspaceBlock next);
        }

        private interface FireAlarmHandler {
            void handle(BaseFFmpegStreamEntity entity, OnOffType onOffType, Scratch3CameraBlocks scratch, WorkspaceBlock workspaceBlock);
        }

        private interface SetAlarmThresholdHandler {
            void handle(BaseFFmpegCameraHandler fFmpegHandler, String inputString);
        }
    }

    @RequiredArgsConstructor
    private enum ReportCommands {
        AudioAlarmThreshold((workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock).getAudioThreshold();
        }),
        MotionAlarmThreshold((workspaceBlock, scratch) -> {
            return scratch.getEntity(workspaceBlock).getMotionThreshold();
        }),
        LastMotionType((workspaceBlock, scratch) -> {
            return scratch.getCameraStringAttribute(workspaceBlock, CHANNEL_LAST_MOTION_TYPE);
        }),
        LastImage((workspaceBlock, scratch) -> {
            return new RawType(scratch.getEntity(workspaceBlock).getLastSnapshot(), MimeTypeUtils.IMAGE_JPEG_VALUE);
        });

        private final BiFunction<WorkspaceBlock, Scratch3CameraBlocks, Object> reportFn;
    }

    private void setAlarmThresholdCommand(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue("ALARM", this.alarmMenu).alarmThresholdHandler
                .handle(getFFmpegHandler(workspaceBlock), workspaceBlock.getInputString(VALUE));
    }

    private RawType getGifMP4Reporter(WorkspaceBlock workspaceBlock) {
        return new RawType(workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).getHandler
                .apply(getFFmpegHandler(workspaceBlock),
                        workspaceBlock.getInputInteger("TIME")), "image/gif");
    }

    private void fireRecordGifMP4Command(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).recordHandler
                .record(getFFmpegHandler(workspaceBlock),
                        workspaceBlock.getInputString("FILE_NAME"),
                        workspaceBlock.getInputInteger("TIME"));
    }

    private RawType getImageReporter(WorkspaceBlock workspaceBlock) {
        String[] cameraWithProfile = workspaceBlock.getMenuValue(VIDEO_STREAM, this.ffmpegCameraMenuWithProfiles).split("/");
        BaseFFmpegStreamEntity cameraEntity = entityContext.getEntity(cameraWithProfile[0]);
        String profile = cameraWithProfile.length > 1 ? cameraWithProfile[1] : null;
        return cameraEntity.getCameraHandler().recordImageSync(profile);
    }

    private void firePollImageCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).startSnapshot();
    }

    private void fireAlarmCommand(WorkspaceBlock workspaceBlock) {
        OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        AlarmEnum alarmEnum = workspaceBlock.getMenuValue("ALARM", this.alarmMenu);
        alarmEnum.fireHandler.handle(getEntity(workspaceBlock), onOffType, this, workspaceBlock);
    }

    private void listenAlarmHat(WorkspaceBlock workspaceBlock) {
        AlarmEnum alarmEnum = workspaceBlock.getMenuValue("ALARM", this.alarmMenu);
        workspaceBlock.handleNext(next -> {
            BaseFFmpegStreamEntity entity = getEntity(workspaceBlock);
            if (!entity.getCameraHandler().setMotionThreshold("ON")) {
                workspaceBlock.logErrorAndThrow("Unable to start motion detection");
            }
            OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
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

    private String getCameraStringAttribute(WorkspaceBlock workspaceBlock, String key) {
        return getFFmpegHandler(workspaceBlock).getAttributes().getOrDefault(key, StringType.EMPTY).toString();
    }

    private BaseFFmpegCameraHandler getFFmpegHandler(WorkspaceBlock workspaceBlock) {
        return getEntity(workspaceBlock).getCameraHandler();
    }

    private Scratch3Block withServerFfmpeg(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(VIDEO_STREAM, this.ffmpegCameraMenu);
        return scratch3Block;
    }

    private Scratch3Block withServerFfmpegAndProfile(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(VIDEO_STREAM, this.ffmpegCameraMenuWithProfiles);
        return scratch3Block;
    }

    private Scratch3Block withServerFfmpegAndOnOff(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(ON_OFF, this.onOffMenu);
        return withServerFfmpeg(scratch3Block);
    }

    private Scratch3Block withServerFfmpegAndValue(Scratch3Block scratch3Block, int value) {
        scratch3Block.addArgument(VALUE, value);
        return withServerFfmpeg(scratch3Block);
    }

    @RequiredArgsConstructor
    public enum RecordType {
        Gif(BaseFFmpegCameraHandler::recordGifSync, BaseFFmpegCameraHandler::recordGif),
        Mp4(BaseFFmpegCameraHandler::recordMp4Sync, BaseFFmpegCameraHandler::recordMp4);
        private final BiFunction<BaseFFmpegCameraHandler, Integer, byte[]> getHandler;
        private final RecordHandler recordHandler;

        private interface RecordHandler {
            void record(BaseFFmpegCameraHandler handler, String fileName, int time);
        }
    }
}
