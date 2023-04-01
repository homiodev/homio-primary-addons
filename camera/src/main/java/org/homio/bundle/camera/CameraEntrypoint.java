package org.homio.bundle.camera;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.bundle.api.BundleEntrypoint;
import org.homio.bundle.api.EntityContext;
import org.homio.bundle.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.bundle.api.video.ffmpeg.FFMPEG;
import org.homio.bundle.camera.scanner.OnvifCameraHttpScanner;
import org.homio.bundle.camera.setting.CameraAutorunIntervalSetting;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CameraEntrypoint implements BundleEntrypoint {

    private final EntityContext entityContext;

    @Override
    public int order() {
        return 300;
    }

    @SneakyThrows
    public void init() {
        entityContext.bgp().registerThreadsPuller("camera-ffmpeg", threadPuller -> {
            for (Map.Entry<String, FFMPEG> threadEntry : FFMPEG.ffmpegMap.entrySet()) {
                FFMPEG ffmpeg = threadEntry.getValue();
                if (ffmpeg.getIsAlive()) {
                    threadPuller.addThread(threadEntry.getKey(), ffmpeg.getDescription(), ffmpeg.getCreationDate(),
                        "working", null,
                        "Command: " + String.join(" ", ffmpeg.getCommandArrayList())
                    );
                }
            }
        });

        entityContext.event().runOnceOnInternetUp("scan-cameras", () -> {
            // fire rescan whole possible items to see if ip address has been changed
            entityContext.getBean(OnvifCameraHttpScanner.class).executeScan(entityContext, null, null, true);
        });

        for (BaseFFMPEGVideoStreamEntity cameraEntity : entityContext.findAll(BaseFFMPEGVideoStreamEntity.class)) {
            cameraEntity.getService().startOrStopService(cameraEntity);
        }

        entityContext.setting().listenValueAndGet(CameraAutorunIntervalSetting.class, "cam-autorun", interval -> {
            entityContext.bgp().builder("camera-schedule").cancelOnError(false)
                         .interval(Duration.ofMinutes(interval))
                         .execute(this::fireStartCamera);
        });

        entityContext.event().addEntityUpdateListener(BaseFFMPEGVideoStreamEntity.class, "video", entity -> {
            entity.getService().startOrStopService(entity);
        });
    }

    private void fireStartCamera() {
        for (BaseFFMPEGVideoStreamEntity cameraEntity : entityContext.findAll(BaseFFMPEGVideoStreamEntity.class)) {
            if (!cameraEntity.isStart() && cameraEntity.isAutoStart()) {
                entityContext.save(cameraEntity.setStart(true)); // start=true is a trigger to start camera
            }
        }
    }
}
