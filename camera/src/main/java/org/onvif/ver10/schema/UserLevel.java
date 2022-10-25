package org.onvif.ver10.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse f�r UserLevel.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 *
 * <pre>
 * <simpleType name="UserLevel">
 *   <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     <enumeration value="Administrator"/>
 *     <enumeration value="Operator"/>
 *     <enumeration value="User"/>
 *     <enumeration value="Anonymous"/>
 *     <enumeration value="Extended"/>
 *   </restriction>
 * </simpleType>
 * </pre>
 */
@XmlType(name = "UserLevel")
@XmlEnum
public enum UserLevel {

  @XmlEnumValue("Administrator")
  ADMINISTRATOR("Administrator"), @XmlEnumValue("Operator")
  OPERATOR("Operator"), @XmlEnumValue("User")
  USER("User"), @XmlEnumValue("Anonymous")
  ANONYMOUS("Anonymous"), @XmlEnumValue("Extended")
  EXTENDED("Extended");
  private final String value;

  UserLevel(String v) {
    value = v;
  }

  public static UserLevel fromValue(String v) {
    for (UserLevel c : UserLevel.values()) {
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
