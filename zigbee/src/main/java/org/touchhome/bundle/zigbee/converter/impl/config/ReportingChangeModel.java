package org.touchhome.bundle.zigbee.converter.impl.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportingChangeModel {

  private final int changeDefault;
  private final int changeMin;
  private final int changeMax;
}
