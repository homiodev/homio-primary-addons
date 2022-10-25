package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r RelayMode.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="RelayMode">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Monostable"/>
 *     <enumeration value="Bistable"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "RelayMode")
@XmlEnum
public enum RelayMode {

  @XmlEnumValue("Monostable")
  MONOSTABLE("Monostable"), @XmlEnumValue("Bistable")
  BISTABLE("Bistable");
  private final String value;

  RelayMode(String v) {
    value = v;
  }

  public static RelayMode fromValue(String v) {
    for (RelayMode c : RelayMode.values()) {
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
