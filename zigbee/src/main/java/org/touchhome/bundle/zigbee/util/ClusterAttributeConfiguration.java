package org.touchhome.bundle.zigbee.util;

import static org.touchhome.bundle.zigbee.ZigBeeConstants.POLLING_PERIOD_DEFAULT;
import static org.touchhome.bundle.zigbee.ZigBeeConstants.REPORTING_PERIOD_DEFAULT_MAX;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.EntityContextVar.VariableType;
import org.touchhome.bundle.zigbee.converter.impl.AttributeHandler;
import org.touchhome.bundle.zigbee.converter.impl.attr.ZigBeeGeneralAttributeHandler;

@Getter
@Setter
@RequiredArgsConstructor
public class ClusterAttributeConfiguration extends ShareConfiguration {

  private @Nullable Integer precision;

  private final @NotNull ClusterConfiguration clusterConfiguration;
  private final @Nullable Class<? extends AttributeHandler> attributeHandler;

  public int getMinInterval() {
    if (getReportMin() == null) {
      return clusterConfiguration.getReportMin();
    }
    return getReportMin();
  }

  public int getMaxInterval() {
    if (getReportMax() == null) {
      return clusterConfiguration.getReportMax();
    }
    return getReportMax();
  }

  public Object getReportableChange() {
    if (getReportChange() == null) {
      return clusterConfiguration.getReportChange();
    }
    return getReportChange();
  }

  public Class<? extends AttributeHandler> getAttributeHandlerClass() {
    if (attributeHandler == null) {
      if (clusterConfiguration.getDefaultAttributeHandler() == null) {
        return ZigBeeGeneralAttributeHandler.class;
      }
      return clusterConfiguration.getDefaultAttributeHandler();
    }
    return attributeHandler;
  }

  public int getReportingFailedPollingInterval() {
    if (getFailedPollingInterval() == null) {
      if (clusterConfiguration.getFailedPollingInterval() == null) {
        return POLLING_PERIOD_DEFAULT;
      }
      return clusterConfiguration.getFailedPollingInterval();
    }
    return getFailedPollingInterval();
  }

  public int getReportingSuccessMaxReportInterval() {
    if (getSuccessMaxReportInterval() == null) {
      if (clusterConfiguration.getSuccessMaxReportInterval() == null) {
        return REPORTING_PERIOD_DEFAULT_MAX;
      }
      return clusterConfiguration.getSuccessMaxReportInterval();
    }
    return getSuccessMaxReportInterval();
  }

  public VariableType getVariableType() {
    return VariableType.Float;
  }

  public Integer getBindFailedPollingPeriod() {
    if (getBindFailedPollingInterval() == null) {
      return clusterConfiguration.getBindFailedPollingInterval();
    }
    return getBindFailedPollingInterval();
  }
}
