package org.touchhome.bundle.zigbee.util;

import static java.util.Optional.ofNullable;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.util.DeviceConfiguration.EndpointDefinition;
import org.touchhome.common.util.CommonUtils;

public final class DeviceConfigurations {

  @Getter
  private static final List<DeviceConfiguration> defineEndpoints = new ArrayList<>();

  static {
    var objectNode = CommonUtils.readAndMergeJSON("device-definitions.json", OBJECT_MAPPER.createObjectNode());
    for (JsonNode vendorNode : objectNode) {
      for (JsonNode deviceNode : vendorNode.get("devices")) {
        var deviceDefinition = new DeviceConfiguration();
        deviceDefinition.setVendor(deviceNode.has("vendor") ? deviceNode.get("vendor").asText() : vendorNode.asText());
        deviceDefinition.setId(ofNullable(deviceNode.get("id")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setImage(ofNullable(deviceNode.get("image")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setCategory(ofNullable(deviceNode.get("category")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setModelId(ofNullable(deviceNode.get("model_id")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setLabel(getDefinitionMap(deviceNode, "label"));
        deviceDefinition.setDescription(getDefinitionMap(deviceNode, "description"));

        for (JsonNode endpoint : deviceNode.path("endpoints")) {
          var endpointDefinition = new EndpointDefinition();
          endpointDefinition.setId(ofNullable(endpoint.get("id")).map(JsonNode::textValue).orElse(null));
          endpointDefinition.setInputClusters(JsonReaderUtil.getOptIntegerArray(endpoint, "input_clusters"));
          endpointDefinition.setLabel(getDefinitionMap(endpoint, "label"));
          endpointDefinition.setDescription(getDefinitionMap(endpoint, "description"));
          endpointDefinition.setTypeId(endpoint.get("type_id").textValue());
          endpointDefinition.setEndpoint(endpoint.get("endpoint").asInt());
          endpointDefinition.setMetadata(endpoint.get("meta"));
          deviceDefinition.getEndpoints().add(endpointDefinition);
        }

        defineEndpoints.add(deviceDefinition);
      }
    }
  }

  private static Map<String, String> getDefinitionMap(JsonNode deviceNode, String key) {
    if (deviceNode.has(key)) {
      Map<String, String> map = new HashMap<>();
      for (Iterator<Entry<String, JsonNode>> iterator = deviceNode.get(key).fields(); iterator.hasNext(); ) {
        Entry<String, JsonNode> entry = iterator.next();
        map.put(entry.getKey(), entry.getValue().textValue());
      }
      return map;
    }
    return null;
  }

  public static Optional<DeviceConfiguration> getDeviceDefinition(@NotNull String modelIdentifier) {
    for (DeviceConfiguration defineEndpoint : defineEndpoints) {
      if (modelIdentifier.equals(defineEndpoint.getModelId())) {
        return Optional.of(defineEndpoint);
      }
    }
    return Optional.empty();
  }

  public static DeviceConfiguration findDeviceDefinition(ZigBeeDeviceEntity entity) {
    if (entity.getModelIdentifier() != null) {
      return defineEndpoints.stream().filter(c -> Objects.equals(c.getModelId(), entity.getModelIdentifier()))
          .findAny().orElse(null);
    }
    return null;
  }
}
