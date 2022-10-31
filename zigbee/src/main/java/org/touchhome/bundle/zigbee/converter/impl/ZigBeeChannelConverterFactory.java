package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.zigbee.ZigBeeController;
import org.touchhome.bundle.zigbee.converter.ZigBeeBaseChannelConverter;
import org.touchhome.bundle.zigbee.requireEndpoint.DeviceDefinition.EndpointDefinition;
import org.touchhome.common.util.CommonUtils;

@Log4j2
@Component
public final class ZigBeeChannelConverterFactory {

  /**
   * Map of all zigbeeRequireEndpoints supported by the binding
   */
  private final Map<ZigBeeConverter, Class<? extends ZigBeeBaseChannelConverter>> channelMap;

  @Getter
  private final Set<Integer> allClientClusterIds = new HashSet<>();
  @Getter
  private final Set<Integer> allServerClusterIds;

  public ZigBeeChannelConverterFactory(EntityContext entityContext) {
    List<Class<? extends ZigBeeBaseChannelConverter>> converters = entityContext.getClassesWithAnnotation(ZigBeeConverter.class);

    channelMap = converters.stream().collect(Collectors
        .toMap((Function<Class, ZigBeeConverter>) aClass -> AnnotationUtils.getAnnotation(aClass, ZigBeeConverter.class), c -> c));
    this.allServerClusterIds = channelMap.keySet().stream().flatMapToInt(c -> IntStream.of(c.serverClusters()))
        .boxed().collect(Collectors.toSet());
    for (ZigBeeConverter converter : channelMap.keySet()) {
      allClientClusterIds.add(converter.clientCluster());
      for (int additionalCluster : converter.additionalClientClusters()) {
        allClientClusterIds.add(additionalCluster);
      }
    }
  }

  public Collection<ZigBeeBaseChannelConverter> createConverterEndpoint(EndpointDefinition endpointDefinition) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = createZigBeeChannels(zc ->
            zc.name().equals(endpointDefinition.getTypeId())
                && ZigBeeController.containsAny(zc, endpointDefinition.getInputCluster()),
        converter -> true);
    return fitEndpoints.values();
  }

  public Collection<ZigBeeBaseChannelConverter> getZigBeeConverterEndpoints(ZigBeeEndpoint endpoint) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = createZigBeeChannels(name -> true, converter -> converter.acceptEndpoint(endpoint));

    // Remove ON/OFF if we support LEVEL
    if (fitEndpoints.containsKey("zigbee:switch_level")) {
      fitEndpoints.remove("zigbee:switch_onoff");
    }

    // Remove LEVEL if we support COLOR
    if (fitEndpoints.containsKey("zigbee:color_color")) {
      fitEndpoints.remove("zigbee:switch_onoff");
    }

    return fitEndpoints.values();
  }

  private Map<String, ZigBeeBaseChannelConverter> createZigBeeChannels(Predicate<ZigBeeConverter> acceptConverterName,
      Predicate<ZigBeeBaseChannelConverter> acceptConverter) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = new HashMap<>();
    for (Map.Entry<ZigBeeConverter, Class<? extends ZigBeeBaseChannelConverter>> converterEntry : channelMap.entrySet()) {
      if (acceptConverterName.test(converterEntry.getKey())) {
        ZigBeeBaseChannelConverter converter = CommonUtils.newInstance(converterEntry.getValue());
        if (acceptConverter.test(converter)) {
          ZigBeeConverter annotation = converterEntry.getKey();
          converter.setAnnotation(annotation);
          fitEndpoints.put(annotation.name(), converter);
        }
      }
    }
    return fitEndpoints;
  }
}
