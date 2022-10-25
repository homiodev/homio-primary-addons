package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Direction")
@XmlEnum
public enum Direction {

  @XmlEnumValue("Left")
  LEFT("Left"), @XmlEnumValue("Right")
  RIGHT("Right"), @XmlEnumValue("Any")
  ANY("Any");
  private final String value;

  Direction(String v) {
    value = v;
  }

  public static Direction fromValue(String v) {
    for (Direction c : Direction.values()) {
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
