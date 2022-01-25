package org.touchhome.bundle.camera.onvif.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.entity.BaseEntity;
import org.touchhome.bundle.api.model.OptionModel;
import org.touchhome.bundle.api.state.JsonType;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.api.ui.action.DynamicOptionLoader;
import org.touchhome.bundle.api.ui.field.selection.UIFieldSelection;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.api.video.VideoPlaybackStorage;
import org.touchhome.bundle.camera.entity.BaseVideoCameraEntity;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.onvif.BrandCameraHasMotionAlarm;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;
import org.touchhome.bundle.camera.ui.UICameraSelectionAttributeValues;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
@CameraBrandHandler(name = "Reolink")
public class ReolinkBrandHandler extends BaseOnvifCameraBrandHandler implements
        BrandCameraHasMotionAlarm, VideoPlaybackStorage {

    private final RestTemplate restTemplate = new RestTemplate();
    private long tokenExpiration;
    private String token;

    public ReolinkBrandHandler(BaseVideoCameraEntity cameraEntity) {
        super(cameraEntity);
    }

    @Override
    public LinkedHashMap<Long, Boolean> getAvailableDaysPlaybacks(EntityContext entityContext, String profile, Date fromDate, Date toDate) {
        ReolinkBrandHandler reolinkBrandHandler = (ReolinkBrandHandler) cameraEntity.getBaseBrandCameraHandler();
        Root[] root = reolinkBrandHandler.firePost("?cmd=Search", true, Root[].class, new ReolinkBrandHandler.ReolinkCmd(1, "Search",
                new SearchRequest(new SearchRequest.Search(1, profile, Time.of(fromDate), Time.of(toDate)))));
        if (root[0].error != null) {
            throw new RuntimeException("Reolink error fetch days: " + root[0].error.detail + ". RspCode: " + root[0].error.rspCode);
        }
        LinkedHashMap<Long, Boolean> res = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        for (Status status : root[0].value.searchResult.status) {
            cal.set(status.year, status.mon - 1, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            for (String item : status.table.split("(?!^)")) {
                if (cal.getTimeInMillis() <= toDate.getTime()) {
                    res.put(cal.getTimeInMillis(), Integer.parseInt(item) == 1);
                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
                }
            }
        }
        return res;
    }

    @Override
    public List<PlaybackFile> getPlaybackFiles(EntityContext entityContext, String profile, Date from, Date to) {
        ReolinkBrandHandler reolinkBrandHandler = (ReolinkBrandHandler) cameraEntity.getBaseBrandCameraHandler();
        Root[] root = reolinkBrandHandler.firePost("?cmd=Search", true, Root[].class, new ReolinkBrandHandler.ReolinkCmd(1, "Search",
                new SearchRequest(new SearchRequest.Search(0, profile, Time.of(from), Time.of(to)))));
        if (root[0].error != null) {
            throw new RuntimeException("RspCode: " + root[0].error.rspCode + ". Details: " + root[0].error.detail);
        }
        List<File> file = root[0].value.searchResult.file;
        if (file == null) {
            throw new IllegalStateException("Unable to find playback files for date range: " + from + " - " + to);
        }
        return file.stream().map(File::toPlaybackFile).collect(Collectors.toList());
    }

    @Override
    public URI getPlaybackVideoURL(EntityContext entityContext, String fileId) throws URISyntaxException {
        Path path = TouchHomeUtils.TMP_FOLDER.resolve(fileId);
        if (Files.exists(path)) {
            return path.toUri();
        } else {
            ReolinkBrandHandler reolinkBrandHandler = (ReolinkBrandHandler) cameraEntity.getBaseBrandCameraHandler();
            String fullUrl = reolinkBrandHandler.getAuthUrl("?cmd=Download&source=" + fileId + "&output=" + fileId, true);
            return new URI(fullUrl);
        }
    }

    @Override
    public DownloadFile downloadPlaybackFile(EntityContext entityContext, String profile, String fileId, Path path) throws Exception {
        ReolinkBrandHandler reolinkBrandHandler = (ReolinkBrandHandler) cameraEntity.getBaseBrandCameraHandler();
        String fullUrl = reolinkBrandHandler.getAuthUrl("?cmd=Download&source=" + fileId + "&output=" + fileId, true);
        restTemplate.execute(fullUrl, HttpMethod.GET, null, clientHttpResponse -> {
            StreamUtils.copy(clientHttpResponse.getBody(), Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            return path;
        });

        return new DownloadFile(new UrlResource(path.toUri()), Files.size(path), fileId);
    }

    @UICameraAction(name = CHANNEL_AUTO_LED, order = 10, icon = "fas fa-lightbulb")
    public void autoLed(boolean on) {
        String state = on ? "Auto" : "Off";
        if (firePostGetCode("/cgi-bin/api.cgi?cmd=SetIrLights", true,
                new ReolinkCmd(0, "SetIrLights", "{\"IrLights\":{\"state\":\"" + state + "\"}}"))) {
            setAttribute(CHANNEL_AUTO_LED, OnOffType.of(on));
            entityContext.ui().sendSuccessMessage("Reolink set IR light applied successfully");
        }
    }

    @UICameraActionGetter(CHANNEL_AUTO_LED)
    public State isAutoLed() {
        return getAttribute(CHANNEL_AUTO_LED);
    }

    @UICameraActionGetter(CHANNEL_POSITION_NAME)
    public State getNamePosition() {
        return getOSDPosition("osdChannel");
    }

    @UICameraAction(name = CHANNEL_POSITION_NAME, order = 100, icon = "fas fa-sort-amount-down-alt", group = "VIDEO.OSD")
    @UICameraSelectionAttributeValues(value = "OsdRange", path = {"osdChannel", "pos"}, prependValues = {"", "Hide"})
    public void setNamePosition(String position) {
        setSetting("Osd", osd -> {
            if ("".equals(position)) {
                osd.set(false, "osdChannel", "enable");
            } else {
                osd.set(true, "osdChannel", "enable");
                osd.set(position, "osdChannel", "pos");
            }
        });
    }

    @UICameraAction(name = CHANNEL_POSITION_DATETIME, order = 101, icon = "fas fa-sort-numeric-up", group = "VIDEO.OSD")
    @UICameraSelectionAttributeValues(value = "OsdRange", path = {"osdTime", "pos"}, prependValues = {"", "Hide"})
    public void setDateTimePosition(String position) {
        setSetting("Osd", osd -> {
            if ("".equals(position)) {
                osd.set(false, "osdTime", "enable");
            } else {
                osd.set(true, "osdTime", "enable");
                osd.set(position, "osdTime", "pos");
            }
        });
    }

    @UICameraActionGetter(CHANNEL_POSITION_DATETIME)
    public State getDateTimePosition() {
        return getOSDPosition("osdTime");
    }

    @UICameraAction(name = CHANNEL_SHOW_WATERMARK, order = 102, icon = "fas fa-copyright", group = "VIDEO.OSD")
    public void setShowWatermark(boolean on) {
        setSetting("Osd", osd -> osd.set(boolToInt(on), "watermark"));
    }

    @UICameraActionGetter(CHANNEL_SHOW_WATERMARK)
    public State getShowWatermark() {
        JsonType osd = (JsonType) getAttribute("Osd");
        return osd == null ? null : OnOffType.of(osd.getJsonNode().path("watermark").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_SHOW_DATETIME, order = 103, icon = "fas fa-copyright", group = "VIDEO.OSD")
    public void setShowDateTime(boolean on) {
        setSetting("Osd", osd -> osd.set(boolToInt(on), "osdTime", "enable"));
    }

    @UICameraActionGetter(CHANNEL_SHOW_DATETIME)
    public State getShowDateTime() {
        JsonType osd = (JsonType) getAttribute("Osd");
        return osd == null ? null : OnOffType.of(osd.getJsonNode().path("osdTime").path("enable").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_IMAGE_ROTATE, order = 160, icon = "fas fa-copyright", group = "VIDEO.ISP")
    public void setRotateImage(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "rotation"));
    }

    @UICameraActionGetter(CHANNEL_IMAGE_ROTATE)
    public State getRotateImage() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.of(isp.getJsonNode().path("rotation").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_IMAGE_MIRROR, order = 161, icon = "fas fa-copyright", group = "VIDEO.ISP")
    public void setMirrorImage(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "mirroring"));
    }

    @UICameraActionGetter(CHANNEL_IMAGE_MIRROR)
    public State getMirrorImage() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.of(isp.getJsonNode().path("mirroring").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_ANTI_FLICKER, order = 162, icon = "fab fa-flickr", group = "VIDEO.ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"antiFlicker"})
    public void setAntiFlicker(String value) {
        setSetting("Isp", isp -> isp.set(value, "antiFlicker"));
    }

    @UICameraActionGetter(CHANNEL_ANTI_FLICKER)
    public State getAntiFlicker() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.get("antiFlicker").asText());
    }

    @UICameraAction(name = CHANNEL_EXPOSURE, order = 163, icon = "fas fa-sun", group = "VIDEO.ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"exposure"})
    public void setExposure(String value) {
        setSetting("Isp", isp -> isp.set(value, "exposure"));
    }

    @UICameraActionGetter(CHANNEL_EXPOSURE)
    public State getExposure() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.get("exposure").asText());
    }

    @UICameraAction(name = CHANNEL_DAY_NIGHT, order = 164, icon = "fas fa-cloud-sun", group = "VIDEO.ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"dayNight"})
    public void setDayNight(String value) {
        setSetting("Isp", isp -> isp.set(value, "dayNight"));
    }

    @UICameraActionGetter(CHANNEL_DAY_NIGHT)
    public State getDayNight() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.getJsonNode().path("dayNight").asText());
    }

    @UICameraAction(name = CHANNEL_3DNR, order = 165, icon = "fab fa-unity", group = "VIDEO.ISP")
    public void set3DNR(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "nr3d"));
    }

    @UICameraActionGetter(CHANNEL_3DNR)
    public State get3DNR() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.of(isp.getJsonNode().path("nr3d").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_RECORD_AUDIO, order = 80, group = "VIDEO.ENC", subGroup = "VIDEO.mainStream", subGroupIcon = "fas fa-dice-six")
    public void setRecAudio(boolean on) {
        setSetting("Enc", enc -> enc.set(boolToInt(on), "audio"));
    }

    @UICameraActionGetter(CHANNEL_RECORD_AUDIO)
    public State getRecAudio() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : OnOffType.of(enc.getJsonNode().path("audio").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_RESOLUTION, order = 81, group = "VIDEO.ENC", subGroup = "VIDEO.mainStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectResolution.class, staticParameters = {"mainStream"})
    public void setStreamMainResolution(String value) {
        setSetting("Enc", enc -> enc.set(value, "mainStream", "size"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_RESOLUTION)
    public State getStreamMainResolution() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "size").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_BITRATE, order = 82, group = "VIDEO.ENC", subGroup = "VIDEO.mainStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "bitRate"})
    public void setStreamMainBitRate(String value) {
        setSetting("Enc", enc -> enc.set(value, "mainStream", "bitRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_BITRATE)
    public State getStreamMainBitRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "bitRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_FRAMERATE, order = 83, group = "VIDEO.ENC", subGroup = "VIDEO.mainStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "frameRate"})
    public void setStreamMainFrameRate(String value) {
        setSetting("Enc", enc -> enc.set(value, "mainStream", "frameRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_FRAMERATE)
    public State getStreamMainFrameRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "frameRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_H264_PROFILE, order = 84, group = "VIDEO.ENC", subGroup = "VIDEO.mainStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "profile"})
    public void setStreamMainH264Profile(String value) {
        setSetting("Enc", enc -> enc.set(value, "mainStream", "profile"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_H264_PROFILE)
    public State getStreamMainH264Profile() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "profile").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_RESOLUTION, order = 90, group = "VIDEO.ENC", subGroup = "VIDEO.subStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectResolution.class, staticParameters = {"subStream"})
    public void setStreamSecondaryResolution(String value) {
        setSetting("Enc", enc -> enc.set(value, "subStream", "size"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_RESOLUTION)
    public State getStreamSecondaryResolution() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "size").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_BITRATE, order = 91, group = "VIDEO.ENC", subGroup = "VIDEO.subStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"subStream", "bitRate"})
    public void setStreamSecondaryBitRate(String value) {
        setSetting("Enc", enc -> enc.set(value, "subStream", "bitRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_BITRATE)
    public State getStreamSecondaryBitRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "bitRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_FRAMERATE, order = 92, group = "VIDEO.ENC", subGroup = "VIDEO.subStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"subStream", "frameRate"})
    public void setStreamSecondaryFrameRate(String value) {
        setSetting("Enc", enc -> enc.set(value, "subStream", "frameRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_FRAMERATE)
    public State getStreamSecondaryFrameRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "frameRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_H264_PROFILE, order = 93, group = "VIDEO.ENC", subGroup = "VIDEO.subStream", subGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"subStream", "profile"})
    public void setStreamSecondaryH264Profile(String value) {
        setSetting("Enc", isp -> isp.set(value, "subStream", "profile"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_H264_PROFILE)
    public State getStreamSecondaryH264Profile() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "profile").asText());
    }

    @Nullable
    private State getOSDPosition(String path) {
        JsonType osd = (JsonType) getAttribute("Osd");
        if (osd == null) {
            return null;
        }
        if (osd.getJsonNode().path(path).path("enable").asInt() == 0) {
            return new StringType("");
        }
        return new StringType(osd.getJsonNode().path(path).path("pos").asText());
    }

    @Override
    public void initialize(EntityContext entityContext) {
        runOncePerMinute(entityContext);
    }

    @Override
    public void runOncePerMinute(EntityContext entityContext) {
        loginIfRequire();

        ObjectNode[] objectNodes = firePost("", true, ObjectNode[].class,
                new ReolinkCmd(1, "GetIrLights", new ChannelParam()),
                new ReolinkCmd(1, "GetOsd", new ChannelParam()),
                new ReolinkCmd(1, "GetEnc", new ChannelParam()),
                new ReolinkCmd(1, "GetImage", new ChannelParam()),
                new ReolinkCmd(1, "Getisp", new ChannelParam()));
        if (objectNodes != null) {
            for (ObjectNode objectNode : objectNodes) {
                String cmd = objectNode.get("cmd").asText();
                switch (cmd) {
                    case "GetOsd":
                        setAttribute("Osd", new JsonType(objectNode.path("value").path("Osd")));
                        setAttribute("OsdRange", new JsonType(objectNode.path("range").path("Osd")));
                        break;
                    case "GetEnc":
                        setAttribute("Enc", new JsonType(objectNode.path("value").path("Enc")));
                        setAttribute("EncRange", new JsonType(objectNode.path("range").path("Enc")));
                        break;
                    case "GetImage":
                        setAttribute("Img", new JsonType(objectNode.path("value").path("Image")));
                        break;
                    case "GetIsp":
                        setAttribute("Isp", new JsonType(objectNode.path("value").path("Isp")));
                        setAttribute("IspRange", new JsonType(objectNode.path("range").path("Isp")));
                        break;
                    case "GetIrLights":
                        setAttribute(CHANNEL_AUTO_LED, OnOffType.of("Auto".equals(objectNode.path("value").path("IrLights").path("state").asText())));
                        break;
                }
            }
        }
    }

    @Override
    public String updateURL(String url) {
        loginIfRequire();
        return url + (url.contains("?") ? "&" : "?") + "token=" + token;
    }

    @Override
    public boolean isSupportOnvifEvents() {
        return true;
    }

    private void loginIfRequire() {
        if (this.tokenExpiration - System.currentTimeMillis() < 60000) {
            LoginRequest loginRequest = new LoginRequest(new LoginRequest.User(cameraEntity.getUser(), cameraEntity.getPassword().asString()));
            Root root = firePost("?cmd=Login", false, Root[].class, new ReolinkCmd(0, "Login", loginRequest))[0];
            this.tokenExpiration = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(root.value.token.leaseTime);
            this.token = root.value.token.name;
        }
    }

    @Getter
    @AllArgsConstructor
    private static class LoginRequest {
        @JsonProperty("User")
        private User user;

        @Getter
        @AllArgsConstructor
        private static class User {
            String userName;
            String password;
        }
    }

    @SneakyThrows
    public <T> T firePost(String url, boolean requireAuth, Class<T> clazz, ReolinkCmd... commands) {
        String fullUrl = getAuthUrl(url, requireAuth);
        var requestEntity = RequestEntity.post(new URL(fullUrl).toURI()).contentType(MediaType.APPLICATION_JSON).body(Arrays.asList(commands));
        ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
        return exchange.getBody() == null ? null : new ObjectMapper().readValue(exchange.getBody(), clazz);
    }

    public String getAuthUrl(String url, boolean requireAuth) {
        if (requireAuth) {
            url = updateURL(url);
        }
        return "http://" + this.ip + ":" + this.cameraEntity.getRestPort() + "/cgi-bin/api.cgi" + url;
    }

    @SneakyThrows
    private boolean firePostGetCode(String url, boolean requireAuth, ReolinkCmd... commands) {
        ObjectNode[] objectNodes = firePost(url, requireAuth, ObjectNode[].class, commands);
        if (objectNodes == null) {
            return false;
        }
        if (objectNodes[0].path("value").path("rspCode").intValue() == 200) {
            return true;
        }
        entityContext.ui().sendErrorMessage("Error while updating reolink settings. " + objectNodes[0].path("error").path("detail").toString());
        return false;
    }

    private void setSetting(String key, Consumer<JsonType> updateHandler) {
        JsonType setting = (JsonType) getAttribute(key);
        if (setting == null) {
            return;
        }
        updateHandler.accept(setting);
        loginIfRequire();
        if (firePostGetCode("/cgi-bin/api.cgi?cmd=Set" + key, true,
                new ReolinkCmd(0, "Set" + key, "{\"" + key + "\":" + setting.toString() + "}"))) {
            entityContext.ui().sendSuccessMessage("Reolink set " + key + " applied successfully");
        }
    }

    @Override
    public void setMotionAlarmThreshold(int threshold) {

    }

    private class SelectResolution implements DynamicOptionLoader {
        @Override
        public Collection<OptionModel> loadOptions(BaseEntity baseEntity, EntityContext entityContext, String[] staticParameters) {
            Collection<OptionModel> result = new ArrayList<>();
            JsonType encRange = (JsonType) ReolinkBrandHandler.this.getAttribute("EncRange");
            if (encRange != null && encRange.getJsonNode().isArray()) {
                for (JsonNode jsonNode : encRange.getJsonNode()) {
                    result.add(OptionModel.key(jsonNode.get(staticParameters[0]).get("size").asText()));
                }
            }
            return result;
        }
    }

    private class SelectStreamValue implements DynamicOptionLoader {
        @Override
        public Collection<OptionModel> loadOptions(BaseEntity baseEntity, EntityContext entityContext, String[] staticParameters) {
            JsonType encRange = (JsonType) ReolinkBrandHandler.this.getAttribute("EncRange");
            JsonType enc = (JsonType) getAttribute("Enc");
            if (enc != null && encRange != null && encRange.getJsonNode().isArray()) {
                String size = enc.get(staticParameters[0], "size").asText();
                for (JsonNode jsonNode : encRange.getJsonNode()) {
                    JsonNode streamJsonNode = jsonNode.get(staticParameters[0]);
                    if (streamJsonNode.get("size").asText().equals(size)) {
                        return OptionModel.list(streamJsonNode.get(staticParameters[1]));
                    }
                }
            }
            return Collections.emptySet();
        }
    }

    @Getter
    public static class ChannelParam {
        private int channel = 0;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ReolinkCmd {
        private final int action;
        private final String cmd;
        private final Object param;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SearchRequest {
        @JsonProperty("Search")
        private final Search search;

        @Getter
        @RequiredArgsConstructor
        public static class Search {
            private final int channel = 0;
            private final int onlyStatus;
            private final String streamType;
            @JsonProperty("StartTime")
            private final Time startTime;
            @JsonProperty("EndTime")
            private final Time endTime;
        }
    }

    @Getter
    public static class Status {
        private int mon;
        private String table;
        private int year;
    }

    @Getter
    public static class SearchResult {
        @JsonProperty("Status")
        private List<Status> status;
        @JsonProperty("File")
        private List<File> file;
        private int channel;
    }

    @Getter
    public static class Value {
        @JsonProperty("SearchResult")
        private SearchResult searchResult;
        @JsonProperty("Token")
        private Token token;
    }

    @Getter
    public static class Token {
        private int leaseTime;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Time {
        private int year;
        private int mon;
        private int day;
        private int hour;
        private int min;
        private int sec;

        public static Time of(Date date) {
            LocalDateTime time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return new Time(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
        }

        public static Date from(Time time) {
            return Date.from(LocalDateTime.of(time.year, time.mon, time.day, time.hour, time.min, time.sec).atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class File {
        @JsonProperty("StartTime")
        private Time startTime;
        @JsonProperty("EndTime")
        private Time endTime;
        private int frameRate;
        private int height;
        private String name;
        private int size;
        private String type;
        private int width;

        public PlaybackFile toPlaybackFile() {
            return new PlaybackFile(name, name, Time.from(startTime), Time.from(endTime), size, type);
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {
        private String cmd;
        private int code;
        private Value value;
        private Error error;

        @Getter
        public static class Error {
            private String detail;
            private int rspCode;
        }
    }
}
