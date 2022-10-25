package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ClassType")
@XmlEnum
public enum ClassType {

  @XmlEnumValue("Animal")
  ANIMAL("Animal"), @XmlEnumValue("Face")
  FACE("Face"), @XmlEnumValue("Human")
  HUMAN("Human"), @XmlEnumValue("Vehical")
  VEHICAL("Vehical"), @XmlEnumValue("Other")
  OTHER("Other");
  private final String value;

  ClassType(String v) {
    value = v;
  }

  public static ClassType fromValue(String v) {
    for (ClassType c : ClassType.values()) {
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
