package org.touchhome.bundle.zigbee.util;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.cluster.ZigBeeGeneralApplication;
import org.touchhome.bundle.zigbee.model.service.ZigBeeDeviceService;
import org.touchhome.common.util.CommonUtils;

@Getter
@Setter
@RequiredArgsConstructor
public class ClusterConfiguration extends ShareConfiguration {

  private final Map<String, ClusterAttributeConfiguration> attributeConfigurations = new HashMap<>();

  // cluster handler or default 'ZigBeeGeneralApplication'
  private Class<? extends ZigBeeGeneralApplication> appClass;
  // get zigbee cluster by name
  private final ZclClusterType zclClusterType;
  // default attributes handler
  private final Class<? extends AttributeHandler> defaultAttributeHandler;
  // discovery attributes. If not - use only from configuration!!!
  private final boolean discoveryAttributes;

  // optional list of output clusters that uses in acceptEndpoint(...) method
  private Set<String> optionalOutputClusters;

  public void addAttribute(String attributeName, ClusterAttributeConfiguration attributeConfiguration) {
    attributeConfigurations.put(attributeName, attributeConfiguration);
  }

  public boolean acceptEndpoint(ZigBeeEndpoint endpoint) {
    if (endpoint.getInputCluster(zclClusterType.getId()) != null) {
      return true;
    }
    for (String outputCluster : optionalOutputClusters) {
      if (endpoint.getOutputCluster(ZclClusterType.valueOf(outputCluster).getId()) != null) {
        return true;
      }
    }
    return false;
  }

  @SneakyThrows
  public ZigBeeGeneralApplication newZigBeeApplicationInstance(ZigBeeEndpoint endpoint, ZigBeeDeviceService service) {
    Class<? extends ZigBeeGeneralApplication> appClass = this.appClass == null ? ZigBeeGeneralApplication.class : this.appClass;
    ZigBeeGeneralApplication zigBeeGeneralApplication = CommonUtils.newInstance(appClass);
    zigBeeGeneralApplication.postConstruct(zclClusterType, this, endpoint, service);

    return zigBeeGeneralApplication;
  }

  public ClusterAttributeConfiguration getAttributeConfiguration(String name) {
    ClusterAttributeConfiguration configuration = attributeConfigurations.get(name);
    return configuration == null ? new ClusterAttributeConfiguration(this, null) : configuration;
  }
}
