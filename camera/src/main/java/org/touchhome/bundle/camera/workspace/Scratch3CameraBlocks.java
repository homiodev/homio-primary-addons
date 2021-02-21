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
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
@Getter
@Component
public class Scratch3CameraBlocks extends Scratch3ExtensionBlocks {

    protected static final String VIDEO_STREAM = "VIDEO_STREAM";
    private static final String ON_OFF = "ON_OFF";
    private static final String RECORD_TYPE = "RECORD_TYPE";

    private final BroadcastLockManager broadcastLockManager;

    private final MenuBlock.ServerMenuBlock ffmpegCameraMenu;
    private final MenuBlock.StaticMenuBlock<OnOffType> onOffMenu;
    private final MenuBlock.StaticMenuBlock<RecordType> recordTypeMenu;

    private final Scratch3Block whenMotionAlarmHat;
    private final Scratch3Block lastMotionTypeReporter;
    private final Scratch3Block fireMotionAlarmCommand;
    private final Scratch3Block fireAudioAlarmCommand;
    private final Scratch3Block recordImageCommand;
    private final Scratch3Block imageReporter;
    private final Scratch3Block recordGifOrMP4Command;
    private final Scratch3Block gifMP4Reporter;
    private final Scratch3Block setFFmpegMotionAlarmThresholdCommand;
    private final Scratch3Block whenAudioAlarmHat;
    private final Scratch3Block audioAlarmReporter;
    private final Scratch3Block motionAlarmReporter;
    private final Scratch3Block setAudioAlarmThresholdCommand;
    private final Scratch3Block lastImageReporter;

    public Scratch3CameraBlocks(EntityContext entityContext, BroadcastLockManager broadcastLockManager,
                                CameraEntryPoint cameraEntryPoint) {
        super("#8F4D77", entityContext, cameraEntryPoint, null);
        this.broadcastLockManager = broadcastLockManager;

        // Menu
        this.ffmpegCameraMenu = MenuBlock.ofServerItems("ffmpegCameraMenu", BaseFFmpegStreamEntity.class);
        this.onOffMenu = MenuBlock.ofStatic("onOff", OnOffType.class, OnOffType.ON);
        this.recordTypeMenu = MenuBlock.ofStatic("recordType", RecordType.class, RecordType.Mp4);

        // Hats
        this.whenMotionAlarmHat = withServerFfmpegAndOnOff(Scratch3Block.ofHandler(10, "motion_alarm",
                BlockType.hat, "Motion alarm [ON_OFF] of [VIDEO_STREAM]", this::listenMotionAlarmHat));

        this.whenAudioAlarmHat = withServerFfmpegAndOnOff(Scratch3Block.ofHandler(20, "audio_alarm",
                BlockType.hat, "Audio alarm [ON_OFF] of [VIDEO_STREAM]", this::listenAudioAlarmHat));
        this.whenAudioAlarmHat.appendSpace();

        // reporters
        this.audioAlarmReporter = withServerFfmpeg(Scratch3Block.ofEvaluate(30, "get_audio_alarm", BlockType.reporter,
                "Get audio alarm threshold of [VIDEO_STREAM]", this::getAudioAlarmReporter));

        this.motionAlarmReporter = withServerFfmpeg(Scratch3Block.ofEvaluate(40, "get_motion_alarm", BlockType.reporter,
                "Get motion alarm threshold of [VIDEO_STREAM]", this::getMotionAlarmReporter));

        this.lastMotionTypeReporter = withServerFfmpeg(Scratch3Block.ofEvaluate(90, "last_motion_type", BlockType.reporter,
                "Get last motion type of [VIDEO_STREAM]", this::getLastMotionTypeReporter));

        this.lastImageReporter = withServerFfmpeg(Scratch3Block.ofEvaluate(95, "last_image", BlockType.reporter,
                "Get last image of [VIDEO_STREAM]", this::getLastImageReporter));

        this.imageReporter = withServerFfmpeg(Scratch3Block.ofEvaluate(100, "get_image", BlockType.reporter,
                "Get image of [VIDEO_STREAM]", this::getImageReporter));

        this.gifMP4Reporter = withServerFfmpeg(Scratch3Block.ofEvaluate(110, "get_gif_mp4", BlockType.reporter,
                "Get [RECORD_TYPE] [TIME]sec. of [VIDEO_STREAM]", this::getGifMP4Reporter));
        this.gifMP4Reporter.addArgument("TIME", ArgumentType.number, "5");
        this.gifMP4Reporter.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.gifMP4Reporter.appendSpace();

        // commands
        this.fireMotionAlarmCommand = withServerFfmpegAndOnOff(Scratch3Block.ofHandler(120, "fire_motion_alarm",
                BlockType.command, "Fire motion alarm [ON_OFF] of [VIDEO_STREAM]", this::fireMotionAlarmCommand));

        this.fireAudioAlarmCommand = withServerFfmpegAndOnOff(Scratch3Block.ofHandler(130, "fire_audio_alarm",
                BlockType.command, "Fire audio alarm [ON_OFF] of [VIDEO_STREAM]", this::fireAudioAlarmCommand));

        this.recordImageCommand = withServerFfmpeg(Scratch3Block.ofHandler(140, "record_image",
                BlockType.command, "Record image of [VIDEO_STREAM]", this::firePollImageCommand));

        this.recordGifOrMP4Command = withServerFfmpeg(Scratch3Block.ofHandler(150, "record_gif_mp4",
                BlockType.command, "Record [RECORD_TYPE] [TIME]sec. to file [FILE_NAME] of [VIDEO_STREAM]",
                this::fireRecordGifMP4Command));
        this.recordGifOrMP4Command.addArgument(RECORD_TYPE, this.recordTypeMenu);
        this.recordGifOrMP4Command.addArgument("TIME", ArgumentType.number, "5");
        this.recordGifOrMP4Command.addArgument("FILE_NAME", ArgumentType.string, "name");

        this.setAudioAlarmThresholdCommand = withServerFfmpegAndValue(Scratch3Block.ofHandler(160, "set_audio_alarm",
                BlockType.command, "Set Audio alarm as [VALUE] of [VIDEO_STREAM]", this::fireSetAudioAlarmCommand), 35);

        this.setFFmpegMotionAlarmThresholdCommand = withServerFfmpegAndValue(Scratch3Block.ofHandler(170, "set_ffmpeg_motion_alarm",
                BlockType.command, "Set FFmpeg motion alarm as [VALUE]% of [VIDEO_STREAM]", this::fireSetFFmpegMotionAlarmThresholdCommand), 35);
        this.setFFmpegMotionAlarmThresholdCommand.appendSpace();
    }

    private RawType getLastImageReporter(WorkspaceBlock workspaceBlock) {
        return new RawType(getEntity(workspaceBlock).getLastSnapshot(), MimeTypeUtils.IMAGE_JPEG_VALUE);
    }

    private int getAudioAlarmReporter(WorkspaceBlock workspaceBlock) {
        return getEntity(workspaceBlock).getAudioThreshold();
    }

    private int getMotionAlarmReporter(WorkspaceBlock workspaceBlock) {
        return getEntity(workspaceBlock).getMotionThreshold();
    }

    private void fireSetAudioAlarmCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).setAudioAlarmThreshold(workspaceBlock.getInputString(VALUE));
    }

    private void fireSetFFmpegMotionAlarmThresholdCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).setFfmpegMotionControl(workspaceBlock.getInputString(VALUE));
    }

    private byte[] getGifMP4Reporter(WorkspaceBlock workspaceBlock) {
        return workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).getHandler
                .apply(getFFmpegHandler(workspaceBlock),
                        workspaceBlock.getInputInteger("TIME"));
    }

    private void fireRecordGifMP4Command(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getMenuValue(RECORD_TYPE, this.recordTypeMenu).recordHandler
                .record(getFFmpegHandler(workspaceBlock),
                        workspaceBlock.getInputString("FILE_NAME"),
                        workspaceBlock.getInputInteger("TIME"));
    }

    private byte[] getImageReporter(WorkspaceBlock workspaceBlock) {
        return getFFmpegHandler(workspaceBlock).recordImageSync();
    }

    private void firePollImageCommand(WorkspaceBlock workspaceBlock) {
        getFFmpegHandler(workspaceBlock).startSnapshot();
    }

    private void fireAudioAlarmCommand(WorkspaceBlock workspaceBlock) {
        OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        getFFmpegHandler(workspaceBlock).audioDetected(onOffType.boolValue());
    }

    private void fireMotionAlarmCommand(WorkspaceBlock workspaceBlock) {
        OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        getFFmpegHandler(workspaceBlock).motionDetected(onOffType.boolValue(), CHANNEL_EXTERNAL_MOTION);
    }

    private String getLastMotionTypeReporter(WorkspaceBlock workspaceBlock) {
        return getCameraStringAttribute(workspaceBlock, CHANNEL_LAST_MOTION_TYPE);
    }

    private void listenMotionAlarmHat(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getNextOrThrow();
        BaseFFmpegStreamEntity entity = getEntity(workspaceBlock);
        if (!entity.getCameraHandler().setFfmpegMotionControl("ON")) {
            workspaceBlock.logErrorAndThrow("Unable to start motion detection");
        }
        OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        BroadcastLock motionAlarm = broadcastLockManager.getOrCreateLock(workspaceBlock, MOTION_ALARM + ":" + entity.getEntityID());
        workspaceBlock.subscribeToLock(motionAlarm, (Function<OnOffType, Boolean>) state -> state.boolValue() == onOffType.boolValue());
    }

    private void listenAudioAlarmHat(WorkspaceBlock workspaceBlock) {
        workspaceBlock.getNextOrThrow();
        BaseFFmpegStreamEntity entity = getEntity(workspaceBlock);
        if (!entity.getCameraHandler().setAudioAlarmThreshold("ON")) {
            workspaceBlock.logErrorAndThrow("Unable to start motion detection");
        }
        OnOffType onOffType = workspaceBlock.getMenuValue(ON_OFF, this.onOffMenu);
        BroadcastLock audioAlarm = broadcastLockManager.getOrCreateLock(workspaceBlock, CHANNEL_AUDIO_ALARM + ":" + entity.getEntityID());
        workspaceBlock.subscribeToLock(audioAlarm, (Function<OnOffType, Boolean>) state -> state.boolValue() == onOffType.boolValue());
    }

    private <T extends BaseFFmpegStreamEntity> T getEntity(WorkspaceBlock workspaceBlock) {
        BaseFFmpegStreamEntity entity = workspaceBlock.getMenuValueEntity(VIDEO_STREAM, this.ffmpegCameraMenu);
        if (entity == null) {
            throw new RuntimeException("Video camera not exists");
        }
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
