package org.touchhome.bundle.zigbee.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ShareConfiguration {
  private Integer reportMin;
  private Integer reportMax;
  private Integer reportChange;
  private Boolean reportConfigurable;

  private boolean analogue;
  private Double reportChangeDefault;
  private Integer reportChangeMin;
  private Integer reportChangeMax;

  private Integer failedPollingInterval;
  private Integer successMaxReportInterval;
  private Integer bindFailedPollingInterval;
}
