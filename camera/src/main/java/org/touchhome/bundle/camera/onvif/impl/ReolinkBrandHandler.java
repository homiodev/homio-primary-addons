package org.touchhome.bundle.camera.onvif.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.state.OnOffType;
import org.touchhome.bundle.api.state.State;
import org.touchhome.bundle.api.state.StringType;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.onvif.BaseOnvifCameraBrandHandler;
import org.touchhome.bundle.camera.ui.UICameraAction;
import org.touchhome.bundle.camera.ui.UICameraActionGetter;

import java.util.concurrent.TimeUnit;

import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_AUTO_LED;

@Log4j2
@CameraBrandHandler(name = "Reolink")
public class ReolinkBrandHandler extends BaseOnvifCameraBrandHandler {

    private final RestTemplate restTemplate = new RestTemplate();
    private long tokenExpiration;
    private String token;

    public ReolinkBrandHandler(OnvifCameraEntity onvifCameraEntity) {
        super(onvifCameraEntity);
    }

    @UICameraAction(name = CHANNEL_AUTO_LED, order = 60, icon = "fas fa-lightbulb")
    public void autoLed(boolean on) {
        loginIfRequire();
        setAttributeRequest(CHANNEL_AUTO_LED, OnOffType.valueOf(on));
        String state = on ? "Auto" : "Off";
        String body = "[{\"cmd\":\"SetIrLights\",\"action\":0,\"param\":{\"IrLights\":{\"state\":\"" + state + "\"}}}]";
        String url = "/cgi-bin/api.cgi?cmd=SetIrLights&token=" + token;
        onvifCameraHandler.sendHttpPOST(url, buildFullHttpRequest(url, body, HttpMethod.POST, MediaType.APPLICATION_JSON));
    }

    @UICameraActionGetter(CHANNEL_AUTO_LED)
    public State isAutoLed() {
        return getAttribute(CHANNEL_AUTO_LED);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg == null || ctx == null) {
            return;
        }
        try {
            String content = msg.toString();
            try {
                for (ObjectNode objectNode : convertToObjectNode(content)) {
                    String cmd = objectNode.get("cmd").asText();
                    int respCode = objectNode.path("value").path("rspCode").intValue();
                    switch (cmd) {
                        case "GetOsd":
                            setAttribute("OsdSettings", new StringType(objectNode.path("value").path("Osd").asText()));
                        case "GetEnc":
                            setAttribute("EncodingSettings", new StringType(objectNode.path("value").path("Enc").asText()));
                            break;
                        case "GetImage":
                            setAttribute("ImageSettings", new StringType(objectNode.path("value").path("Image").asText()));
                            break;
                        case "GetIsp":
                            setAttribute("ISP", new StringType(objectNode.path("value").path("Isp").asText()));
                            break;
                        case "GetIrLights":
                            setAttribute(CHANNEL_AUTO_LED, OnOffType.valueOf("Auto".equals(objectNode.path("value").path("IrLights").path("state").asText())));
                            break;
                        case "SetIrLights":
                            if (respCode == 200) {
                                setAttribute(CHANNEL_AUTO_LED, getAttributeRequest(CHANNEL_AUTO_LED));
                            }
                            break;
                    }
                }
            } catch (Exception ex) {
                log.error("Unable to handle reolink income message: <{}>", msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
            ctx.close();
        }
    }

    @Override
    public void runOncePerMinute(EntityContext entityContext) {
        loginIfRequire();
        String url = "/cgi-bin/api.cgi?token=" + token;
        String body = "[{\"cmd\":\"GetIrLights\",\"action\":1,\"param\":{}},{\"cmd\":\"GetOsd\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"GetEnc\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"GetImage\",\"action\":1,\"param\":{\"channel\":0}},{\"cmd\":\"GetIsp\",\"action\":1,\"param\":{\"channel\":0}}]";
        onvifCameraHandler.sendHttpPOST(url, buildFullHttpRequest(url, body, HttpMethod.POST, MediaType.APPLICATION_JSON));
    }

    private void loginIfRequire() {
        if (this.tokenExpiration - System.currentTimeMillis() < 60000) {
            String request = "[{\"cmd\":\"Login\",\"action\":0,\"param\":{\"User\":{\"userName\":\"admin\",\"password\":\"rozarija\"}}}]";
            String body = StringUtils.defaultString(restTemplate.postForEntity("http://" + this.ip + "/cgi-bin/api.cgi?cmd=Login", request, String.class).getBody(), "");

            ObjectNode objectNode = convertToObjectNode(body)[0];
            JsonNode token = objectNode.path("value").path("Token");
            this.tokenExpiration = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(token.get("leaseTime").asInt());
            this.token = token.get("name").asText();
        }
    }

    @SneakyThrows
    private ObjectNode[] convertToObjectNode(String msg) {
        return new ObjectMapper().readValue(msg, ObjectNode[].class);
    }
}
