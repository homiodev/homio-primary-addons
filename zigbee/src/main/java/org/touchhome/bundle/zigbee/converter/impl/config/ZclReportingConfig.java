package org.touchhome.bundle.zigbee.converter.impl.config;

import lombok.Getter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;

@Getter
public class ZclReportingConfig {

  private final Integer changeDefault;
  private final Integer changeMin;
  private final Integer changeMax;

  private int reportingTimeMin;
  private int reportingTimeMax;
  private int reportingChange;
  private int pollingPeriod;

  public ZclReportingConfig(ZigBeeEndpointEntity entity) {
    this(entity, -1, -1, -1);
  }

  public ZclReportingConfig(ZigBeeEndpointEntity entity, int changeDefault, int changeMin, int changeMax) {
    this.reportingTimeMin = entity.getReportingTimeMin();
    this.reportingTimeMax = entity.getReportingTimeMax();
    this.reportingChange = entity.getReportingChange();
    this.pollingPeriod = entity.getPollingPeriod();

    this.changeDefault = changeDefault;
    this.changeMin = changeMin;
    this.changeMax = changeMax;
  }

  public boolean updateConfiguration(ZigBeeEndpointEntity entity) {
    boolean updated = false;
    if (reportingTimeMin != entity.getReportingTimeMin()) {
      reportingTimeMin = entity.getReportingTimeMin();
      updated = true;
    }
    if (reportingTimeMax != entity.getReportingTimeMax()) {
      reportingTimeMax = entity.getReportingTimeMax();
      updated = true;
    }
    if (reportingChange != entity.getReportingChange()) {
      reportingChange = entity.getReportingChange();
      updated = true;
    }
    if (pollingPeriod != entity.getPollingPeriod()) {
      pollingPeriod = entity.getPollingPeriod();
      updated = true;
    }
    return updated;
  }
}
