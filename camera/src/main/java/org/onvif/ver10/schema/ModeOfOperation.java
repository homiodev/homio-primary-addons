package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r ModeOfOperation.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="ModeOfOperation">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Idle"/>
 *     <enumeration value="Active"/>
 *     <enumeration value="Unknown"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "ModeOfOperation")
@XmlEnum
public enum ModeOfOperation {

  @XmlEnumValue("Idle")
  IDLE("Idle"), @XmlEnumValue("Active")
  ACTIVE("Active"),

  /**
   * This case should never happen.
   */
  @XmlEnumValue("Unknown")
  UNKNOWN("Unknown");
  private final String value;

  ModeOfOperation(String v) {
    value = v;
  }

  public static ModeOfOperation fromValue(String v) {
    for (ModeOfOperation c : ModeOfOperation.values()) {
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
