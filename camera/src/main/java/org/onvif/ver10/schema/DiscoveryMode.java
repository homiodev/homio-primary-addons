package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DiscoveryMode")
@XmlEnum
public enum DiscoveryMode {

  @XmlEnumValue("Discoverable")
  DISCOVERABLE("Discoverable"), @XmlEnumValue("NonDiscoverable")
  NON_DISCOVERABLE("NonDiscoverable");
  private final String value;

  DiscoveryMode(String v) {
    value = v;
  }

  public static DiscoveryMode fromValue(String v) {
    for (DiscoveryMode c : DiscoveryMode.values()) {
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
