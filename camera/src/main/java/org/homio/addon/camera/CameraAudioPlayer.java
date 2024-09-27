package org.homio.addon.camera;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.camera.service.IpCameraService;
import org.homio.api.model.UpdatableValue;
import org.homio.api.stream.ContentStream;
import org.homio.api.stream.audio.AudioPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.onvif.ver10.schema.AudioOutputConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.String.format;

@Log4j2
@RequiredArgsConstructor
public class CameraAudioPlayer implements AudioPlayer {
    @NotNull
    private final IpCameraService service;
    @NotNull
    private final AudioOutputConfiguration configuration;
    @Getter
    @NotNull
    private final UpdatableValue<Boolean> available = UpdatableValue.wrap(false, "camera");

    @Override
    public String getId() {
        return "camera-" + service.getEntityID();
    }

    @Override
    public @NotNull String getLabel() {
        return "CAMERA_SOURCES";
    }

    // TODO: not tested yet
    @Override
    public void play(@NotNull ContentStream audioStream, @Nullable Integer startFrame, @Nullable Integer endFrame) throws Exception {
        String rtspServerAddress = configuration.getOutputToken();
        String ffmpegCommand = format("ffmpeg -re -i - -acodec pcm_mulaw -f rtp %s", rtspServerAddress);
        Process ffmpegProcess = Runtime.getRuntime().exec(ffmpegCommand);
        try (OutputStream ffmpegInput = ffmpegProcess.getOutputStream();
             InputStream inputStream = audioStream.getResource().getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Continuously read from the unknown audio stream and write it to FFmpeg's stdin
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                ffmpegInput.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Failed to play audio", e);
        }
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void setVolume(int volume) {

    }

    @Override
    public boolean isAvailable() {
        return available.getValue();
    }
}
