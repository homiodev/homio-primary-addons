package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r RelayIdleState.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="RelayIdleState">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="closed"/>
 *     <enumeration value="open"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "RelayIdleState")
@XmlEnum
public enum RelayIdleState {

  @XmlEnumValue("closed")
  CLOSED("closed"), @XmlEnumValue("open")
  OPEN("open");
  private final String value;

  RelayIdleState(String v) {
    value = v;
  }

  public static RelayIdleState fromValue(String v) {
    for (RelayIdleState c : RelayIdleState.values()) {
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
