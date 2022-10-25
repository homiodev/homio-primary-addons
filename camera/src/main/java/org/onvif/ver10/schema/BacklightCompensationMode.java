package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BacklightCompensationMode")
@XmlEnum
public enum BacklightCompensationMode {

  /**
   * Backlight compensation is disabled.
   */
  OFF,

  /**
   * Backlight compensation is enabled.
   */
  ON;

  public static BacklightCompensationMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
