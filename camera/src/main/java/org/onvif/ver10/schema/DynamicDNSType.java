package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r DynamicDNSType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="DynamicDNSType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="NoUpdate"/>
 *     <enumeration value="ClientUpdates"/>
 *     <enumeration value="ServerUpdates"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "DynamicDNSType")
@XmlEnum
public enum DynamicDNSType {

  @XmlEnumValue("NoUpdate")
  NO_UPDATE("NoUpdate"), @XmlEnumValue("ClientUpdates")
  CLIENT_UPDATES("ClientUpdates"), @XmlEnumValue("ServerUpdates")
  SERVER_UPDATES("ServerUpdates");
  private final String value;

  DynamicDNSType(String v) {
    value = v;
  }

  public static DynamicDNSType fromValue(String v) {
    for (DynamicDNSType c : DynamicDNSType.values()) {
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
