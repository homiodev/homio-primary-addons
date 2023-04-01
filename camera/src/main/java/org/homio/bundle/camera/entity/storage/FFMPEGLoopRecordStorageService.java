package org.homio.bundle.camera.entity.storage;

import static org.homio.bundle.api.video.ffmpeg.FFMPEGFormat.RECORD;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.bundle.api.entity.DeviceBaseEntity;
import org.homio.bundle.api.entity.RestartHandlerOnChange;
import org.homio.bundle.api.entity.storage.VideoBaseStorageService;
import org.homio.bundle.api.ui.UISidebarChildren;
import org.homio.bundle.api.ui.field.UIField;
import org.homio.bundle.api.ui.field.UIFieldType;
import org.homio.bundle.api.util.CommonUtils;
import org.homio.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.bundle.api.video.BaseVideoService;
import org.homio.bundle.api.video.ffmpeg.FFMPEG;

@Log4j2
@Entity
@UISidebarChildren(icon = "rest/bundle/image/camera/rpi-loop-record.png", color = "#0088CC")
public class FFMPEGLoopRecordStorageService extends VideoBaseStorageService<FFMPEGLoopRecordStorageService> {

    public static final String PREFIX = "ffmpeglr_";

    private static Map<String, FFMPEG> ffmpegServices = new HashMap<>();

    @UIField(order = 11)
    @RestartHandlerOnChange
    public int getSegmentTime() {
        return getJsonData("st", 300);
    }

    public void setSegmentTime(int value) {
        setJsonData("st", value);
    }

    @UIField(order = 20)
    @RestartHandlerOnChange
    public int getMaxSegments() {
        return getJsonData("ms", 10);
    }

    public void setMaxSegments(int maxClips) {
        setJsonData("ms", maxClips);
    }

    @UIField(order = 30)
    @RestartHandlerOnChange
    public MuxerType getMuxerType() {
        return getJsonDataEnum("mt", MuxerType.segments);
    }

    public FFMPEGLoopRecordStorageService setMuxerType(MuxerType value) {
        setJsonDataEnum("mt", value);
        return this;
    }

    /**
     * Advanced settings
     */
    @UIField(order = 400, hideInView = true)
    @RestartHandlerOnChange
    public String getVideoCodec() {
        return getJsonData("vc", "copy");
    }

    public FFMPEGLoopRecordStorageService setVideoCodec(String value) {
        setJsonData("vc", value);
        return this;
    }

    @UIField(order = 401, hideInView = true)
    @RestartHandlerOnChange
    public String getAudioCodec() {
        return getJsonData("ac", "copy");
    }

    public FFMPEGLoopRecordStorageService setAudioCodec(String value) {
        setJsonData("ac", value);
        return this;
    }

    @UIField(order = 402, hideInView = true)
    @RestartHandlerOnChange
    public String getSelectStream() {
        return getJsonData("map", "0");
    }

    public FFMPEGLoopRecordStorageService setSelectStream(String value) {
        setJsonData("map", value);
        return this;
    }

    @UIField(order = 1000, hideInView = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getExtraOptions() {
        return getJsonDataList("eo");
    }

    public void setExtraOptions(String value) {
        setJsonData("eo", value);
    }

    @UIField(order = 402, hideInView = true)
    @RestartHandlerOnChange
    public boolean getVerbose() {
        return getJsonData("vb", false);
    }

    public FFMPEGLoopRecordStorageService setVerbose(boolean value) {
        setJsonData("vb", value);
        return this;
    }

    @Override
    public String getEntityPrefix() {
        return PREFIX;
    }

    @Override
    public String getDefaultName() {
        return "FFMPEG FS loop storage (" + getMuxerType() + ")";
    }

    @SneakyThrows
    @Override
    public void startRecord(String id, String output, String profile, DeviceBaseEntity deviceEntity) {
        stopRecord(id, output, deviceEntity);
        if (!(deviceEntity instanceof BaseFFMPEGVideoStreamEntity)) {
            throw new IllegalArgumentException("Unable to start video record for non ffmpeg compatible source");
        }

        if (getMuxerType() == MuxerType.hls && !output.endsWith(".m3u8")) {
            throw new IllegalArgumentException("To record to hls output need set file extension as .m3u8");
        }

        BaseFFMPEGVideoStreamEntity videoStreamEntity = (BaseFFMPEGVideoStreamEntity) deviceEntity;
        BaseVideoService service = videoStreamEntity.getService();

        FFMPEG.FFMPEGHandler ffmpegHandler = new FFMPEG.FFMPEGHandler() {
            @Override
            public String getEntityID() {
                return deviceEntity.getEntityID();
            }

            @Override
            public void motionDetected(boolean on, String key) {

            }

            @Override
            public void audioDetected(boolean on) {

            }

            @Override
            public void ffmpegError(String error) {
                log.error("[{}]: Record error: <{}>", getEntityID(), error);
            }
        };
        String target = buildOutput(output);
        Path path = Paths.get(target);
        if (!path.isAbsolute()) {
            path = CommonUtils.getMediaPath().resolve(videoStreamEntity.getFolderName() + "_" + profile)
                              .resolve("ffmpeg").resolve(target);
        }
        Path folder = path.getParent();
        CommonUtils.createDirectoriesIfNotExists(folder);

        String source = service.getRtspUri(profile);
        log.info("[{}]: Start ffmpeg video recording from source: <{}> to: <{}>", getEntityID(), source, path);
        FFMPEG ffmpeg = new FFMPEG(getEntityID(), "FFMPEG loop record", ffmpegHandler, log,
            RECORD, getVerbose() ? "" : "-hide_banner -loglevel warning", source,
            buildFFMPEGRecordCommand(folder), path.toString(),
            videoStreamEntity.getUser(), videoStreamEntity.getPassword().asString(), null);
        ffmpegServices.put(id, ffmpeg);
        ffmpeg.startConverting();
    }

    @Override
    public void stopRecord(String id, String output, DeviceBaseEntity cameraEntity) {
        FFMPEG ffmpeg = ffmpegServices.remove(id);
        if (ffmpeg != null) {
            ffmpeg.stopConverting();
        }
    }

    public String buildFFMPEGRecordCommand(Path folder) {
        List<String> options = new ArrayList<>();
        options.add("-vcodec " + getVideoCodec());
        options.add("-acodec " + getAudioCodec());
        options.add("-map " + getSelectStream());
        options.addAll(getExtraOptions());

        if (getMuxerType() == MuxerType.hls) {
            options.add("-f hls");
            options.add("-hls_time " + getSegmentTime());
            options.add("-hls_list_size " + getMaxSegments());
            options.add("-hls_flags delete_segments");
            options.add("-reset_timestamps 1");
        } else {
            options.add("-f segment");
            options.add("-segment_time " + getSegmentTime());
            options.add("-write_empty_segments 1");
            options.add("-segment_list_size " + getMaxSegments());
            options.add("-segment_wrap " + getMaxSegments());
            options.add("-segment_format mp4");

            options.add("-segment_list_type m3u8");
            options.add("-segment_list " + folder.resolve("playlist.m3u8"));
        }

        return String.join(" ", options);
    }

    private String buildOutput(String output) {
        if (getMuxerType() == MuxerType.hls) {
            return output.endsWith(".m3u8") ? output : output + ".m3u8";
        }
        return output.contains("-%03d.mp4") ? output : output + "-%03d.mp4";
    }

    public enum MuxerType {
        segments, hls
    }
}
