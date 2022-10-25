package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AudioEncoding")
@XmlEnum
public enum AudioEncoding {

  @XmlEnumValue("G711")
  G_711("G711"), @XmlEnumValue("G726")
  G_726("G726"), AAC("AAC");
  private final String value;

  AudioEncoding(String v) {
    value = v;
  }

  public static AudioEncoding fromValue(String v) {
    for (AudioEncoding c : AudioEncoding.values()) {
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
