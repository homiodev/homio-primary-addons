package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Duplex.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="Duplex">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Full"/>
 *     <enumeration value="Half"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "Duplex")
@XmlEnum
public enum Duplex {

  @XmlEnumValue("Full")
  FULL("Full"), @XmlEnumValue("Half")
  HALF("Half");
  private final String value;

  Duplex(String v) {
    value = v;
  }

  public static Duplex fromValue(String v) {
    for (Duplex c : Duplex.values()) {
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
