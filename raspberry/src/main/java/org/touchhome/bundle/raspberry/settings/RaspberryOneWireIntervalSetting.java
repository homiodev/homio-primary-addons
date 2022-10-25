package org.touchhome.bundle.raspberry.settings;

import org.touchhome.bundle.api.setting.SettingPluginSlider;

public class RaspberryOneWireIntervalSetting implements SettingPluginSlider {

  @Override
  public Integer getMin() {
    return 1;
  }

  @Override
  public Integer getMax() {
    return 120;
  }

  @Override
  public String getHeader() {
    return "S";
  }

  @Override
  public int defaultValue() {
    return 30;
  }

  @Override
  public int order() {
    return 100;
  }

  @Override
  public boolean isReverted() {
    return true;
  }
}
