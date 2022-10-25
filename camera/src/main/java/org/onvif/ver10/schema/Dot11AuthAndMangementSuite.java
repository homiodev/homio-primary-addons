package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Dot11AuthAndMangementSuite")
@XmlEnum
public enum Dot11AuthAndMangementSuite {

  @XmlEnumValue("None")
  NONE("None"), @XmlEnumValue("Dot1X")
  DOT_1_X("Dot1X"), PSK("PSK"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  Dot11AuthAndMangementSuite(String v) {
    value = v;
  }

  public static Dot11AuthAndMangementSuite fromValue(String v) {
    for (Dot11AuthAndMangementSuite c : Dot11AuthAndMangementSuite.values()) {
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
