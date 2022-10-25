package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r NetworkHostType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="NetworkHostType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="IPv4"/>
 *     <enumeration value="IPv6"/>
 *     <enumeration value="DNS"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "NetworkHostType")
@XmlEnum
public enum NetworkHostType {

  @XmlEnumValue("IPv4")
  I_PV_4("IPv4"), @XmlEnumValue("IPv6")
  I_PV_6("IPv6"), DNS("DNS");
  private final String value;

  NetworkHostType(String v) {
    value = v;
  }

  public static NetworkHostType fromValue(String v) {
    for (NetworkHostType c : NetworkHostType.values()) {
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
