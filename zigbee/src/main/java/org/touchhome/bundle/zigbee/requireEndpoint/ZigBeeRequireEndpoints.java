package org.touchhome.bundle.zigbee.requireEndpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.touchhome.bundle.zigbee.ZigBeeNodeDescription;
import org.touchhome.common.util.CommonUtils;

public final class ZigBeeRequireEndpoints {

  private static final ZigBeeRequireEndpoints INSTANCE;

  static {
    INSTANCE = new ZigBeeRequireEndpoints();
    for (ZigBeeRequireEndpoints file : CommonUtils.readJSON("zigBee/device-properties.json", ZigBeeRequireEndpoints.class)) {
      INSTANCE.getZigBeeRequireEndpoints().addAll(file.getZigBeeRequireEndpoints());
    }
  }

  @Getter
  @Setter
  private List<ZigBeeRequireEndpoint> zigBeeRequireEndpoints = new ArrayList<>();

  public static ZigBeeRequireEndpoints get() {
    return INSTANCE;
  }

  public String getImage(String modelId) {
    return zigBeeRequireEndpoints.stream().filter(c -> c.getModelId().equals(modelId)).map(ZigBeeRequireEndpoint::getImage).findAny().orElse(null);
  }

  public ZigBeeRequireEndpoint findByNode(ZigBeeNodeDescription zigBeeNodeDescription) {
    return this.zigBeeRequireEndpoints.stream().filter(c -> c.matchAllTypes(zigBeeNodeDescription.getChannels())).findAny().orElse(null);
  }

  public Stream<RequireEndpoint> getRequireEndpoints(String modelIdentifier) {
    return zigBeeRequireEndpoints.stream().filter(c -> c.getModelId().equals(modelIdentifier))
        .map(ZigBeeRequireEndpoint::getRequireEndpoints).filter(Objects::nonNull).flatMap(Collection::stream);
  }

  public Optional<ZigBeeRequireEndpoint> getZigBeeRequireEndpoint(String modelIdentifier) {
    return zigBeeRequireEndpoints.stream().filter(c -> c.getModelId().equals(modelIdentifier)).findAny();
  }

  public boolean isDisablePooling(String modelIdentifier) {
    return getZigBeeRequireEndpoint(modelIdentifier).map(ZigBeeRequireEndpoint::isDisablePooling).orElse(false);
  }
}
