package org.touchhome.bundle.zigbee.util;

import static java.util.Optional.ofNullable;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.zigbee.model.ZigBeeDeviceEntity;
import org.touchhome.bundle.zigbee.util.DeviceDefinition.EndpointDefinition;
import org.touchhome.common.util.CommonUtils;

@Log4j2
public final class ZigBeeDefineEndpoints {

  @Getter
  private static final List<DeviceDefinition> defineEndpoints = new ArrayList<>();

  static {
    var objectNode = CommonUtils.readAndMergeJSON("device-definitions.json", OBJECT_MAPPER.createObjectNode());
    for (JsonNode vendorNode : objectNode) {
      for (JsonNode deviceNode : vendorNode.get("devices")) {
        var deviceDefinition = new DeviceDefinition();
        deviceDefinition.setVendor(deviceNode.has("vendor") ? deviceNode.get("vendor").asText() : vendorNode.asText());
        deviceDefinition.setId(ofNullable(deviceNode.get("id")).map(JsonNode::textValue).orElseThrow(
            () -> new RuntimeException("Device definition must specify id field for device: " + deviceNode)
        ));
        deviceDefinition.setImage(ofNullable(deviceNode.get("image")).map(JsonNode::textValue).orElse(null));
        deviceDefinition.setCategory(ofNullable(deviceNode.get("category")).map(JsonNode::textValue).orElse(null));
        Set<String> modelIds = ofNullable(deviceNode.get("models")).map(jsonNode -> {
          Set<String> result = new HashSet<>();
          for (JsonNode node : jsonNode) {
            result.add(node.asText());
          }
          return result;
        }).orElseThrow(() -> new RuntimeException("Device definition must specify 'models' array field for device: " + deviceNode));
        deviceDefinition.setModelId(modelIds);
        deviceDefinition.setLabel(getDefinitionMap(deviceNode, "label"));
        deviceDefinition.setDescription(getDefinitionMap(deviceNode, "description"));

        for (JsonNode endpoint : deviceNode.path("endpoints")) {
          var endpointDefinition = new EndpointDefinition();
          endpointDefinition.setId(ofNullable(endpoint.get("id")).map(JsonNode::textValue).orElse(null));
          List<Integer> inputClusters = new ArrayList<>();
          if (endpoint.has("input_clusters")) {
            for (JsonNode clusterNode : endpoint.get("input_clusters")) {
              inputClusters.add(clusterNode.asInt());
            }
          }
          endpointDefinition.setInputClusters(inputClusters);
          endpointDefinition.setOutputClusters(Collections.emptyList());
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
