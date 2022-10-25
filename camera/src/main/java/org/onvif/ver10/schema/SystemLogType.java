package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r SystemLogType.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="SystemLogType">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="System"/>
 *     <enumeration value="Access"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "SystemLogType")
@XmlEnum
public enum SystemLogType {

  /**
   * Indicates that a system log is requested.
   */
  @XmlEnumValue("System")
  SYSTEM("System"),

  /**
   * Indicates that a access log is requested.
   */
  @XmlEnumValue("Access")
  ACCESS("Access");
  private final String value;

  SystemLogType(String v) {
    value = v;
  }

  public static SystemLogType fromValue(String v) {
    for (SystemLogType c : SystemLogType.values()) {
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
