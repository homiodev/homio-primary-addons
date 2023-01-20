package org.touchhome.bundle.z2m.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.common.util.CommonUtils;

@Log4j2
public final class ZigBeeUtil {

    static {
        try {
            deviceDefinitions = new HashMap<>();
            ArrayNode nodes = CommonUtils.OBJECT_MAPPER.readValue(ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"), ArrayNode.class);
            for (JsonNode node : nodes) {
                ZigBeeUtil.deviceDefinitions.put(node.get("model").asText(), node);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void zigbeeScanStarted(EntityContext entityContext, String entityID, int duration, Runnable onDurationTimedOutHandler,
        Runnable stopScanHandler) {
        entityContext.ui().headerButtonBuilder("zigbee-scan-" + entityID).title("zigbee.action.stop_scan").border(1, "#899343").clickAction(() -> {
                         stopScanHandler.run();
                         return null;
                     })
                     .duration(duration)
                     .icon("fas fa-search-location", "#899343", false)
                     .build();

        entityContext.bgp().builder("zigbee-scan-killer-" + entityID).delay(Duration.ofSeconds(duration)).execute(() -> {
            log.info("[{}]: Scanning stopped", entityID);
            onDurationTimedOutHandler.run();
            entityContext.ui().removeHeaderButton("zigbee-scan-" + entityID);
        });
    }

    public static String getDeviceIcon(String modelId, String defaultIcon) {
        return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).get("icon").asText() : defaultIcon;
    }

    public static String getDeviceIconColor(String modelId, String defaultIconColor) {
        return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).get("iconColor").asText() : defaultIconColor;
    }

    public static JsonNode getDeviceOptions(String modelId) {
        ObjectNode empty = CommonUtils.OBJECT_MAPPER.createObjectNode();
        JsonNode options = deviceDefinitions.getOrDefault(modelId, empty).get("options");
        return options == null ? empty : options;
    }

    private static final Map<String, JsonNode> deviceDefinitions;
}
