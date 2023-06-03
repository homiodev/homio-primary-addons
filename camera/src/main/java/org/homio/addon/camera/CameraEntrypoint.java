package org.homio.addon.camera;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.addon.camera.scanner.OnvifCameraHttpScanner;
import org.homio.addon.camera.setting.CameraAutorunIntervalSetting;
import org.homio.api.AddonEntrypoint;
import org.homio.api.EntityContext;
import org.homio.api.model.Icon;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.action.v1.item.UIInfoItemBuilder;
import org.homio.api.ui.field.action.v1.layout.dialog.UIStickyDialogItemBuilder;
import org.homio.api.video.BaseFFMPEGVideoStreamEntity;
import org.homio.api.video.ffmpeg.FFMPEG;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CameraEntrypoint implements AddonEntrypoint {

    private final EntityContext entityContext;

    @SneakyThrows
    public void init() {
        entityContext.ui().addNotificationBlockOptional("CAM", "CAM", new Icon("fas fa-video", "#367387"));
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

    public static void updateCamera(
        @NotNull EntityContext entityContext, BaseFFMPEGVideoStreamEntity entity,
        @Nullable Supplier<String> titleSupplier,
        @NotNull Icon icon,
        @Nullable Consumer<UIStickyDialogItemBuilder> settingsBuilder) {
        entityContext.ui().updateNotificationBlock("CAM", builder -> {
            builder.addFlexAction(entity.getEntityID(), flex -> {
                String text = titleSupplier == null ? entity.getTitle() : titleSupplier.get();
                UIInfoItemBuilder cameraInfo = flex.addInfo(text).setIcon(icon).linkToEntity(entity);
                if (!entity.getStatus().isOnline()) {
                    cameraInfo.setColor(Color.RED);
                }

                if (!entity.isStart()) {
                    flex.addButton("play", new Icon("fas fa-play"), (ec, params) -> {
                        ec.save(entity.setStart(true));
                        return null;
                    });
                } else if (settingsBuilder != null) {
                    settingsBuilder.accept(flex.addStickyDialogButton("actions", new Icon("fas fa-ellipsis-vertical")).up());
                }
            });
        });
    }
}
