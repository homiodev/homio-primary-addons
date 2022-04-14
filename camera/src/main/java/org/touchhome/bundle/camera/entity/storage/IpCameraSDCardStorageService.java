package org.touchhome.bundle.camera.entity.storage;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.entity.DeviceBaseEntity;
import org.touchhome.bundle.api.entity.RestartHandlerOnChange;
import org.touchhome.bundle.api.entity.storage.CameraBaseStorageService;
import org.touchhome.bundle.api.ui.UISidebarChildren;
import org.touchhome.bundle.api.ui.field.UIField;
import org.touchhome.bundle.api.ui.field.UIFieldType;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.entity.BaseFFmpegStreamEntity;
import org.touchhome.bundle.camera.ffmpeg.Ffmpeg;
import org.touchhome.bundle.camera.handler.BaseFFmpegCameraHandler;
import org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants;
import org.touchhome.common.util.CommonUtils;

import javax.persistence.Entity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Setter
@Getter
@Entity
@UISidebarChildren(icon = "rest/bundle/image/camera/memory-card.png", color = "#AACC00")
public class IpCameraSDCardStorageService extends CameraBaseStorageService<IpCameraSDCardStorageService> {

    public static final String PREFIX = "ipcsd_";

    private static Map<String, Ffmpeg> ffmpegServices = new HashMap<>();

    @UIField(order = 11)
    @RestartHandlerOnChange
    public int getSegmentTime() {
        return getJsonData("st", 300);
    }

    public IpCameraSDCardStorageService setSegmentTime(int value) {
        return setJsonData("st", value);
    }

    @UIField(order = 20)
    @RestartHandlerOnChange
    public int getMaxSegments() {
        return getJsonData("ms", 10);
    }

    public IpCameraSDCardStorageService setMaxSegments(int maxClips) {
        return setJsonData("ms", maxClips);
    }

    @UIField(order = 30)
    @RestartHandlerOnChange
    public MuxerType getMuxerType() {
        return getJsonDataEnum("mt", MuxerType.segments);
    }

    public IpCameraSDCardStorageService setMuxerType(MuxerType value) {
        setJsonDataEnum("mt", value);
        return this;
    }

    /**
     * Advanced settings
     */
    @UIField(order = 400, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public String getVideoCodec() {
        return getJsonData("vc", "copy");
    }

    public IpCameraSDCardStorageService setVideoCodec(String value) {
        setJsonData("vc", value);
        return this;
    }

    @UIField(order = 401, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public String getAudioCodec() {
        return getJsonData("ac", "copy");
    }

    public IpCameraSDCardStorageService setAudioCodec(String value) {
        setJsonData("ac", value);
        return this;
    }

    @UIField(order = 402, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public String getSelectStream() {
        return getJsonData("map", "0");
    }

    public IpCameraSDCardStorageService setSelectStream(String value) {
        setJsonData("map", value);
        return this;
    }

    @UIField(order = 1000, onlyEdit = true, advanced = true, type = UIFieldType.Chips)
    @RestartHandlerOnChange
    public List<String> getExtraOptions() {
        return getJsonDataList("eo");
    }

    public void setExtraOptions(String value) {
        setJsonData("eo", value);
    }

    @UIField(order = 402, onlyEdit = true, advanced = true)
    @RestartHandlerOnChange
    public boolean getVerbose() {
        return getJsonData("vb", false);
    }

    public IpCameraSDCardStorageService setVerbose(boolean value) {
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
        if (!(deviceEntity instanceof BaseFFmpegStreamEntity)) {
            throw new IllegalArgumentException("Unable to start video record for non ffmpeg compatible source");
        }

        if (getMuxerType() == MuxerType.hls && !output.endsWith(".m3u8")) {
            throw new IllegalArgumentException("To record to hls output need set file extension as .m3u8");
        }

        BaseFFmpegStreamEntity cameraEntity = (BaseFFmpegStreamEntity) deviceEntity;
        BaseFFmpegCameraHandler cameraHandler = cameraEntity.getCameraHandler();

        Ffmpeg.FFmpegHandler ffmpegHandler = new Ffmpeg.FFmpegHandler() {
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
                log.error("Record error: <{}>", error);
            }
        };
        String target = buildOutput(output);
        Path path = Paths.get(target);
        if (!path.isAbsolute()) {
            path = cameraEntity.getFolder(profile).resolve("ffmpeg").resolve(target);
        }
        Path folder = path.getParent();
        CommonUtils.createDirectoriesIfNotExists(folder);

        String source = cameraHandler.getRtspUri(profile);
        log.info("Start ffmpeg video recording from source: <{}> to: <{}>", source, path);
        Ffmpeg ffmpeg = new Ffmpeg("FFmpegLoopRecord", "FFMPEG loop record", ffmpegHandler, log,
                IpCameraBindingConstants.FFmpegFormat.RECORD, cameraHandler.getFfmpegLocation(),
                getVerbose() ? "" : "-hide_banner -loglevel warning", source,
                buildFFMPEGRecordCommand(folder), path.toString(),
                cameraEntity.getUser(), cameraEntity.getPassword().asString(), null);
        ffmpegServices.put(id, ffmpeg);
        ffmpeg.startConverting();
    }

    @Override
    public void stopRecord(String id, String output, DeviceBaseEntity cameraEntity) {
        Ffmpeg ffmpeg = ffmpegServices.remove(id);
        if (ffmpeg != null) {
            ffmpeg.stopConverting();
        }
    }

    public enum MuxerType {
        segments, hls
    }

    private String buildOutput(String output) {
        if (getMuxerType() == MuxerType.hls) {
            return output.endsWith(".m3u8") ? output : output + ".m3u8";
        }
        return output.contains("-%03d.mp4") ? output : output + "-%03d.mp4";
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
            options.add("-segment_list " + folder.resolve("playlist.m3u8").toString());
        }

        return String.join(" ", options);
    }
}
