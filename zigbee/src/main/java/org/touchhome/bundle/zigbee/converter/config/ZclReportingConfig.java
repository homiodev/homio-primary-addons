package org.touchhome.bundle.zigbee.converter.config;

import lombok.Getter;
import org.touchhome.bundle.zigbee.model.ZigBeeEndpointEntity;

@Getter
public class ZclReportingConfig {

  private int reportingTimeMin;
  private int reportingTimeMax;

  private double reportingChange;
  private int pollingPeriod;

  public ZclReportingConfig(ZigBeeEndpointEntity entity) {
    this.reportingTimeMin = entity.getReportingTimeMin();
    this.reportingTimeMax = entity.getReportingTimeMax();
    this.reportingChange = entity.getReportingChange();
    this.pollingPeriod = entity.getPollingPeriod();
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
    if (Double.compare(reportingChange, entity.getReportingChange()) != 0) {
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
