package org.touchhome.bundle.camera.onvif.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
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
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;
import org.touchhome.bundle.camera.ui.UICameraSelectionAttributeValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.*;

@Log4j2
@CameraBrandHandler(name = "Reolink")
public class ReolinkBrandHandler extends BaseOnvifCameraBrandHandler {

    private final RestTemplate restTemplate = new RestTemplate();
    private long tokenExpiration;
    private String token;

    public ReolinkBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
    }

    @UICameraAction(name = CHANNEL_AUTO_LED, order = 40, icon = "fas fa-lightbulb")
    public void autoLed(boolean on) {
        String state = on ? "Auto" : "Off";
        String body = "[{\"cmd\":\"SetIrLights\",\"action\":0,\"param\":{\"IrLights\":{\"state\":\"" + state + "\"}}}]";
        if (firePostGetCode("/cgi-bin/api.cgi?cmd=SetIrLights", body, true)) {
            setAttribute(CHANNEL_AUTO_LED, OnOffType.valueOf(on));
            entityContext.ui().sendSuccessMessage("Reolink set IR light applied successfully");
        }
    }

    @UICameraActionGetter(CHANNEL_AUTO_LED)
    public State isAutoLed() {
        return getAttribute(CHANNEL_AUTO_LED);
    }

    @UICameraAction(name = CHANNEL_RECORD_AUDIO, order = 41, icon = "fas fa-microphone-slash")
    public void setRecAudio(boolean on) {
        setSetting("Esp", isp -> isp.set(boolToInt(on), "audio"));
    }

    @UICameraActionGetter(CHANNEL_RECORD_AUDIO)
    public State getRecAudio() {
        JsonType esp = (JsonType) getAttribute("Esp");
        return esp == null ? null : OnOffType.valueOf(esp.getJsonNode().path("audio").asInt() == 1);
    }

    @UICameraActionGetter(CHANNEL_POSITION_NAME)
    public State getNamePosition() {
        return getOSDPosition("osdChannel");
    }

    @UICameraAction(name = CHANNEL_POSITION_NAME, order = 50, icon = "fas fa-sort-amount-down-alt", group = "OSD")
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

    @UICameraAction(name = CHANNEL_SHOW_DATETIME, order = 51, icon = "fas fa-copyright", group = "OSD")
    public void setShowDateTime(boolean on) {
        setSetting("Osd", osd -> osd.set(boolToInt(on), "osdTime", "enable"));
    }

    @UICameraActionGetter(CHANNEL_SHOW_DATETIME)
    public State getShowDateTime() {
        JsonType osd = (JsonType) getAttribute("Osd");
        return osd == null ? null : OnOffType.valueOf(osd.getJsonNode().path("osdTime").path("enable").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_POSITION_DATETIME, order = 52, icon = "fas fa-sort-numeric-up", group = "OSD")
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

    @UICameraAction(name = CHANNEL_SHOW_WATERMARK, order = 53, icon = "fas fa-copyright", group = "OSD")
    public void setShowWatermark(boolean on) {
        setSetting("Osd", osd -> osd.set(boolToInt(on), "watermark"));
    }

    @UICameraActionGetter(CHANNEL_SHOW_WATERMARK)
    public State getShowWatermark() {
        JsonType osd = (JsonType) getAttribute("Osd");
        return osd == null ? null : OnOffType.valueOf(osd.getJsonNode().path("watermark").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_IMAGE_ROTATE, order = 60, icon = "fas fa-copyright", group = "ISP")
    public void setRotateImage(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "rotation"));
    }

    @UICameraActionGetter(CHANNEL_IMAGE_ROTATE)
    public State getRotateImage() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.valueOf(isp.getJsonNode().path("rotation").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_IMAGE_MIRROR, order = 61, icon = "fas fa-copyright", group = "ISP")
    public void setMirrorImage(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "mirroring"));
    }

    @UICameraActionGetter(CHANNEL_IMAGE_MIRROR)
    public State getMirrorImage() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.valueOf(isp.getJsonNode().path("mirroring").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_ANTI_FLICKER, order = 62, icon = "fab fa-flickr", group = "ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"antiFlicker"})
    public void setAntiFlicker(String value) {
        setSetting("Isp", isp -> isp.set(value, "antiFlicker"));
    }

    @UICameraActionGetter(CHANNEL_ANTI_FLICKER)
    public State getAntiFlicker() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.get("antiFlicker").asText());
    }

    @UICameraAction(name = CHANNEL_EXPOSURE, order = 63, icon = "fas fa-sun", group = "ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"exposure"})
    public void setExposure(String value) {
        setSetting("Isp", isp -> isp.set(value, "exposure"));
    }

    @UICameraActionGetter(CHANNEL_EXPOSURE)
    public State getExposure() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.get("exposure").asText());
    }

    @UICameraAction(name = CHANNEL_DAY_NIGHT, order = 64, icon = "fas fa-cloud-sun", group = "ISP")
    @UICameraSelectionAttributeValues(value = "IspRange", path = {"dayNight"})
    public void setDayNight(String value) {
        setSetting("Isp", isp -> isp.set(value, "dayNight"));
    }

    @UICameraActionGetter(CHANNEL_DAY_NIGHT)
    public State getDayNight() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : new StringType(isp.getJsonNode().path("dayNight").asText());
    }

    @UICameraAction(name = CHANNEL_3DNR, order = 61, icon = "fab fa-unity", group = "ISP")
    public void set3DNR(boolean on) {
        setSetting("Isp", isp -> isp.set(boolToInt(on), "nr3d"));
    }

    @UICameraActionGetter(CHANNEL_3DNR)
    public State get3DNR() {
        JsonType isp = (JsonType) getAttribute("Isp");
        return isp == null ? null : OnOffType.valueOf(isp.getJsonNode().path("nr3d").asInt() == 1);
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_RESOLUTION, order = 80, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "mainStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectResolution.class, staticParameters = {"mainStream"})
    public void setStreamMainResolution(String value) {
        setSetting("Enc", isp -> isp.set(value, "mainStream", "size"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_RESOLUTION)
    public State getStreamMainResolution() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "size").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_BITRATE, order = 81, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "mainStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "bitRate"})
    public void setStreamMainBitRate(String value) {
        setSetting("Enc", isp -> isp.set(value, "mainStream", "bitRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_BITRATE)
    public State getStreamMainBitRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "bitRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_FRAMERATE, order = 82, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "mainStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "frameRate"})
    public void setStreamMainFrameRate(String value) {
        setSetting("Enc", isp -> isp.set(value, "mainStream", "frameRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_FRAMERATE)
    public State getStreamMainFrameRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "frameRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_MAIN_H264_PROFILE, order = 83, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "mainStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"mainStream", "profile"})
    public void setStreamMainH264Profile(String value) {
        setSetting("Enc", isp -> isp.set(value, "mainStream", "profile"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_MAIN_H264_PROFILE)
    public State getStreamMainH264Profile() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("mainStream", "profile").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_RESOLUTION, order = 80, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "subStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectResolution.class, staticParameters = {"subStream"})
    public void setStreamSecondaryResolution(String value) {
        setSetting("Enc", isp -> isp.set(value, "subStream", "size"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_RESOLUTION)
    public State getStreamSecondaryResolution() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "size").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_BITRATE, order = 81, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "subStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"subStream", "bitRate"})
    public void setStreamSecondaryBitRate(String value) {
        setSetting("Enc", isp -> isp.set(value, "subStream", "bitRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_BITRATE)
    public State getStreamSecondaryBitRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "bitRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_FRAMERATE, order = 82, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "subStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
    @UIFieldSelection(value = SelectStreamValue.class, staticParameters = {"subStream", "frameRate"})
    public void setStreamSecondaryFrameRate(String value) {
        setSetting("Enc", isp -> isp.set(value, "subStream", "frameRate"));
    }

    @UICameraActionGetter(CHANNEL_STREAM_SECONDARY_FRAMERATE)
    public State getStreamSecondaryFrameRate() {
        JsonType enc = (JsonType) getAttribute("Enc");
        return enc == null ? null : new StringType(enc.get("subStream", "frameRate").asText());
    }

    @UICameraAction(name = CHANNEL_STREAM_SECONDARY_H264_PROFILE, order = 83, icon = "fas fa-microphone-slash",
            group = "ENC", subGroup = "subStream", collapseGroup = true, collapseGroupIcon = "fas fa-dice-six")
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
        String body = "[{\"cmd\":\"GetIrLights\",\"action\":1,\"param\":{}},{\"cmd\":\"GetOsd\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"GetEnc\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"GetImage\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"Getisp\",\"action\":1,\"param\":{\"channel\":0}}]";
        ObjectNode[] objectNodes = firePost("/cgi-bin/api.cgi", body, true);
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
                    setAttribute(CHANNEL_AUTO_LED, OnOffType.valueOf("Auto".equals(objectNode.path("value").path("IrLights").path("state").asText())));
                    break;
            }
        }
    }

    @Override
    public String updateURL(String url) {
        loginIfRequire();
        return url + (url.contains("?") ? "&" : "?") + "token=" + token;
    }

    private void loginIfRequire() {
        if (this.tokenExpiration - System.currentTimeMillis() < 60000) {
            String body = "[{\"cmd\":\"Login\",\"action\":0,\"param\":{\"User\":{\"userName\":\"" +
                    cameraEntity.getUser() + "\",\"password\":\"" + cameraEntity.getPassword().asString() + "\"}}}]";
            ObjectNode objectNode = firePost("?cmd=Login", body, false)[0];
            JsonNode token = objectNode.path("value").path("Token");
            this.tokenExpiration = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(token.path("leaseTime").asInt());
            this.token = token.path("name").asText();
        }
    }

    @SneakyThrows
    private ObjectNode[] firePost(String url, String body, boolean requireAuth) {
        if (requireAuth) {
            url = updateURL(url);
        }
        String msg = restTemplate.postForEntity("http://" + this.ip + ":" + this.cameraEntity.getRestPort() +
                "/cgi-bin/api.cgi" + url, body, String.class).getBody();
        return msg == null ? new ObjectNode[0] : new ObjectMapper().readValue(msg, ObjectNode[].class);
    }

    @SneakyThrows
    private boolean firePostGetCode(String url, String body, boolean requireAuth) {
        ObjectNode[] objectNodes = firePost(url, body, requireAuth);
        if (objectNodes.length == 0) {
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
        String body = "[{\"cmd\":\"Set" + key + "\",\"action\":0,\"param\":{\"" + key + "\":" + setting.toString() + "}}]";
        if (firePostGetCode("/cgi-bin/api.cgi?cmd=Set" + key, body, true)) {
            entityContext.ui().sendSuccessMessage("Reolink set " + key + " applied successfully");
        }
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
}
