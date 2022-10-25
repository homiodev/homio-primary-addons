package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r Dot11SignalStrength.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="Dot11SignalStrength">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="None"/>
 *     <enumeration value="Very Bad"/>
 *     <enumeration value="Bad"/>
 *     <enumeration value="Good"/>
 *     <enumeration value="Very Good"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "Dot11SignalStrength")
@XmlEnum
public enum Dot11SignalStrength {

  @XmlEnumValue("None")
  NONE("None"), @XmlEnumValue("Very Bad")
  VERY_BAD("Very Bad"), @XmlEnumValue("Bad")
  BAD("Bad"), @XmlEnumValue("Good")
  GOOD("Good"), @XmlEnumValue("Very Good")
  VERY_GOOD("Very Good"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  Dot11SignalStrength(String v) {
    value = v;
  }

  public static Dot11SignalStrength fromValue(String v) {
    for (Dot11SignalStrength c : Dot11SignalStrength.values()) {
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
