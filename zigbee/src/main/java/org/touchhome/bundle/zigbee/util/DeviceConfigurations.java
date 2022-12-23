package org.touchhome.bundle.zigbee.util;

import static java.util.Optional.ofNullable;
import static org.touchhome.bundle.zigbee.util.JsonReaderUtil.getOptStringArray;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.zigbee.util.DeviceConfiguration.EndpointDefinition;
import org.touchhome.common.util.CommonUtils;

public final class DeviceConfigurations {

  @Getter
  private static final List<DeviceConfiguration> defineEndpoints = new ArrayList<>();

  static {
    try {
      var objectNode = CommonUtils.readAndMergeJSON("device-definitions.json", OBJECT_MAPPER.createObjectNode());
      for (JsonNode vendorNode : objectNode) {
        for (JsonNode deviceNode : vendorNode.get("devices")) {
          var deviceDefinition = new DeviceConfiguration();
          if (!deviceNode.has("models")) {
            throw noKeyFound(deviceNode, "models");
          }
          deviceDefinition.setModels(getOptStringArray(deviceNode, "models"));

          deviceDefinition.setVendor(deviceNode.has("vendor") ? deviceNode.get("vendor").asText() : vendorNode.asText());
          deviceDefinition.setImage(ofNullable(deviceNode.get("image")).map(JsonNode::textValue).orElse(null));
          deviceDefinition.setIcon(ofNullable(deviceNode.get("icon")).map(JsonNode::textValue).orElseThrow(() -> noKeyFound(deviceNode, "icon")));
          deviceDefinition.setIconColor(ofNullable(deviceNode.get("iconColor")).map(JsonNode::textValue).orElseThrow(() -> noKeyFound(deviceNode, "iconColor")));
          deviceDefinition.setCategory(ofNullable(deviceNode.get("category")).map(JsonNode::textValue).orElse(null));
          for (JsonNode endpoint : deviceNode.path("endpoints")) {
            var endpointDefinition = new EndpointDefinition();
            endpointDefinition.setId(ofNullable(endpoint.get("id")).map(JsonNode::textValue).orElse(null));
            endpointDefinition.setInputClusters(JsonReaderUtil.getOptIntegerArray(endpoint, "input_clusters"));
            endpointDefinition.setTypeId(endpoint.get("type_id").textValue());
            if (endpoint.has("unit")) {
              endpointDefinition.setUnit(ZigBeeUtil.getUnit(endpoint.get("unit").asText()));
            }
            endpointDefinition.setEndpoint(endpoint.get("endpoint").asInt());
            endpointDefinition.setMetadata(endpoint.get("meta"));
            deviceDefinition.addEndpoint(endpointDefinition);
          }

          defineEndpoints.add(deviceDefinition);
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static Exception noKeyFound(JsonNode deviceNode, String key) {
    return new IllegalStateException("Unable to find key: " + key + " in device: " + deviceNode);
  }

  public static Optional<DeviceConfiguration> getDeviceDefinition(@Nullable String modelIdentifier) {
    if (modelIdentifier != null) {
      for (DeviceConfiguration defineEndpoint : defineEndpoints) {
        if (defineEndpoint.getModels().contains(modelIdentifier)) {
          return Optional.of(defineEndpoint);
        }
      }
    }
    return Optional.empty();
  }
}
