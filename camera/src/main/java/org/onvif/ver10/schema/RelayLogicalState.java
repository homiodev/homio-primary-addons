package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r RelayLogicalState.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="RelayLogicalState">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="active"/>
 *     <enumeration value="inactive"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "RelayLogicalState")
@XmlEnum
public enum RelayLogicalState {

  @XmlEnumValue("active")
  ACTIVE("active"), @XmlEnumValue("inactive")
  INACTIVE("inactive");
  private final String value;

  RelayLogicalState(String v) {
    value = v;
  }

  public static RelayLogicalState fromValue(String v) {
    for (RelayLogicalState c : RelayLogicalState.values()) {
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
