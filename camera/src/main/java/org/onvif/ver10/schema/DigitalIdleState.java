package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DigitalIdleState")
@XmlEnum
public enum DigitalIdleState {

  @XmlEnumValue("closed")
  CLOSED("closed"),
  @XmlEnumValue("open")
  OPEN("open");
  private final String value;

  DigitalIdleState(String v) {
    value = v;
  }

  public static DigitalIdleState fromValue(String v) {
    for (DigitalIdleState c : DigitalIdleState.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  public String value() {
    return value;
  }

}
