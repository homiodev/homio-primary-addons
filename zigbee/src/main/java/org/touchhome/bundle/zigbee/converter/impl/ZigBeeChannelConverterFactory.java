package org.touchhome.bundle.zigbee.converter.impl;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
   * List of all @ZigBeeConverter
   */
  private final List<ConverterContext> allConverters;

  @Getter
  private final Set<Integer> allClientClusterIds = new HashSet<>();
  @Getter
  private final Set<Integer> allServerClusterIds;

  public int getConverterCount() {
    return allConverters.size();
  }

  public ZigBeeChannelConverterFactory(EntityContext entityContext) {
    List<Class<? extends ZigBeeBaseChannelConverter>> converters = entityContext.getClassesWithAnnotation(ZigBeeConverter.class);

    allConverters = converters.stream().map(ConverterContext::new).collect(Collectors.toList());
    allServerClusterIds = allConverters.stream().flatMapToInt(c -> IntStream.of(c.zigBeeConverter.serverClusters()))
        .boxed().collect(Collectors.toSet());
    for (ConverterContext context : allConverters) {
      allClientClusterIds.add(context.zigBeeConverter.clientCluster());
      for (int additionalCluster : context.zigBeeConverter.additionalClientClusters()) {
        allClientClusterIds.add(additionalCluster);
      }
    }
  }

  public Collection<ZigBeeBaseChannelConverter> createConverterEndpoint(EndpointDefinition endpointDefinition) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = createZigBeeChannels(zc ->
            zc.name().equals(endpointDefinition.getTypeId())
                && ZigBeeController.containsAny(zc, endpointDefinition.getInputCluster()),
        converter -> true, () -> {
        });
    return fitEndpoints.values();
  }

  public Collection<ZigBeeBaseChannelConverter> getZigBeeConverterEndpoints(ZigBeeEndpoint endpoint, String entityID,
      EntityContext entityContext, Runnable unitDone) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = createZigBeeChannels(name -> true,
        converter -> {
          try {
            return converter.acceptEndpoint(endpoint, entityID, entityContext);
          } catch (Exception ex) {
            log.error("[{}]: Unable to evaluate acceptEndpoint for converter: {}. Endpoint: {}", entityID,
                converter.getClass().getSimpleName(), endpoint);
            return false;
          }
        }, unitDone);

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
      Predicate<ZigBeeBaseChannelConverter> acceptConverter, Runnable unitDone) {
    Map<String, ZigBeeBaseChannelConverter> fitEndpoints = new HashMap<>();
    for (ConverterContext context : allConverters) {
      if (acceptConverterName.test(context.zigBeeConverter)) {
        if (acceptConverter.test(context.converter)) {
          ZigBeeBaseChannelConverter newConverter = CommonUtils.newInstance(context.converter.getClass());
          newConverter.setAnnotation(context.zigBeeConverter);
          fitEndpoints.put(context.zigBeeConverter.name(), newConverter);
        }
      }
      unitDone.run();
    }

    return fitEndpoints;
  }

  private static class ConverterContext {

    private final ZigBeeConverter zigBeeConverter;
    private final ZigBeeBaseChannelConverter converter;

    public ConverterContext(Class<? extends ZigBeeBaseChannelConverter> converterClass) {
      zigBeeConverter = AnnotationUtils.getAnnotation(converterClass, ZigBeeConverter.class);
      converter = CommonUtils.newInstance(converterClass);
    }
  }
}
