package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AutoFocusMode")
@XmlEnum
public enum AutoFocusMode {

  AUTO, MANUAL;

  public static AutoFocusMode fromValue(String v) {
    return valueOf(v);
  }

  public String value() {
    return name();
  }

}
