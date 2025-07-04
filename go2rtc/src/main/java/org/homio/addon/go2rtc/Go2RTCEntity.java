package org.homio.addon.go2rtc;

import static org.homio.api.util.Constants.PRIMARY_DEVICE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Entity;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SystemUtils;
import org.homio.api.Context;
import org.homio.api.entity.CreateSingleEntity;
import org.homio.api.entity.device.DeviceBaseEntity;
import org.homio.api.entity.device.DeviceEndpointsBehaviourContractStub;
import org.homio.api.entity.log.HasEntityLog;
import org.homio.api.entity.version.HasGitHubFirmwareVersion;
import org.homio.api.model.*;
import org.homio.api.model.endpoint.BaseDeviceEndpoint;
import org.homio.api.model.endpoint.DeviceEndpoint;
import org.homio.api.repository.GitHubProject;
import org.homio.api.service.EntityService;
import org.homio.api.state.DecimalType;
import org.homio.api.ui.UI.Color;
import org.homio.api.ui.field.UIField;
import org.homio.api.ui.field.UIFieldGroup;
import org.homio.api.ui.field.UIFieldIgnore;
import org.homio.api.ui.field.action.UIContextMenuAction;
import org.homio.api.ui.field.action.v1.UIInputBuilder;
import org.homio.api.ui.route.UIRouteMedia;
import org.homio.api.util.CommonUtils;
import org.homio.hquery.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Entity
@CreateSingleEntity
@UIRouteMedia(icon = "fab fa-eye", color = "#3DC4B1", allowCreateItem = false)
public class Go2RTCEntity extends DeviceBaseEntity implements HasEntityLog,
        HasGitHubFirmwareVersion, EntityService<Go2RTCService>,
        DeviceEndpointsBehaviourContractStub {

    public static final GitHubProject go2rtcGitHub =
            GitHubProject.of("AlexxIT", "go2rtc")
                    .setInstalledVersionResolver((context, gitHubProject) -> {
                        // somehow --version param exists only in windows
                        if (SystemUtils.IS_OS_WINDOWS) {
                            Path executable = CommonUtils.getInstallPath().resolve("go2rtc").resolve("go2rtc");
                            String version = context.hardware().execute(executable + " --version", "V?");
                            if (version != null && version.startsWith("Current version: ")) {
                                return "v" + version.substring("Current version: ".length()).trim();
                            }
                            return version;
                        }
                        return "V?";
                    }).setLinuxExecutableAsset("go2rtc");

    public static Go2RTCEntity getEntity(Context context) {
        return context.db().get(Go2RTCEntity.class, PRIMARY_DEVICE);
    }

    @Override
    public String getDescriptionImpl() {
        return "CAMERA.GO2RTC_DESCRIPTION";
    }

    @Override
    public String toString() {
        return "Go2RTC" + getTitle();
    }

    @Override
    public String getDefaultName() {
        return "Go2RTC server";
    }

    @Override
    public void logBuilder(@NotNull EntityLogBuilder builder) {
        builder.addTopic(Go2RTCService.class);
    }

    @UIField(order = 1, inlineEdit = true)
    @UIFieldGroup("GENERAL")
    public boolean isStart() {
        return getJsonData("start", false);
    }

    public void setStart(boolean start) {
        setJsonData("start", start);
    }

    @Override
    public @NotNull ActionResponseModel update(@NotNull ProgressBar progressBar, @NotNull String version) {
        return getService().updateFirmware(progressBar, version);
    }

    @Override
    public @NotNull GitHubProject getGitHubProject() {
        return go2rtcGitHub;
    }

    @Override
    @JsonIgnore
    @UIFieldIgnore
    public String getName() {
        return super.getName();
    }

    @Override
    public Go2RTCService createService(@NotNull Context context) {
        return new Go2RTCService(context, this);
    }

    @Override
    public long getEntityServiceHashCode() {
        return getJsonDataHashCode("api", "rtsp", "webrtc");
    }

  @UIField(order = 1, hideInEdit = true, color = "#C4CC23")
  @UIFieldGroup("STATUS")
  public int getConnectedStreamsCount() {
    if (!getStatus().isOnline()) {
      return -1;
    }
    return optService().stream()
        .map(
            s -> {
              try {
                return s.getApiListStreams().size();
              } catch (Exception ignored) {
                return -1;
              }
            })
        .findAny()
        .orElse(-1);
  }

    @UIField(order = 30, hideInEdit = true)
    @UIFieldGroup("STATUS")
    public boolean isRunningLocally() {
        return optService().map(Go2RTCService::isRunningLocally).orElse(false);
    }

    @UIField(order = 31, hideInEdit = true)
    @UIFieldGroup("STATUS")
    public WebAddress getHost() {
        return new WebAddress("localhost:%s".formatted(getApiPort()), null, new Icon("fas fa-code", "#31623D"));
    }

    @UIField(order = 200)
    @UIFieldGroup("CONFIGURATION")
    public int getApiPort() {
        return getJsonData("api", 1984);
    }

    public void setApiPort(int port) {
        setJsonData("api", port);
    }

    @UIField(order = 210)
    @UIFieldGroup("CONFIGURATION")
    public int getRtspPort() {
        return getJsonData("rtsp", 8554);
    }

    public void setRtspPort(int port) {
        setJsonData("rtsp", port);
    }

    @UIField(order = 220)
    @UIFieldGroup("CONFIGURATION")
    public int getWebRtcPort() {
        return getJsonData("webrtc", 8555);
    }

    public void setWebRtcPort(int port) {
        setJsonData("webrtc", port);
    }

    @Override
    protected @NotNull String getDevicePrefix() {
        return "go2rtc";
    }

    @Override
    public void assembleActions(UIInputBuilder uiInputBuilder) {

    }

    @SneakyThrows
    @UIContextMenuAction(value = "EDIT_CONFIG",
            icon = "fas fa-keyboard",
            iconColor = "#899343")
    public ActionResponseModel editConfig() {
        String content = Files.readString(getService().getConfigurationPath());
        return ActionResponseModel.showFile(new FileModel("go2rtc.yaml", content, FileContentType.yaml)
                .setSaveHandler(mc -> getService().updateConfiguration(mc)));
    }

    @SneakyThrows
    @UIContextMenuAction(value = "RESET_CONFIG",
            confirmMessage = "RESET_CONFIG",
            confirmMessageDialogColor = Color.ERROR_DIALOG,
            icon = "fas fa-clock-rotate-left",
            iconColor = "#91293E")
    public ActionResponseModel resetConfiguration() {
        getService().resetConfiguration();
        return ActionResponseModel.success();
    }

    @Override
    @SneakyThrows
    public @NotNull ActionResponseModel handleTextFieldAction(
            @NotNull String field,
            @NotNull JSONObject metadata) {
        if (metadata.optString("key", "").equals("config")) {
            return editConfig();
        }
        return ActionResponseModel.showError("W.ERROR.NO_HANDLER");
    }

    @UIContextMenuAction(value = "GET_LIST",
            icon = "fab fa-quinscape",
            iconColor = "#899343")
    public ActionResponseModel apiGetList() {
        return ActionResponseModel.showJson("GET_LIST", getService().getApiList());
    }

    @Override
    public @NotNull Map<String, ? extends DeviceEndpoint> getDeviceEndpoints() {
        Map<String, StreamEndpoint> streams = new HashMap<>();
        for (Entry<String, JsonNode> entry : getService().getApiListStreams().entrySet()) {
            streams.put(entry.getKey(), new StreamEndpoint(entry.getKey(), entry.getValue(), this));
        }
        return streams;
    }

    @Override
    @UIFieldIgnore
    public @Nullable String getImageIdentifier() {
        return super.getImageIdentifier();
    }

    @Override
    @JsonIgnore
    @UIFieldIgnore
    public @Nullable String getIeeeAddress() {
        return getEntityID();
    }

    public static class StreamEndpoint extends BaseDeviceEndpoint<Go2RTCEntity> {

        private final @Getter String description;
        private final JsonNode node;

        public StreamEndpoint(String key, JsonNode node, Go2RTCEntity entity) {
            super(new Icon(), "GO2RTC", entity.context(), entity, key, false, EndpointType.trigger);
            this.node = node;
            setIcon(createIcon(node.get("producers").path(0).path("url").asText()));

            setValue(new DecimalType(node.get("consumers").size(), 0), false);
            this.description = node.get("producers").path(0).path("url").asText();
        }

        private static Icon createIcon(String sourceType) {
            if (sourceType.startsWith("rtsp://")) {
                return new Icon("fas fa-blog", "#399AAA");
            }
            return new Icon("fas fa-film", "#767873");
        }

        @Override
        public UIInputBuilder createTriggerActionBuilder(@NotNull UIInputBuilder uiInputBuilder) {
            uiInputBuilder.addButton(getEntityID(), null, (context, params) ->
                            ActionResponseModel.showJson("TITLE.NODE_INFO", node))
                    .setText(getValue().toString());
            return uiInputBuilder;
        }

        @Override
        public @NotNull String getName(boolean shortFormat) {
            return "/" + getEndpointEntityID();
        }

        @Override
        public String getVariableGroupID() {
            return context().var().createGroup("go2rtc-" + getDeviceID(), "Go2RTC", builder ->
                    builder.setIcon(new Icon("fas fa-eye", "#3DC4B1")));
        }
    }
}
