package org.touchhome.bundle.zigbee.requireEndpoint;

import static java.util.Optional.ofNullable;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition.EndpointDefinition;
import org.touchhome.common.util.CommonUtils;

public final class ZigBeeDefineEndpoints {

  @Getter
  private static final List<DeviceDefinition> defineEndpoints = new ArrayList<>();

  static {
    var objectNode = CommonUtils.readAndMergeJSON("zigBee/device-properties.json", OBJECT_MAPPER.createObjectNode());
    for (JsonNode vendorNode : objectNode) {
      for (JsonNode deviceNode : vendorNode.get("devices")) {
        var deviceDefinition = new DeviceDefinition();
        deviceDefinition.setVendor(deviceNode.has("vendor") ? deviceNode.get("vendor").asText() : vendorNode.asText());
        deviceDefinition.setId(ofNullable(deviceNode.get("id")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setImage(ofNullable(deviceNode.get("image")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setCategory(ofNullable(deviceNode.get("category")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setModelId(ofNullable(deviceNode.get("model_id")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setLabel(getDefinitionMap(deviceNode, "label"));
        deviceDefinition.setDescription(getDefinitionMap(deviceNode, "description"));

        for (JsonNode endpoint : deviceNode.get("endpoints")) {
          var endpointDefinition = new EndpointDefinition();
          endpointDefinition.setId(ofNullable(endpoint.get("id")).map(JsonNode::textValue).orElse(null));
          endpointDefinition.setInputCluster(ofNullable(endpoint.get("input_cluster")).map(JsonNode::asInt).orElse(null));
          endpointDefinition.setLabel(getDefinitionMap(endpoint, "label"));
          endpointDefinition.setDescription(getDefinitionMap(endpoint, "description"));
          endpointDefinition.setTypeId(endpoint.get("type_id").textValue());
          endpointDefinition.setEndpoint(endpoint.get("endpoint").asInt());
          if (endpoint.has("meta")) {
            Map<String, Object> map = new HashMap<>();
            for (JsonNode langNode : deviceNode.get("meta")) {
              map.put(langNode.asText(), langNode.asText());
            }
            endpointDefinition.setMetadata(map);
          }
          deviceDefinition.getEndpoints().add(endpointDefinition);
        }

        defineEndpoints.add(deviceDefinition);
      }
    }
  }

  private static Map<String, String> getDefinitionMap(JsonNode deviceNode, String key) {
    if (deviceNode.has(key)) {
      Map<String, String> map = new HashMap<>();
      for (JsonNode langNode : deviceNode.get(key)) {
        map.put(langNode.asText(), langNode.textValue());
      }
      return map;
    }
    return null;
  }

  public static List<EndpointDefinition> getEndpointDefinitions(@NotNull String modelIdentifier) {
    return defineEndpoints.stream().filter(c -> Objects.equals(c.getModelId(), modelIdentifier))
        .map(DeviceDefinition::getEndpoints).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
  }

  public static DeviceDefinition findDeviceDefinition(ZigBeeDeviceEntity entity) {
    if (entity.getModelIdentifier() != null) {
      return defineEndpoints.stream().filter(c -> Objects.equals(c.getModelId(), entity.getModelIdentifier()))
          .findAny().orElse(null);
    }
    return null;
  }
}
