package org.touchhome.bundle.zigbee.util;

import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.COMMISSIONING;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.DEMAND_RESPONSE_AND_LOAD_CONTROL;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.DIAGNOSTICS;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.GREEN_POWER;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.GROUPS;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.IDENTIFY;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.KEY_ESTABLISHMENT;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.MESSAGING;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.PREPAYMENT;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.PRICE;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.RSSI_LOCATION;
import static com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType.SMART_ENERGY_TUNNELING;
import static org.touchhome.common.util.CommonUtils.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.JsonNode;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeAtmosphericPressureAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeElectricalMeasurementAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeGeneralAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeMeteringAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeePowerAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeTemperatureAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeThermostatAttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.cluster.ZigBeeGeneralApplication;
import org.touchhome.bundle.zigbee.converter.impl.cluster.ZigBeeSwitchApplication;
import org.touchhome.common.util.CommonUtils;

@Log4j2
public final class ClusterConfigurations {

  @Getter
  private static final Map<Integer, ClusterConfiguration> clusterConfigurations = new HashMap<>();

  public static final Set<Integer> SKIPPED_CLIENT_CLUSTERS = Set.of(
      IDENTIFY.getId(),
      GROUPS.getId(),
      RSSI_LOCATION.getId(),
      COMMISSIONING.getId(),
      GREEN_POWER.getId(),
      PRICE.getId(),
      DEMAND_RESPONSE_AND_LOAD_CONTROL.getId(),
      MESSAGING.getId(),
      SMART_ENERGY_TUNNELING.getId(),
      PREPAYMENT.getId(),
      KEY_ESTABLISHMENT.getId(),
      DIAGNOSTICS.getId()
  );

  private static Map<String, Class<? extends ZigBeeGeneralApplication>> CLUSTER_HANDLERS =
      Stream.of(
          ZigBeeGeneralApplication.class,
          ZigBeeSwitchApplication.class).collect(Collectors.toMap(Class::getSimpleName, h -> h));

  private static Map<String, Class<? extends AttributeHandler>> ATTRIBUTE_HANDLERS =
      Stream.of(
          ZigBeeAtmosphericPressureAttributeHandler.class,
          ZigBeeElectricalMeasurementAttributeHandler.class,
          ZigBeeGeneralAttributeHandler.class,
          ZigBeeMeteringAttributeHandler.class,
          ZigBeePowerAttributeHandler.class,
          ZigBeeTemperatureAttributeHandler.class,
          ZigBeeThermostatAttributeHandler.class).collect(Collectors.toMap(Class::getSimpleName, h -> h));

  static {
    var objectNode = CommonUtils.readAndMergeJSON("cluster-configuration.json", OBJECT_MAPPER.createObjectNode());
    for (Iterator<Entry<String, JsonNode>> iterator = objectNode.fields(); iterator.hasNext(); ) {
      Entry<String, JsonNode> entry = iterator.next();

      String clusterName = entry.getKey();
      JsonNode clusterNode = entry.getValue();
      ZclClusterType zclClusterType = ZclClusterType.valueOf(clusterName);
      var clusterConfiguration = new ClusterConfiguration(zclClusterType, getAttributeHandler(clusterNode, "defaultAttributeHandler"),
          getBoolean(clusterNode, "discoveryAttributes", true));
      clusterConfiguration.setOptionalOutputClusters(getOptStringArray(clusterNode, "orOutputCluster"));

      if (clusterNode.has("app")) {
        Class<? extends ZigBeeGeneralApplication> appClass = CLUSTER_HANDLERS.get(clusterNode.get("app").asText());
        if (appClass == null) {
          throw new IllegalArgumentException("Unable to find cluster handler with id: " + clusterNode.get("app"));
        }
        clusterConfiguration.setAppClass(appClass);
      }
      assembleReport(clusterConfiguration, clusterNode);

      if (clusterNode.has("attributes")) {
        for (JsonNode attributeNode : clusterNode.get("attributes")) {
          var attributeConfiguration = new ClusterAttributeConfiguration(clusterConfiguration,
              getAttributeHandler(attributeNode, "handler"));
          if (attributeNode.has("precision")) {
            attributeNode.get("precision").asInt();
          }
          assembleReport(attributeConfiguration, attributeNode);
          clusterConfiguration.addAttribute(attributeNode.asText(), attributeConfiguration);
        }
      }
      clusterConfigurations.put(zclClusterType.getId(), clusterConfiguration);
    }
  }

  private static Set<String> getOptStringArray(JsonNode clusterNode, String key) {
    Set<String> strings = new HashSet<>();
    if (clusterNode.has(key)) {
      for (JsonNode jsonNode : clusterNode.get(key)) {
        strings.add(jsonNode.asText());
      }
    }
    return strings;
  }

  @SneakyThrows
  private static Class<? extends AttributeHandler> getAttributeHandler(JsonNode attributeNode, String key) {
    if (attributeNode.has(key)) {
      Class<? extends AttributeHandler> attributeClass = ATTRIBUTE_HANDLERS.get(attributeNode.get(key).asText());
      if (attributeClass == null) {
        throw new IllegalArgumentException("Unable to find attribute handler with id: " + attributeNode);
      }
      return attributeClass;
    }
    return null;
  }

  private static void assembleReport(ShareConfiguration configuration, JsonNode jsonNode) {
    if (jsonNode.has("report")) {
      JsonNode reportNode = jsonNode.get("report");
      configuration.setReportMin(getNumber(reportNode, "min"));
      configuration.setReportMax(getNumber(reportNode, "max"));
      configuration.setReportChange(getNumber(reportNode, "change"));
      Boolean configurable = getBoolean(reportNode, "configurable", null);
      configuration.setReportConfigurable(configurable);
    }

    if (jsonNode.has("analogue")) {
      JsonNode analogueNode = jsonNode.get("analogue");
      configuration.setAnalogue(true);
      configuration.setReportChangeMin(getNumber(analogueNode, "min"));
      configuration.setReportChangeMax(getNumber(analogueNode, "max"));
      configuration.setReportChangeDefault(getDouble(analogueNode, "default"));
    }

    if (jsonNode.has("failedPollingInterval")) {
      configuration.setFailedPollingInterval(jsonNode.get("failedPollingInterval").asInt());
    }
    if (jsonNode.has("bindFailedPollingInterval")) {
      configuration.setBindFailedPollingInterval(jsonNode.get("bindFailedPollingInterval").asInt());
    }
    if (jsonNode.has("successMaxReportInterval")) {
      configuration.setSuccessMaxReportInterval(jsonNode.get("successMaxReportInterval").asInt());
    }
  }

  private static Boolean getBoolean(JsonNode jsonNode, String key, Boolean defaultValue) {
    if (jsonNode.has(key)) {
      return jsonNode.get(key).asBoolean();
    }
    return defaultValue;
  }

  private static Integer getNumber(JsonNode jsonNode, String key) {
    if (jsonNode.has(key)) {
      return jsonNode.get(key).asInt();
    }
    return null;
  }

  private static Double getDouble(JsonNode jsonNode, String key) {
    if (jsonNode.has(key)) {
      return jsonNode.get(key).asDouble();
    }
    return null;
  }

  public static Set<Integer> getSupportedClientClusters() {
    return clusterConfigurations.keySet();
  }
}
