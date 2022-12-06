package org.touchhome.bundle.zigbee.util;

import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;

@Getter
@Setter
@RequiredArgsConstructor
public class ClusterAttributeConfiguration extends ShareConfiguration {

  private final int attributeID;
  private final @NotNull ClusterConfiguration clusterConfiguration;

  public Integer getBindFailedPollingPeriod() {
    if (bindFailedPollingInterval != null) {
      return bindFailedPollingInterval;
    }
    return Objects.requireNonNullElse(clusterConfiguration.bindFailedPollingInterval, 60);
  }

  public int getReportMinInterval(ZigBeeEndpointEntity endpointEntity) {
    if (isReportConfigurable()) {
      return endpointEntity.getReportingTimeMin();
    }
    if (reportingTimeMin != null) {
      return reportingTimeMin;
    }
    return Objects.requireNonNullElse(clusterConfiguration.reportingTimeMin, 1);

  }

  public boolean isReportConfigurable() {
    if (this.reportConfigurable != null) {
      return this.reportConfigurable;
    }
    return Objects.requireNonNullElse(this.clusterConfiguration.reportConfigurable, false);
  }

  public int getReportMaxInterval(ZigBeeEndpointEntity endpointEntity) {
    if (isReportConfigurable()) {
      return endpointEntity.getReportingTimeMax();
    }
    if (reportingTimeMax != null) {
      return reportingTimeMax;
    }
    return Objects.requireNonNullElse(clusterConfiguration.reportingTimeMax, 7200);

  }

  public Object getReportChange(ZigBeeEndpointEntity endpointEntity) {
    if (isReportConfigurable()) {
      return endpointEntity.getReportingChange();
    }
    if (reportingChange != null) {
      return reportingChange;
    }
    if (clusterConfiguration.reportingChange != null) {
      return clusterConfiguration.reportingChange;
    }
    return null;
  }

  public Integer getFailedPollingInterval() {
    if (failedPollingInterval != null) {
      return failedPollingInterval;
    }
    return Objects.requireNonNullElse(clusterConfiguration.failedPollingInterval, 60);
  }

  public int getSuccessMaxReportInterval(ZigBeeEndpointEntity endpointEntity) {
    if (isReportConfigurable()) {
      return endpointEntity.getPollingPeriod();
    }
    if (successMaxReportInterval != null) {
      return successMaxReportInterval;
    }
    return Objects.requireNonNullElse(clusterConfiguration.successMaxReportInterval, 7200);
  }

  public boolean isDiscoverAttributes() {
    if (discoverAttributes != null) {
      return discoverAttributes;
    }
    if (clusterConfiguration.discoverAttributes != null) {
      return clusterConfiguration.discoverAttributes;
    }
    return false;
  }

  public boolean isReadAttribute() {
    if (readAttribute != null) {
      return readAttribute;
    }
    if (clusterConfiguration.readAttribute != null) {
      return clusterConfiguration.readAttribute;
    }
    return false;
  }
}
