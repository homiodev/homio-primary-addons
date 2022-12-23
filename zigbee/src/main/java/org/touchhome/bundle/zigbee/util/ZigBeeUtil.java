package org.touchhome.bundle.zigbee.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zsmartsystems.zigbee.CommandResult;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import javax.measure.Unit;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.util.Units;
import org.touchhome.common.util.CommonUtils;

@Log4j2
public final class ZigBeeUtil {

  private static Map<String, JsonNode> deviceDefinitions = new HashMap<>();

  static {
    try {
      ArrayNode nodes = CommonUtils.OBJECT_MAPPER.readValue(ZigBeeUtil.class.getClassLoader().getResource("zigbee-devices.json"), ArrayNode.class);
      for (JsonNode node : nodes) {
        ZigBeeUtil.deviceDefinitions.put(node.get("model").asText(), node);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static ActionResponseModel toResponseModel(Future<CommandResult> command) {
    try {
      CommandResult commandResult = command.get();
      if (commandResult.isSuccess()) {
        return ActionResponseModel.success();
      } else {
        return ActionResponseModel.showError("Unable to execute command. Response: [code '" +
            commandResult.getStatusCode() + "', data: '" + commandResult + "']");
      }
    } catch (Exception ex) {
      return ActionResponseModel.showError(ex);
    }
  }

  public static void zigbeeScanStarted(EntityContext entityContext, String entityID, int duration,
      Runnable onDurationTimedOutHandler, Runnable stopScanHandler) {
    entityContext.ui().headerButtonBuilder("zigbee-scan-" + entityID)
                 .title("zigbee.action.stop_scan")
                 .border(1, "#899343")
                 .clickAction(() -> {
                   stopScanHandler.run();
                   return null;
                 }).duration(duration).icon("fas fa-search-location", "#899343", false).build();

    entityContext.bgp().builder("zigbee-scan-killer-" + entityID)
                 .delay(Duration.ofSeconds(duration))
                 .execute(() -> {
                   log.info("[{}]: Scanning stopped", entityID);
                   onDurationTimedOutHandler.run();
                   entityContext.ui().removeHeaderButton("zigbee-scan-" + entityID);
                 });
  }

  public static Unit<?> getUnit(String unitName) {
    for (Unit<?> unit : Units.getInstance().getUnits()) {
      if (unitName.equals(unit.toString())) {
        return unit;
      }
    }
    for (Unit<?> unit : tech.units.indriya.unit.Units.getInstance().getUnits()) {
      if (unitName.equals(unit.toString())) {
        return unit;
      }
    }
    throw new IllegalArgumentException("Unable to find unit: " + unitName);
  }

  public static String getDeviceIcon(String modelId, String defaultIcon) {
    return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).get("icon").asText() : defaultIcon;
  }

  public static String getDeviceIconColor(String modelId, String defaultIconColor) {
    return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).get("iconColor").asText() : defaultIconColor;
  }

  public static JsonNode getDeviceOptions(String modelId) {
    return deviceDefinitions.containsKey(modelId) ? deviceDefinitions.get(modelId).get("options") : CommonUtils.OBJECT_MAPPER.createObjectNode();
  }
}
