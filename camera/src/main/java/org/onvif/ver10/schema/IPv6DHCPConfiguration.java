package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r IPv6DHCPConfiguration.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="IPv6DHCPConfiguration">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Auto"/>
 *     <enumeration value="Stateful"/>
 *     <enumeration value="Stateless"/>
 *     <enumeration value="Off"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "IPv6DHCPConfiguration")
@XmlEnum
public enum IPv6DHCPConfiguration {

  @XmlEnumValue("Auto")
  AUTO("Auto"), @XmlEnumValue("Stateful")
  STATEFUL("Stateful"), @XmlEnumValue("Stateless")
  STATELESS("Stateless"), @XmlEnumValue("Off")
  OFF("Off");
  private final String value;

  IPv6DHCPConfiguration(String v) {
    value = v;
  }

  public static IPv6DHCPConfiguration fromValue(String v) {
    for (IPv6DHCPConfiguration c : IPv6DHCPConfiguration.values()) {
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
