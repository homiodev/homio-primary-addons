package org.homio.addon.go2rtc;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.homio.api.AddonEntrypoint;
import org.homio.api.Context;
import org.homio.api.ContextMediaVideo;
import org.homio.api.Unregistered;
import org.homio.api.model.OptionModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class Go2RTCEntrypoint implements AddonEntrypoint {

  private final Context context;
  private final List<Unregistered> providerUnregistered = new ArrayList<>();

  @SneakyThrows
  public void init() {
    var entity = Go2RTCEntity.getEntity(context);
    context.event().addEntityStatusUpdateListener(entity.getEntityID(), "go2rtc", baseEntityIdentifier -> {
      if (entity.getStatus().isOnline()) {
        providerUnregistered.add(context.media().video().addVideoWebRTCProvider("go2rtc", entity.getWebRtcPort()));
        providerUnregistered.add(context.media().video().addVideoSourceInfoListener("go2rtc", new ContextMediaVideo.VideoSourceInfoListener() {
          @Override
          public void addVideoSourceInfo(String path, Map<String, OptionModel> videoSources) {
            entity.getService().addSourceInfo(path, videoSources);
          }

          @Override
          public void removeVideoSourceInfo(String path) {
          }
        }));
        providerUnregistered.add(context.media().video().addVideoSourceListener("go2rtc", new ContextMediaVideo.RegisterVideoSourceListener() {
          @Override
          public void addVideoSource(@NotNull String path, @NotNull String source) {
            entity.getService().addSource(path, source);
          }

          @Override
          public void removeVideoSource(@NotNull String path) {
            entity.getService().removeSource(path);
          }
        }));
      } else {
        for (Unregistered unregistered : providerUnregistered) {
          unregistered.unregister();
        }
        providerUnregistered.clear();
      }
    });
  }
}
